package com.rizero.feature_project_select.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_project_select.store.AddProjectDialogStore
import com.rizero.feature_project_select.store.AddProjectDialogStoreFactory
import com.rizero.feature_project_select.ProjectSelectorState
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.ProjectRepository
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

class DefaultAddProjectDialogComponent(
    private val projectRepository: ProjectRepository,
    private val session: Session,
    componentContext : ComponentContext,
    projectCreatedCallback : (project : Project, registrationError : ProjectRegistrationError?) -> Unit,
    storeFactory: StoreFactory = DefaultStoreFactory()
) : AddProjectDialogComponent, ComponentContext by componentContext{

    enum class ProjectRegistrationError {
        NETWORK_UNAVAILABLE,
        SERVER_ERROR,
        UNAUTHORIZED,
    }

    val scope = coroutineScope()
    val store = instanceKeeper.getStore {
        AddProjectDialogStoreFactory(
            storeFactory = storeFactory,
            projectRepository = projectRepository
        ).create(session = session)
    }

    init {
        scope.launch {
            store.labels.collect { label ->
                when(label) {
                    is AddProjectDialogStore.Label.CreatedUnregisteredProject -> {
                        when(label.cause){
                            AddProjectDialogStore.Label.CreatedUnregisteredProject.Cause.Network ->
                                projectCreatedCallback(label.project, ProjectRegistrationError.NETWORK_UNAVAILABLE)
                            AddProjectDialogStore.Label.CreatedUnregisteredProject.Cause.Server ->
                                projectCreatedCallback(label.project, ProjectRegistrationError.SERVER_ERROR)
                            AddProjectDialogStore.Label.CreatedUnregisteredProject.Cause.Unauthorized ->
                                projectCreatedCallback(label.project, ProjectRegistrationError.UNAUTHORIZED)
                        }
                    }
                    is AddProjectDialogStore.Label.ProjectCreatedAndRegistered -> {
                        projectCreatedCallback(label.project,null)
                    }
                    is AddProjectDialogStore.Label.JoinedToProject -> TODO()
                }
            }
        }
    }


    override val stateFlow = store.stateFlow(lifecycle)

    override fun onNewProjectNameChanged(name: String) {
        store.accept(AddProjectDialogStore.Intent.ChangeProjectName(name))
    }

    override fun onJoinCodeChanged(code: String) {
        store.accept(AddProjectDialogStore.Intent.ChangeJoinCode(code))
    }

    override fun onSelectorStateChanged(selectorState: ProjectSelectorState) {
        store.accept(AddProjectDialogStore.Intent.ChangeSelectorState(selectorState))
    }

    override fun createProject() {
        store.accept(AddProjectDialogStore.Intent.CreateNewProject)
    }

    @Factory
    class ComponentFactory(
        val projectRepository: ProjectRepository
    ) : AddProjectDialogComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            session: Session,
            projectCreatedCallback : (project : Project, registrationError : ProjectRegistrationError?) -> Unit,
        ) = DefaultAddProjectDialogComponent(
            projectRepository = projectRepository,
            componentContext = componentContext,
            projectCreatedCallback = projectCreatedCallback,
            session = session,
        )
    }
}