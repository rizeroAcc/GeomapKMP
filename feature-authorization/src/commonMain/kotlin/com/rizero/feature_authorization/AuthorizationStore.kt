package com.rizero.feature_authorization

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.feature_authorization.AuthorizationStore.Label
import com.rizero.feature_authorization.AuthorizationStore.Label.*
import com.rizero.feature_authorization.AuthorizationStoreFactory.Action.*
import com.rizero.feature_authorization.AuthorizationStoreFactory.Message.*
import com.rizero.shared_core_data.exceptions.LogInError
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.SessionRepository
import com.rizero.shared_core_data.repository.UserRepository
import com.rizero.shared_core_utils.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours

interface AuthorizationStore : Store<AuthorizationStore.Intent, AuthorizationStore.State, Label> {
    sealed class Label{
        data class SuccessfulLogIn(val session : Session) : Label()
    }

    sealed class Intent{
        data class ChangePassword(val newPassword: String) : Intent()
        data class ChangePhone(val phoneNumber : String) : Intent()
        data object Authorize : Intent()
    }

    data class State(
        val phoneNumber : String = "",
        val password : String = "",
        val error : AuthorizationError? = null,
        val authorizationInProcess : Boolean = false,
        val initialSessionCheckInProcess : Boolean = true,
    )
    sealed interface AuthorizationError{
        object InvalidCredentials : AuthorizationError
        object NetworkUnavailable : AuthorizationError
        object ServerError : AuthorizationError
        object IncorrectPhoneInput : AuthorizationError
        object IncorrectPasswordInput : AuthorizationError
    }
}

class AuthorizationStoreFactory(
    private val storeFactory: StoreFactory,
    private val sessionRepository : SessionRepository,
    private val userRepository: UserRepository
) {
    fun create(expiredSession : Session? = null): AuthorizationStore =
        object : AuthorizationStore, Store<AuthorizationStore.Intent, AuthorizationStore.State, Label> by storeFactory.create(
            name = "AuthorizationStore",
            initialState = AuthorizationStore.State(
                phoneNumber = expiredSession?.user?.phone ?: "",
                initialSessionCheckInProcess = expiredSession == null
            ),
            executorFactory = {
                ExecutorImpl(
                    sessionRepository = sessionRepository,
                    userRepository = userRepository
                )
            },
            reducer = ReducerImpl,
            bootstrapper = BootstrapperImpl()
        ) {

        }
    private sealed class Message {
        data object AuthorizationCompleted : Message()
        data object AuthorizationInProcess : Message()
        data class AuthorizationFailed(val error: AuthorizationStore.AuthorizationError) : Message()
        data class PasswordChanged(val newPassword : String) : Message()
        data class PhoneChanged(val newPhone : String) : Message()
    }
    private sealed class Action{
        data object ValidateInput : Action()
        data class SkipAuthentication(val session: Session) : Action()
        data class AuthorizeUser(
            val phone : String,
            val password : String,
        ) : Action()

        data object GetCachedSession : Action()

        data class CheckSessionValid(val session: Session) : Action()
    }
    private class BootstrapperImpl() : CoroutineBootstrapper<Action>() {
        override fun invoke() {

        }
    }
    private class ExecutorImpl(
        val sessionRepository: SessionRepository,
        val userRepository: UserRepository,
    ): CoroutineExecutor<AuthorizationStore.Intent, Action, AuthorizationStore.State, Message, Label>() {
        override fun executeIntent(intent: AuthorizationStore.Intent) {
            when(intent){
                AuthorizationStore.Intent.Authorize -> {
                    forward(ValidateInput)
                }
                is AuthorizationStore.Intent.ChangePhone -> {
                    dispatch(PhoneChanged(intent.phoneNumber))
                }
                is AuthorizationStore.Intent.ChangePassword -> {
                    dispatch(PasswordChanged(intent.newPassword))
                }
            }
        }

        override fun executeAction(action: Action) {
            val state = state()
            when(action){
                GetCachedSession -> {
                    scope.launch(Dispatchers.IO) {
                        val savedSession = sessionRepository.getCachedSession()
                        if (savedSession!=null){
                            withContext(Dispatchers.Main){
                                forward(CheckSessionValid(savedSession))
                            }
                        }
                    }
                }
                is CheckSessionValid -> {
                    //TODO Занимаюсь хуйней, надо вынести в отдельный стор и экран начальную проверку сессии иначе уже жестко нарушается SRP
                }
                is SkipAuthentication -> {
                    publish(SuccessfulLogIn(action.session))
                    dispatch(AuthorizationCompleted)
                }
                ValidateInput -> {
                    if (!validatePhone(state.phoneNumber)) {
                        dispatch(AuthorizationFailed(AuthorizationStore.AuthorizationError.IncorrectPhoneInput))
                    }else if(!validatePassword(state.password)){
                        dispatch(AuthorizationFailed(AuthorizationStore.AuthorizationError.IncorrectPasswordInput))
                    } else{
                        dispatch(AuthorizationInProcess)
                        forward(AuthorizeUser(phone = state.phoneNumber, password = state.password))
                    }
                }
                is AuthorizeUser -> {
                    scope.launch(Dispatchers.IO) {
                        val nowState = state()
                        val user = sessionRepository.logInUser(nowState.phoneNumber, nowState.password)
                        withContext(Dispatchers.Main.immediate){
                            user.fold(
                                onSuccess = { session->
                                    scope.launch(Dispatchers.IO) {
                                        userRepository.saveUser(session.user)
                                    }
                                    publish(SuccessfulLogIn(session))
                                    dispatch(AuthorizationCompleted)
                                },
                                onError = { error->
                                    when(error){
                                        is LogInError.ConnectionError ->
                                            dispatch(AuthorizationFailed(AuthorizationStore.AuthorizationError.NetworkUnavailable))
                                        is LogInError.InvalidCredentials ->
                                            dispatch(AuthorizationFailed(AuthorizationStore.AuthorizationError.InvalidCredentials))
                                        is LogInError.ServerError,
                                        is LogInError.UnexpectedResponse ->
                                            dispatch(AuthorizationFailed(AuthorizationStore.AuthorizationError.ServerError))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        private fun validatePhone(phone : String) : Boolean{
            if (phone.startsWith("+7")){
                return phone.length == 12
            }else if (phone.startsWith("8")){
                return phone.length == 11
            }
            return false
        }
        private fun validatePassword(password: String) : Boolean{
            return password.isNotBlank() && password.length >= 8
        }
    }

    private object ReducerImpl : Reducer<AuthorizationStore.State, Message> {
        override fun AuthorizationStore.State.reduce(msg: Message): AuthorizationStore.State {
            return when(msg){
                is AuthorizationFailed -> {
                    if (msg.error is AuthorizationStore.AuthorizationError.IncorrectPasswordInput){
                        copy(error = AuthorizationStore.AuthorizationError.InvalidCredentials, authorizationInProcess = false)
                    }else{
                        copy(error = msg.error, authorizationInProcess = false)
                    }
                }
                is PhoneChanged -> copy(phoneNumber = msg.newPhone)
                is PasswordChanged -> copy(password = msg.newPassword)
                AuthorizationCompleted -> copy(authorizationInProcess = false)
                AuthorizationInProcess -> copy(authorizationInProcess = true, error = null)
            }
        }
    }
}