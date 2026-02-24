package com.rizero.feature_registration

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.shared_core_data.exceptions.RegistrationError
import com.rizero.shared_core_data.model.UserModel
import com.rizero.shared_core_data.repository.SessionRepository
import com.rizero.shared_core_utils.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface RegistrationStore : Store<RegistrationStore.Intent, RegistrationStore.State, RegistrationStore.Label> {
    data class State(
        val phone : String = "",
        val username : String = "",
        val password : String = "",
        val repeatedPassword : String = "",
        val performingRegistration : Boolean = false,
        val error: RegistrationError? = null,
    )

    sealed interface RegistrationError{
        sealed class InvalidPhoneFormat {
            object InvalidPhoneLength : RegistrationError
            object PhoneIsNotRussian : RegistrationError
        }
        sealed class InvalidPassword {
            object InvalidLength : RegistrationError
            object PasswordBlank : RegistrationError
            object RepeatedPasswordNotMatches : RegistrationError
        }
        sealed class InvalidUsername {
            object BlankUsername : RegistrationError
        }
        object UserAlreadyRegistered : RegistrationError
        object ServerError : RegistrationError
        object NetworkError : RegistrationError
    }

    sealed interface Intent{
        data class PhoneChanged(val newPhone : String) : Intent
        data class UsernameChanged(val newUsername : String) : Intent
        data class PasswordChanged(val newPassword : String) : Intent
        data class RepeatedPasswordChanged (val newRepeatedPassword : String) :  Intent
        data object PerformRegister : Intent
    }

    sealed interface Label{
        data class RegistrationComplete(val registeredUser : UserModel) : Label
    }
}

class RegistrationStoreFactory(
    private val sessionRepository: SessionRepository,
    private val storeFactory: StoreFactory
){
    fun create() : RegistrationStore =
        object : RegistrationStore, Store<RegistrationStore.Intent, RegistrationStore.State, RegistrationStore.Label> by storeFactory.create(
            name = "RegistrationStore",
            initialState = RegistrationStore.State(),
            executorFactory = { ExecutorImpl(sessionRepository) },
            reducer = ReducerImpl,
        ){}

    sealed class Action(){
        data object ValidateInput : Action()
        data class RegisterNewUser(
            val phone : String,
            val username : String,
            val password : String,
        ) : Action()
    }
    private sealed class Message(){
        data class PhoneChanged(val newPhone : String) : Message()
        data class UsernameChanged(val newUsername : String) : Message()
        data class PasswordChanged(val newPassword : String) : Message()
        data class RepeatedPasswordChanged(val newRepeatedPassword : String) : Message()
        data object RegistrationStarted : Message()
        data class ErrorOccured(val error : RegistrationStore.RegistrationError) : Message()
    }

    private class ExecutorImpl(
        val sessionRepository: SessionRepository
    ) : CoroutineExecutor<RegistrationStore.Intent, Action, RegistrationStore.State, Message, RegistrationStore.Label>(){
        override fun executeIntent(intent: RegistrationStore.Intent) {
            when(intent){
                is RegistrationStore.Intent.PasswordChanged -> {
                    dispatch(Message.PasswordChanged(intent.newPassword))
                }
                is RegistrationStore.Intent.PhoneChanged -> {
                    dispatch(Message.PhoneChanged(intent.newPhone))
                }
                is RegistrationStore.Intent.RepeatedPasswordChanged -> {
                    dispatch(Message.RepeatedPasswordChanged(intent.newRepeatedPassword))
                }
                is RegistrationStore.Intent.UsernameChanged -> {
                    dispatch(Message.UsernameChanged(intent.newUsername))
                }
                RegistrationStore.Intent.PerformRegister -> {
                    forward(Action.ValidateInput)
                }
            }
        }

        override fun executeAction(action: Action) {
            val state = state()
            when(action){
                is Action.RegisterNewUser -> {
                    dispatch(Message.RegistrationStarted)
                    scope.launch(Dispatchers.IO) {
                        sessionRepository.registerUser(
                            phone = action.phone,
                            password = action.password,
                            username = action.username).fold(
                            onSuccess = { registeredUser ->
                                withContext(Dispatchers.Main){
                                    publish(RegistrationStore.Label.RegistrationComplete(registeredUser))
                                }
                            },
                            onError = { error->
                                withContext(Dispatchers.Main){
                                    when(error){
                                        is RegistrationError.ConnectionError ->
                                            dispatch(Message.ErrorOccured(RegistrationStore.RegistrationError.NetworkError))
                                        is RegistrationError.ServerError ->
                                            dispatch(Message.ErrorOccured(RegistrationStore.RegistrationError.ServerError))
                                        is RegistrationError.UserAlreadyExists ->
                                            dispatch(Message.ErrorOccured(RegistrationStore.RegistrationError.UserAlreadyRegistered))
                                    }
                                }
                            }
                        )
                    }
                }
                Action.ValidateInput -> {
                    val validationError = validateRegistrationData(state)
                    if (validationError != null) {
                        dispatch(Message.ErrorOccured(validationError))
                    }else{
                        forward(Action.RegisterNewUser(
                            username = state.username,
                            phone = state.phone,
                            password = state.password
                        ))
                    }
                }
            }
    }

        private fun validateRegistrationData(state: RegistrationStore.State): RegistrationStore.RegistrationError? = when {
            !(state.phone.startsWith("+7") || state.phone.startsWith("8")) ->
                RegistrationStore.RegistrationError.InvalidPhoneFormat.PhoneIsNotRussian

            !validateRussiaPhoneLength(state.phone) ->
                RegistrationStore.RegistrationError.InvalidPhoneFormat.InvalidPhoneLength

            state.username.isBlank() ->
                RegistrationStore.RegistrationError.InvalidUsername.BlankUsername

            state.password.isBlank() ->
                RegistrationStore.RegistrationError.InvalidPassword.PasswordBlank

            state.password.length < 8 ->
                RegistrationStore.RegistrationError.InvalidPassword.InvalidLength

            state.password != state.repeatedPassword ->
                RegistrationStore.RegistrationError.InvalidPassword.RepeatedPasswordNotMatches

            else -> null
        }

        private fun validateRussiaPhoneLength(phone : String) : Boolean{
            if (phone.startsWith("+7")){
                return phone.length == 12
            }else if (phone.startsWith("8")){
                return phone.length == 11
            }
            return false
        }
    }

    private object ReducerImpl : Reducer<RegistrationStore.State, Message>{
        override fun RegistrationStore.State.reduce(msg: Message): RegistrationStore.State {
            return when(msg){
                is Message.PasswordChanged -> copy(password = msg.newPassword)
                is Message.PhoneChanged -> copy(phone = msg.newPhone)
                is Message.RepeatedPasswordChanged -> copy(repeatedPassword = msg.newRepeatedPassword)
                is Message.UsernameChanged -> copy(username = msg.newUsername)
                is Message.ErrorOccured -> copy(performingRegistration = false, error = msg.error)
                Message.RegistrationStarted -> copy(performingRegistration = true, error = null)
            }
        }
    }

}