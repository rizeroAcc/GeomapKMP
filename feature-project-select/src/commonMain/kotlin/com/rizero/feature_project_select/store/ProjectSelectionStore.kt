package com.rizero.feature_project_select.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.repository.ProjectRepository
import com.rizero.shared_core_data.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface ProjectSelectionStore : Store<ProjectSelectionStore.Intent, ProjectSelectionStore.State, ProjectSelectionStore.Label> {
    sealed class Label{

    }
    sealed class Intent{
        data object ReloadProjectList : Intent()
    }
    data class State(
        val isProjectListLoading : Boolean = true,
        val projectList : List<Project> = emptyList(),
        val listPlaceholderText : String?,
    )
}

class ProjectSelectionStoreFactory(
    private val storeFactory : StoreFactory,
    private val projectRepository: ProjectRepository,
    private val sessionRepository: SessionRepository,
){
    sealed class Message{
        data object ProjectListLoading : Message()
        data class ProjectListLoaded(val projects : List<Project>) : Message()
    }
    sealed class Action{
        data class LoadProjectList(val userPhone : String) : Action()
        data class ReloadProjectList(val userPhone: String) : Action()
    }
    fun create(): ProjectSelectionStore {
        return object : ProjectSelectionStore,
            Store<ProjectSelectionStore.Intent, ProjectSelectionStore.State, ProjectSelectionStore.Label>
            by storeFactory.create(
                name = "ProjectSelectionStore",
                bootstrapper = Bootstrapper(sessionRepository),
                reducer = ReducerImpl(),
                executorFactory = { Executor(sessionRepository, projectRepository) },
                initialState = ProjectSelectionStore.State(listPlaceholderText = "Loading...")
            ) {}
    }
    private class Bootstrapper(val sessionRepository: SessionRepository,) : CoroutineBootstrapper<Action>(){
        override fun invoke() {
            scope.launch {
                val userPhone = sessionRepository.getCurrentSession()?.user?.phone
                    ?: throw RuntimeException("Passed project list without authorization")
                dispatch(Action.LoadProjectList(userPhone))
            }
        }

    }
    private class Executor(
        val sessionRepository : SessionRepository,
        val projectRepository: ProjectRepository,
    ) : CoroutineExecutor<ProjectSelectionStore.Intent, Action, ProjectSelectionStore.State, Message, ProjectSelectionStore.Label>(){

        var cachedUserPhone : String? = null

        override fun executeIntent(intent: ProjectSelectionStore.Intent) {
            when(intent) {
                ProjectSelectionStore.Intent.ReloadProjectList -> {
                    dispatch(Message.ProjectListLoading)
                    scope.launch {
                        if (cachedUserPhone == null){
                            cachedUserPhone = sessionRepository.getCurrentSession()?.user?.phone
                                ?: throw RuntimeException("Passed project list without authorization")
                        }
                        val userPhone = cachedUserPhone!!
                        forward(Action.ReloadProjectList(userPhone))
                    }
                }
            }
        }

        override fun executeAction(action: Action) {
            when(action) {
                is Action.LoadProjectList -> {
                    scope.launch(Dispatchers.IO) {
                        val projects = projectRepository.getAllUserProjects(userPhone = action.userPhone)
                        withContext(Dispatchers.Main.immediate){
                            dispatch(Message.ProjectListLoaded(projects))
                        }
                    }
                }
                is Action.ReloadProjectList -> {
                    scope.launch(Dispatchers.IO) {
                        val projects = projectRepository.getAllUserProjects(userPhone = action.userPhone)
                        withContext(Dispatchers.Main.immediate){
                            dispatch(Message.ProjectListLoaded(projects))
                        }
                    }
                }
            }
        }
    }

    private class ReducerImpl() : Reducer<ProjectSelectionStore.State, Message> {
        override fun ProjectSelectionStore.State.reduce(msg: Message): ProjectSelectionStore.State {
            return when(msg) {
                is Message.ProjectListLoaded -> copy(isProjectListLoading = false, projectList = msg.projects, listPlaceholderText = null)
                Message.ProjectListLoading -> copy(isProjectListLoading = true)
            }
        }

    }
}