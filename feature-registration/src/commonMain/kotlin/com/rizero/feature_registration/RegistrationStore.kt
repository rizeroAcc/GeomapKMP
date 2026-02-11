package com.rizero.feature_registration

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface RegistrationStore : Store<RegistrationStore.Intent, RegistrationStore.State, RegistrationStore.Label> {
    data class State(
        val phone : String = "",
        val username : String = "",
        val password : String = "",
        val repeatedPassword : String = "",
        val performingRegistration : Boolean = false,
        val errorMessage: String = "",
    )

    sealed interface Intent{
        data class PhoneChanged(val newPhone : String) : Intent
        data class UsernameChanged(val newUsername : String) : Intent
        data class PasswordChanged(val newPassword : String) : Intent
        data class RepeatedPasswordChanged (val newRepeatedPassword : String) :  Intent
        data object Register : Intent
    }

    sealed interface Label{
        data object RegistrationComplete : Label
    }
}

class RegistrationStoreFactory(
    private val storeFactory: StoreFactory
){
    fun create() : RegistrationStore =
        object : RegistrationStore, Store<RegistrationStore.Intent, RegistrationStore.State, RegistrationStore.Label> by storeFactory.create(
            name = "RegistrationStore",
            initialState = RegistrationStore.State(),
            executorFactory = ::ExecutorImpl,
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
        data class ErrorOccured(val errorMessage : String) : Message()
    }

    private class ExecutorImpl() : CoroutineExecutor<RegistrationStore.Intent, Action, RegistrationStore.State, Message, RegistrationStore.Label>(){
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
                RegistrationStore.Intent.Register -> {
                    forward(Action.ValidateInput)
                }
            }
        }

        //TODO Возвращать ошибки строками плохо, пробрасывай объект и по нему читай строковый ресурс
        override fun executeAction(action: Action) {
            val state = state()
            when(action){
                is Action.RegisterNewUser -> {
                    dispatch(Message.RegistrationStarted)
                    scope.launch {
                        delay(1500)
                        publish(RegistrationStore.Label.RegistrationComplete)
                    }
                }
                Action.ValidateInput -> {
                    if (!validatePhone(state.phone)) {
                        dispatch(Message.ErrorOccured("Incorrect phone number"))
                    }
                    else if (state.username.isBlank()) {
                        dispatch(Message.ErrorOccured("Username is blank"))
                    }
                    else if (state.password.isBlank()) {
                        dispatch(Message.ErrorOccured("Password is empty"))
                    }
                    else if (state.password != state.repeatedPassword) {
                        dispatch(Message.ErrorOccured("Incorrect repeated password"))
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

        private fun validatePhone(phone : String) : Boolean{
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
                is Message.ErrorOccured -> copy(performingRegistration = false, errorMessage = msg.errorMessage)
                Message.RegistrationStarted -> copy(performingRegistration = true, errorMessage = "")
            }
        }
    }

}