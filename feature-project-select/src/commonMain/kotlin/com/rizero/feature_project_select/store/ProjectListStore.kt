package com.rizero.feature_project_select.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.feature_project_select.store.ProjectSelectionStoreFactory.Action.*
import com.rizero.feature_project_select.store.ProjectListStore.*
import com.rizero.feature_project_select.store.ProjectSelectionStoreFactory.Message.*
import com.rizero.shared_core_data.exceptions.ProjectRegistrationError
import com.rizero.shared_core_data.exceptions.LoadUserProjectsError
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.ProjectRepository
import com.rizero.shared_core_utils.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//TODO Подумать о защите от повторного создания проектов(Создались на сервере но ответ на клиет не пришел по итогу он стянет уже зареганые и повторно зарегает свои проекты)

interface ProjectListStore : Store<Intent, State, Label> {
    sealed interface Label{
        data object SessionExpired : Label
    }
    sealed interface Intent{
        data object ReloadFromCache : Intent
        data object ReloadProjectList : Intent
    }
    data class State(
        val isProjectListLoading : Boolean = false,
        val projectList : List<Project> = emptyList(),
        val error : SyncError? = null
    ){
        sealed interface SyncError{
            class ServerError : SyncError
            class NetworkUnavailable : SyncError
            class SessionExpired : SyncError
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
        data class CachedProjectsRegistered(val projects : List<Project>) : Message()
        data class SyncFinished(val projects : List<Project>) : Message()
        data class SyncError(val error : State.SyncError) : Message()
    }
    sealed interface Action{
        data object StartLoading : Action //Marker to start
        data class LoadFromCache(val continueSync : Boolean = true) : Action
        data class RegisterUnregisteredProjects(val cachedProjects: List<Project>) : Action
        data object LoadFromServer : Action
        data class CacheReceived(val projects : List<Project>) : Action
    }
    fun create(): ProjectListStore {
        return object : ProjectListStore, Store<Intent, State, Label>
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
                initialState = State()
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
                Intent.ReloadProjectList -> {
                    forward(StartLoading)
                }
                Intent.ReloadFromCache -> {
                    forward(LoadFromCache(continueSync = false))
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action) {
                StartLoading -> {
                    dispatch(ProjectListLoading)
                    forward(LoadFromCache())

                }
                is LoadFromCache -> {
                    scope.launch {
                        val cachedProjects = projectRepository.getCachedUserProjects(userPhone = currentSession.user.phone)
                        withContext(Dispatchers.Main) {
                            dispatch(CachedProjectsLoaded(cachedProjects))
                        }
                        if(action.continueSync){
                            forward(RegisterUnregisteredProjects(cachedProjects))
                        }
                    }
                }
                is RegisterUnregisteredProjects -> {
                    val unregisteredProjects = action.cachedProjects.filter { it.serverID == null }
                    if (unregisteredProjects.isEmpty()){
                        forward(LoadFromServer)
                    }else{
                        scope.launch(Dispatchers.IO) {
                            projectRepository.registerProjects(
                                unregisteredProjects,
                                session = currentSession
                            ).fold(
                                onSuccess = { _ ->
                                    forward(LoadFromCache(continueSync = false))
                                    forward(LoadFromServer)
                                },
                                onError = { error ->
                                    when (error) {
                                        is ProjectRegistrationError.ServerError -> withContext(
                                            Dispatchers.Main
                                        ) {
                                            dispatch(SyncError(State.SyncError.ServerError()))
                                        }

                                        is ProjectRegistrationError.ConnectionError -> withContext(
                                            Dispatchers.Main
                                        ) {
                                            dispatch(SyncError(State.SyncError.NetworkUnavailable()))
                                        }

                                        is ProjectRegistrationError.Unauthorized -> withContext(
                                            Dispatchers.Main
                                        ) {
                                            publish(Label.SessionExpired)
                                            dispatch(SyncError(State.SyncError.SessionExpired()))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                LoadFromServer -> {
                    scope.launch(Dispatchers.IO) {
                        projectRepository.loadProjectsFromNetwork(session = currentSession).fold(
                            onSuccess = { loadedProjects->
                                withContext(Dispatchers.Main){
                                    forward(CacheReceived(loadedProjects))
                                }
                            },
                            onError = { error->
                                when(error){
                                    is LoadUserProjectsError.ServerError-> {
                                        withContext(Dispatchers.Main){
                                            dispatch(SyncError(State.SyncError.ServerError()))
                                        }
                                    }
                                    is LoadUserProjectsError.ConnectionError -> {
                                        withContext(Dispatchers.Main){
                                            dispatch(SyncError(State.SyncError.NetworkUnavailable()))
                                        }
                                    }
                                    is LoadUserProjectsError.Unauthorized -> withContext(Dispatchers.Main){
                                        publish(Label.SessionExpired)
                                        dispatch(SyncError(State.SyncError.SessionExpired()))
                                    }
                                }
                            }
                        )
                    }
                }
                is CacheReceived -> {
                    scope.launch(Dispatchers.IO) {
                        val syncedList = projectRepository.saveReceivedProjects(
                            serverProjects = action.projects,
                            userPhone = currentSession.user.phone
                        )
                        withContext(Dispatchers.Main){
                            dispatch(SyncFinished(syncedList))
                        }
                    }
                }
            }
        }
    }

    private class ReducerImpl() : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State {
            return when(msg) {
                ProjectListLoading -> copy(isProjectListLoading = true)
                is CachedProjectsLoaded -> copy(projectList = msg.projects)
                is CachedProjectsRegistered -> copy(projectList = msg.projects)
                is SyncError -> copy(isProjectListLoading = false, error = msg.error)
                is SyncFinished -> copy(isProjectListLoading = false, projectList = msg.projects)
            }
        }
    }
}