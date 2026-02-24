package com.rizero.feature_user_profile.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.create
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.feature_user_profile.store.LogOutDialogStore.Intent
import com.rizero.feature_user_profile.store.LogOutDialogStore.State
import com.rizero.feature_user_profile.store.LogOutDialogStore.Label
import com.rizero.shared_core_data.exceptions.LogOutError
import com.rizero.shared_core_data.repository.SessionRepository
import com.rizero.shared_core_utils.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface LogOutDialogStore : Store<Intent, State, Label>{
    data class State(
        val isLoading : Boolean = false,
        val suggestLocalLogOut : Boolean = false
    )
    sealed interface Intent{
        class LogOut : Intent
        class LocalLogOut : Intent
    }
    sealed interface Label{
        class LogOutComplete : Label
    }

}

class LogOutDialogStoreFactory(
    private val storeFactory: StoreFactory,
    private val sessionRepository: SessionRepository
){
    fun create() : LogOutDialogStore = object : LogOutDialogStore, Store<Intent, State, Label> by storeFactory.create(
        name = "Log out dialog store",
        initialState = State(),
        bootstrapper = null,
        executorFactory = { Executor(sessionRepository) },
        reducer = DefaultReducer()
    ){}

    private sealed interface Message {
        class LogOutStarted : Message
        class FullLogOutFailed : Message
    }
    private sealed interface Action{
        class PerformFullLogOut : Action
        class PerformLocalLogOut : Action
    }

    private class Executor(
        val sessionRepository: SessionRepository
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            when(intent){
                is Intent.LogOut -> {
                    dispatch(Message.LogOutStarted())
                    forward(Action.PerformFullLogOut())
                }
                is Intent.LocalLogOut -> {
                    dispatch(Message.LogOutStarted())
                    forward(Action.PerformLocalLogOut())
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action){
                is Action.PerformFullLogOut -> {
                    scope.launch(Dispatchers.IO) {
                        sessionRepository.logOutUser().fold(
                            onSuccess = {
                                withContext(Dispatchers.Main){
                                    publish(Label.LogOutComplete())
                                }
                            },
                            onError = { error ->
                                withContext(Dispatchers.Main){
                                    dispatch(Message.FullLogOutFailed())
                                }
                                when(error){
                                    is LogOutError.ConnectionError->{ /*todo всплывающее сообщение*/ }
                                    is LogOutError.ServerError,
                                    is LogOutError.ServerError ->{ /*todo логировать ошибку*/ }
                                }
                            }
                        )
                    }
                }
                is Action.PerformLocalLogOut -> {
                    scope.launch(Dispatchers.IO) {
                        sessionRepository.clearActiveSession()
                        withContext(Dispatchers.Main){
                            publish(Label.LogOutComplete())
                        }
                    }
                }
            }
        }
    }

    private class DefaultReducer : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State {
            return when(msg){
                is Message.FullLogOutFailed -> copy(isLoading = false, suggestLocalLogOut = true)
                is Message.LogOutStarted -> copy(isLoading = true)
            }
        }
    }
}