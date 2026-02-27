package com.rizero.feature_project_select.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.feature_project_select.store.ProjectSelectionStoreFactory.Action.*
import com.rizero.feature_project_select.store.ProjectSelectionStore.*
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.ProjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface ProjectSelectionStore : Store<Intent, State, Label> {
    sealed class Label{

    }
    sealed class Intent{
        data object LoadProjectList : Intent()
        data object ReloadProjectList : Intent()
    }
    data class State(
        val isProjectListLoading : Boolean = true,
        val projectList : List<Project> = emptyList(),
        val listPlaceholderText : String?,
        val syncState : ProjectSyncState? = null
    ){
        sealed interface ProjectSyncState{
            class SyncStarted() : ProjectSyncState
            class ReceivedSaved(val newCachedProjects : List<Project>) : ProjectSyncState
            class InProcess(val registered : Int, val total : Int) : ProjectSyncState
            class Synced(val refreshedProjects : List<Project>) : ProjectSyncState
            class SyncError(val error : Throwable, val syncedProjects : List<Project>) : ProjectSyncState
        }
    }
}

class ProjectSelectionStoreFactory(
    private val storeFactory : StoreFactory,
    private val projectRepository: ProjectRepository,
    private val currentSession: Session,
){
    sealed class Message{
        data object ProjectListLoading : Message()
        data class CachedProjectsLoaded(val projects : List<Project>) : Message()
        data class ProjectListLoaded(val projects : List<Project>) : Message()
    }
    sealed interface Action{
        data object StartLoading : Action //Marker to start
        data object LoadFromCache : Action
        data class RegisterUnregisteredProjects(val cachedProjects: List<Project>) : Action
        data object LoadFromServer : Action
        data class CacheReceived(val projects : List<Project>) : Action
    }
    fun create(): ProjectSelectionStore {
        return object : ProjectSelectionStore, Store<Intent, State, Label>
            by storeFactory.create(
                name = "ProjectSelectionStore",
                bootstrapper = Bootstrapper(),
                reducer = ReducerImpl(),
                executorFactory = {
                    Executor(
                        currentSession = currentSession,
                        projectRepository = projectRepository
                    )
                },
                initialState = State(listPlaceholderText = "Loading...")
            ) {}
    }
    private class Bootstrapper() : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            dispatch(StartLoading)
        }
    }
    private class Executor(
        val currentSession: Session,
        val projectRepository: ProjectRepository,
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>(){



        override fun executeIntent(intent: Intent) {
            when(intent) {
                Intent.ReloadProjectList -> {}
                Intent.LoadProjectList -> {}
            }
        }

        override fun executeAction(action: Action) {
            when(action) {
                StartLoading -> {
                    dispatch(Message.ProjectListLoading)
                    forward(LoadFromCache)
                }
                LoadFromCache -> {
                    scope.launch(Dispatchers.IO) {
                        val cachedProjects = projectRepository.getCachedUserProjects(session = currentSession)
                        withContext(Dispatchers.Main) {
                            dispatch(Message.CachedProjectsLoaded(cachedProjects))
                            forward(RegisterUnregisteredProjects(cachedProjects))
                        }
                    }
                }
                is RegisterUnregisteredProjects -> {
                    val unregisteredProjects = action.cachedProjects.filter { it.serverID == null }
                    projectRepository.registerUnregisteredProjects()
                }
                LoadFromServer -> TODO()
                is CacheReceived -> TODO()
            }
        }
    }

    private class ReducerImpl() : Reducer<State, Message> {
        override fun State.reduce(msg: Message): ProjectSelectionStore.State {
            return when(msg) {
                is Message.ProjectListLoaded -> copy(isProjectListLoading = false, projectList = msg.projects, listPlaceholderText = null)
                Message.ProjectListLoading -> copy(isProjectListLoading = true)
            }
        }

    }
}