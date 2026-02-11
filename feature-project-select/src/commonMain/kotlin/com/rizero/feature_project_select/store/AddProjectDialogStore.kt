package com.rizero.feature_project_select.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.rizero.feature_project_select.ProjectSelectorState
import com.rizero.shared_core_data.model.Project

interface AddProjectDialogStore
    :
    Store<AddProjectDialogStore.Intent, AddProjectDialogStore.State, AddProjectDialogStore.Label> {
    sealed class Intent{
        data class ChangeProjectName(val name : String) : Intent()
        data class ChangeJoinCode(val code : String) : Intent()
        data class ChangeSelectorState(val selectorState : ProjectSelectorState) : Intent()
        data class CreateNewProject(val projectName : String) : Intent()
        data class JoinProject(val joinCode : String) : Intent()
    }
    sealed class Label{
        data class ProjectCreated(val project : Project) : Label()
        data class JoinedToProject(val project : Project) : Label()
    }
    data class State(
        val projectSelectorState : ProjectSelectorState = ProjectSelectorState.NEW_PROJECT,
        val newProjectName : String = "",
        val joinCode : String = "",
        val isLoading : Boolean = false,
    )
}

class AddProjectDialogStoreFactory(
    val storeFactory: StoreFactory,
){
    fun create(): AddProjectDialogStore = object : AddProjectDialogStore,
        Store<AddProjectDialogStore.Intent, AddProjectDialogStore.State, AddProjectDialogStore.Label> by storeFactory.create(
            name = "AddProjectDialogStore",
            initialState = AddProjectDialogStore.State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = { Executor() },
            reducer = ReducerImp()
        ){

    }

    sealed class Message{
        data class ProjectNameChanged(val name : String) : Message()
        data class JoinCodeChanged(val code : String) : Message()
        data class SelectorStateChanged(val selectorState: ProjectSelectorState) : Message()
    }

    sealed class Action{
        data class CreateNewProject(val projectName : String) : Action()
        data class JoinToProject(val joinCode : String) : Action()
    }

    class Executor() :
        CoroutineExecutor<
                AddProjectDialogStore.Intent,
                Action,
                AddProjectDialogStore.State,
                Message,
                AddProjectDialogStore.Label>(){
        override fun executeIntent(intent: AddProjectDialogStore.Intent) {
            when(intent) {
                is AddProjectDialogStore.Intent.ChangeJoinCode -> dispatch(Message.JoinCodeChanged(intent.code))
                is AddProjectDialogStore.Intent.ChangeProjectName -> dispatch(Message.ProjectNameChanged(intent.name))
                is AddProjectDialogStore.Intent.ChangeSelectorState -> dispatch(Message.SelectorStateChanged(intent.selectorState))
                is AddProjectDialogStore.Intent.CreateNewProject -> {
                    //todo
                }
                is AddProjectDialogStore.Intent.JoinProject -> {
                    //todo
                }
            }
        }

        override fun executeAction(action: Action) {

        }
    }

    class BootstrapperImpl() : CoroutineBootstrapper<Action>(){
        override fun invoke() {}
    }

    class ReducerImp() : Reducer<AddProjectDialogStore.State, Message> {
        override fun AddProjectDialogStore.State.reduce(msg: Message): AddProjectDialogStore.State {
            return when(msg) {
                is Message.JoinCodeChanged -> copy(joinCode = msg.code)
                is Message.ProjectNameChanged -> copy(newProjectName = msg.name)
                is Message.SelectorStateChanged -> copy(projectSelectorState = msg.selectorState)
            }
        }
    }
}