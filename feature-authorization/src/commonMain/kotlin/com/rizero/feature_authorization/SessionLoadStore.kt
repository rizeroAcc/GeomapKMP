package com.rizero.feature_authorization

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.feature_authorization.SessionLoadStore.*
import com.rizero.feature_authorization.SessionLoadStore.Label.*
import com.rizero.feature_authorization.SessionLoadStoreFactory.Action.*
import com.rizero.shared_core_data.exceptions.RefreshSessionError
import com.rizero.shared_core_data.exceptions.ValidateTokenError
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.SessionRepository
import com.rizero.shared_core_utils.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

interface SessionLoadStore : Store<Intent,State,Label>{
    data class State(
        val loadingStatus : LoadingStatus,
    ){
        sealed interface LoadingStatus{
            object LoadingCachedSession : LoadingStatus
            object CheckingSessionValid : LoadingStatus
            class RefreshingSession : LoadingStatus
            sealed interface LoadingFinished : LoadingStatus{
                class Success(val session: Session) : LoadingFinished
                object NoCachedSession : LoadingFinished
                object CachedSessionInvalid : LoadingFinished
                object NetworkErrorDuringValidating : LoadingFinished
                object NetworkErrorDuringRefreshing : LoadingFinished
            }
        }
    }
    sealed interface Label{
        object SessionNotFound : Label
        class SessionValid(val session: Session, val refreshed : Boolean = true) : Label
        class SessionInvalid(val session: Session, val cause : Cause) : Label {
            sealed interface Cause {
                object Expired : Cause
                object NotValidOnServer : Cause
                object NetworkCheckUnavailable : Cause
            }
        }
    }

    sealed interface Intent{

    }
}

class SessionLoadStoreFactory(
    private val storeFactory: StoreFactory,
    private val sessionRepository: SessionRepository,
){

    sealed interface Action{
        class LoadSessionFromCache() : Action
        class ValidateCachedSession(val session: Session) : Action
        class RefreshCachedSession(val session: Session) : Action
    }

    sealed interface Message{
        object LoadingFromCache : Message
        object CachedSessionNotFound : Message
        object CachedSessionExpired : Message
        object ValidatingCachedSession : Message
        object CachedSessionInvalid : Message
        object NetworkErrorOnSessionValidation : Message
        object RefreshingCachedSession : Message
        object NetworkErrorOnSessionRefresh : Message
        object SessionExpiredDuringRefresh : Message
        class SessionRefreshed(val session: Session) : Message
        class CachedSessionValid(val session: Session) : Message
    }

    fun create() : SessionLoadStore =
        object : SessionLoadStore,Store<Intent, State, Label> by storeFactory.create(
            name = "SessionLoadStoreFactory",
            initialState = State(
                loadingStatus = State.LoadingStatus.LoadingCachedSession
            ),
            bootstrapper = DefaultBootstrapper() ,
            executorFactory = { DefaultExecutor(sessionRepository) },
            reducer = DefaultReducer()
        ){

        }

    class DefaultBootstrapper() : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            dispatch(LoadSessionFromCache())
        }
    }

    class DefaultExecutor(
        val sessionRepository: SessionRepository
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){

        override fun executeAction(action: Action) {
            when(action) {
                is LoadSessionFromCache -> {
                    dispatch(Message.LoadingFromCache)
                    scope.launch(Dispatchers.Main.immediate) {
                        loadCachedSession(sessionRepository = sessionRepository,)
                    }
                }
                is ValidateCachedSession -> {
                    dispatch(Message.ValidatingCachedSession)
                    val session = action.session
                    scope.launch(Dispatchers.Main) {
                        validateCachedSession(sessionRepository,session)
                    }
                }
                is RefreshCachedSession -> {
                    val oldSession = action.session
                    dispatch(Message.RefreshingCachedSession)
                    scope.launch(Dispatchers.IO) {
                        refreshSession(sessionRepository,oldSession)
                    }
                }
            }
        }

        private suspend fun loadCachedSession(sessionRepository: SessionRepository, ){
            val cachedSession = sessionRepository.getCachedSession()
            if (cachedSession != null){
                if (sessionExpired(cachedSession)){
                    dispatch(Message.CachedSessionExpired)
                    publish(
                        SessionInvalid(
                            session = cachedSession,
                            cause = SessionInvalid.Cause.Expired
                        )
                    )
                }else{
                    forward(ValidateCachedSession(cachedSession))
                }
            }else{
                dispatch(Message.CachedSessionNotFound)
                publish(SessionNotFound)
            }
        }
        private suspend fun validateCachedSession(sessionRepository: SessionRepository, cachedSession : Session){
            sessionRepository.checkSessionValid(cachedSession).fold(
                onSuccess = { isSessionValid->
                    if (!isSessionValid){
                        dispatch(Message.CachedSessionInvalid)
                        publish(SessionInvalid(
                            session = cachedSession,
                            cause = SessionInvalid.Cause.NotValidOnServer
                        ))
                    }else{
                        if (sessionNeedRefresh(cachedSession)){
                            forward(RefreshCachedSession(cachedSession))
                        }else{
                            dispatch(Message.CachedSessionValid(cachedSession))
                            publish(SessionValid(cachedSession))
                        }
                    }
                },
                onError = { error->
                    when(error){
                        is ValidateTokenError.ConnectionError -> {
                            withContext(Dispatchers.Main){
                                dispatch(Message.NetworkErrorOnSessionValidation)
                                publish(SessionInvalid(
                                    session = cachedSession,
                                    cause = SessionInvalid.Cause.NetworkCheckUnavailable
                                ))
                            }
                        }
                    }
                }
            )
        }
        private suspend fun refreshSession(sessionRepository: SessionRepository,oldSession: Session){
            sessionRepository.refreshSession(oldSession).fold(
                onSuccess = { newSession->
                    withContext(Dispatchers.Main.immediate) {
                        dispatch(Message.SessionRefreshed(newSession))
                        publish(SessionValid(newSession))
                    }
                },
                onError = { error ->
                    when(error){
                        RefreshSessionError.NetworkUnavailable -> {
                            dispatch(Message.NetworkErrorOnSessionRefresh)
                            publish(SessionValid(oldSession, refreshed = false))
                        }
                        RefreshSessionError.TokenExpired -> {
                            withContext(Dispatchers.Main.immediate) {
                                dispatch(Message.SessionExpiredDuringRefresh)
                                publish(SessionInvalid(
                                    session = oldSession,
                                    cause = SessionInvalid.Cause.Expired
                                ))
                            }
                        }

                        RefreshSessionError.ServerError -> {
                            publish(SessionValid(
                                session = oldSession,
                                refreshed = false
                            ))
                        }
                    }
                }
            )
        }

        private fun sessionExpired(session: Session) : Boolean {
            return session.token.expireAt < Clock.System.now().toEpochMilliseconds()
        }
        private fun sessionNeedRefresh(session: Session) : Boolean {
            return session.token.expireAt < Clock.System.now().toEpochMilliseconds() + 1.days.inWholeMilliseconds
        }
    }

    class DefaultReducer() : Reducer<State, Message>{
        override fun State.reduce(msg: Message): State {
            return when(msg) {
                Message.LoadingFromCache -> copy(loadingStatus = State.LoadingStatus.LoadingCachedSession)
                Message.CachedSessionNotFound -> copy(loadingStatus = State.LoadingStatus.LoadingFinished.NoCachedSession)
                Message.CachedSessionExpired -> copy(loadingStatus = State.LoadingStatus.LoadingFinished.CachedSessionInvalid)
                Message.CachedSessionInvalid -> copy(loadingStatus = State.LoadingStatus.LoadingFinished.CachedSessionInvalid)

                Message.ValidatingCachedSession -> copy(loadingStatus = State.LoadingStatus.CheckingSessionValid)
                Message.NetworkErrorOnSessionValidation ->copy(loadingStatus = State.LoadingStatus.LoadingFinished.NetworkErrorDuringValidating)

                Message.RefreshingCachedSession -> copy(loadingStatus = State.LoadingStatus.RefreshingSession())
                Message.NetworkErrorOnSessionRefresh -> copy(loadingStatus = State.LoadingStatus.LoadingFinished.NetworkErrorDuringRefreshing)
                Message.SessionExpiredDuringRefresh -> copy(loadingStatus = State.LoadingStatus.LoadingFinished.CachedSessionInvalid)

                is Message.CachedSessionValid -> copy(loadingStatus = State.LoadingStatus.LoadingFinished.Success(msg.session))
                is Message.SessionRefreshed -> copy(loadingStatus = State.LoadingStatus.LoadingFinished.Success(msg.session))

            }
        }

    }
}