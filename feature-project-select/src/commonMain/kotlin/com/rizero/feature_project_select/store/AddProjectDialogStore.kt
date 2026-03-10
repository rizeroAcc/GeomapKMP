package com.rizero.feature_project_select.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.rizero.feature_project_select.ProjectSelectorState
import com.rizero.shared_core_data.model.Project
import com.rizero.feature_project_select.store.AddProjectDialogStore.*
import com.rizero.feature_project_select.store.AddProjectDialogStore.Label.*
import com.rizero.feature_project_select.store.AddProjectDialogStoreFactory.Action.*
import com.rizero.shared_core_data.exceptions.ProjectRegistrationError
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.ProjectRepository
import com.rizero.shared_core_utils.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface AddProjectDialogStore : Store<Intent, State, Label> {
    sealed class Intent{
        data class ChangeProjectName(val name : String) : Intent()
        data class ChangeJoinCode(val code : String) : Intent()
        data class ChangeSelectorState(val selectorState : ProjectSelectorState) : Intent()
        data object CreateNewProject : Intent()
        data class JoinProject(val joinCode : String) : Intent()
    }
    sealed interface Label{
        data class ProjectCreatedAndRegistered(val project : Project) : Label
        data class CreatedUnregisteredProject(val project: Project, val cause : Cause) : Label {
            sealed interface Cause{
                object Network : Cause
                object Server : Cause
                object Unauthorized : Cause
            }
        }
        data class JoinedToProject(val project : Project) : Label
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
    val projectRepository: ProjectRepository,
){
    fun create(session: Session): AddProjectDialogStore =
        object : AddProjectDialogStore, Store<Intent, State, Label> by storeFactory.create(
            name = "AddProjectDialogStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = {
                Executor(
                    session = session,
                    projectRepository = projectRepository
                )
            },
            reducer = ReducerImp()
        ){

    }

    sealed interface Message{
        data class ProjectNameChanged(val name : String) : Message
        data class JoinCodeChanged(val code : String) : Message
        data class SelectorStateChanged(val selectorState: ProjectSelectorState) : Message
        data object LoadingStarted : Message
        data object LoadingFinished : Message

    }

    sealed interface Action{
        data class StartProjectRegistration(val projectName : String) : Action
        data class CreateLocalProject(val projectName: String) : Action
        data class RegisterCreatedProjectOnServer(val project: Project) : Action
        data class JoinToProject(val joinCode : String) : Action
    }

    class Executor(
        val session: Session,
        val projectRepository: ProjectRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){
        override fun executeIntent(intent: Intent) {
            val state = state()
            when(intent) {
                is Intent.ChangeJoinCode -> {
                    if (!state.isLoading) {
                        dispatch(Message.JoinCodeChanged(intent.code))
                    }
                }
                is Intent.ChangeProjectName -> {
                    if (!state.isLoading) {
                        dispatch(Message.ProjectNameChanged(intent.name))
                    }
                }
                is Intent.ChangeSelectorState -> {
                    if (!state.isLoading) {
                        dispatch(Message.SelectorStateChanged(intent.selectorState))
                    }
                }
                is Intent.CreateNewProject -> {
                    forward(StartProjectRegistration(state.newProjectName))
                }
                is Intent.JoinProject -> {
                    //todo
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action) {
                is StartProjectRegistration -> {
                    dispatch(Message.LoadingStarted)
                    forward(CreateLocalProject(action.projectName))
                }
                is CreateLocalProject -> {
                    scope.launch(Dispatchers.Main.immediate) {
                        val cachedProject = projectRepository.createUnregisteredProject(action.projectName, session = session)
                        forward(RegisterCreatedProjectOnServer(cachedProject))
                    }
                }
                is RegisterCreatedProjectOnServer -> {
                    scope.launch(Dispatchers.IO) {
                        projectRepository.registerProject(action.project,session).fold(
                            onSuccess = { project->
                                projectRepository.updateProjectServerID(project.id,project.serverID!!)
                                publish(ProjectCreatedAndRegistered(project))
                            },
                            onError = {  error->
                                when(error) {
                                    is ProjectRegistrationError.ConnectionError ->
                                        publish(CreatedUnregisteredProject(action.project, CreatedUnregisteredProject.Cause.Network))
                                    is ProjectRegistrationError.ServerError ->
                                        publish(CreatedUnregisteredProject(action.project, CreatedUnregisteredProject.Cause.Server))
                                    is ProjectRegistrationError.Unauthorized ->
                                        publish(CreatedUnregisteredProject(action.project, CreatedUnregisteredProject.Cause.Unauthorized))
                                }
                            }
                        )
                        withContext(Dispatchers.Main){
                            dispatch(Message.LoadingFinished)
                        }
                    }
                }
                is JoinToProject -> {
                    TODO()
                }
            }
        }
    }

    class BootstrapperImpl() : CoroutineBootstrapper<Action>(){
        override fun invoke() {}
    }

    class ReducerImp() : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State {
            return when(msg) {
                is Message.JoinCodeChanged -> copy(joinCode = msg.code)
                is Message.ProjectNameChanged -> copy(newProjectName = msg.name)
                is Message.SelectorStateChanged -> copy(projectSelectorState = msg.selectorState)
                Message.LoadingStarted -> copy(isLoading = true)
                Message.LoadingFinished -> copy(isLoading = false)
            }
        }
    }
}