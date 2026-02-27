package com.rizero.feature_authorization

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.feature_authorization.AuthorizationStore.Label
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
import kotlin.time.ExperimentalTime

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
        val isLoading : Boolean = false,
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
    fun create(): AuthorizationStore =
        object : AuthorizationStore, Store<AuthorizationStore.Intent, AuthorizationStore.State, Label> by storeFactory.create(
            name = "AuthorizationStore",
            initialState = AuthorizationStore.State(),
            executorFactory = {
                ExecutorImpl(
                    sessionRepository = sessionRepository,
                    userRepository = userRepository
                )
            },
            reducer = ReducerImpl,
            bootstrapper = BootstrapperImpl(sessionRepository = sessionRepository)
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
    }
    private class BootstrapperImpl(
        private val sessionRepository: SessionRepository
    ) : CoroutineBootstrapper<Action>() {
        @OptIn(ExperimentalTime::class)
        override fun invoke() {
            scope.launch {
                val savedSession = sessionRepository.getCurrentSession()
                if (savedSession != null){
                    val token = savedSession.token
                    val nowTime = Clock.System.now().toEpochMilliseconds()
                    val day = 24.hours.inWholeMilliseconds
                    if (nowTime + day < token.expireAt){
                        //token expiration more than 1 day, no need to refresh token
                        dispatch(SkipAuthentication(savedSession))
                    }else{
                        //token expiration less than 1 day, need to refresh token
                        //todo refresh token (сделать когда будет endpoint на сервере)
                    }
                }
            }
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
                                    publish(Label.SuccessfulLogIn(session))
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
                is SkipAuthentication -> {
                    publish(Label.SuccessfulLogIn(action.session))
                    dispatch(AuthorizationCompleted)
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
                        copy(error = AuthorizationStore.AuthorizationError.InvalidCredentials, isLoading = false)
                    }else{
                        copy(error = msg.error, isLoading = false)
                    }
                }
                is PhoneChanged -> copy(phoneNumber = msg.newPhone)
                is PasswordChanged -> copy(password = msg.newPassword)
                AuthorizationCompleted -> copy(isLoading = false)
                AuthorizationInProcess -> copy(isLoading = true, error = null)
            }
        }
    }
}