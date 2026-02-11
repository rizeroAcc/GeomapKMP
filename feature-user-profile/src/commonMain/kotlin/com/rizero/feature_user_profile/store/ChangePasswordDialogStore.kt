package com.rizero.feature_user_profile.store

import com.arkivanov.mvikotlin.core.store.Executor
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.feature_user_profile.store.ChangePasswordDialogStore.Intent
import com.rizero.feature_user_profile.store.ChangePasswordDialogStore.State
import com.rizero.feature_user_profile.store.ChangePasswordDialogStore.Label
interface ChangePasswordDialogStore : Store<Intent, State, Label>{
    data class State(
        val oldPassword : String = "",
        val newPassword : String = "",
        val repeatNewPassword : String = "",
        val passwordChangeInProcess : Boolean = false,
        val errorText : Any? //TODO Передавать sealed классом, UI сам решит как обработать
    )
    sealed interface Intent{
        data class ChangeOldPasswordText(val newValue : String) : Intent
        data class ChangeNewPasswordText(val newValue: String) : Intent
        data class ChangeRepeatedPasswordText(val newValue: String) : Intent
        data class PerformPasswordChange(
            val oldPassword : String,
            val newPassword : String,
            val repeatedNewPassword : String
        ) : Intent
    }
    sealed interface Label{
        class ChangeFinished() : Label
    }
}

class ChangePasswordDialogStoreFactory(
    private val storeFactory: StoreFactory
) {
    fun create() : ChangePasswordDialogStore =
        object : ChangePasswordDialogStore, Store<Intent, State, Label> by storeFactory.create(
            name = "Change password dialog store",
            initialState = State(),
            bootstrapper = null,
            executorFactory = { DefaultExecutor() },
            reducer = DefaultReducer()
        ){}

    sealed interface Action{

    }

    sealed interface Message{
        data class OldPasswordTextChanged(val newValue : String) : Message
        data class NewPasswordTextChanged(val newValue : String) : Message
        data class RepeatNewPasswordTextChanged(val newValue : String) : Message
    }

    private class DefaultExecutor : CoroutineExecutor<Intent, Action, State, Message, Label>() {
        override fun executeIntent(intent: Intent) {

        }

        override fun executeAction(action: Action) {

        }
    }

    private class DefaultReducer : Reducer<State, Message>{
        override fun State.reduce(
            msg: Message
        ): State {
            TODO("Not yet implemented")
        }

    }
}