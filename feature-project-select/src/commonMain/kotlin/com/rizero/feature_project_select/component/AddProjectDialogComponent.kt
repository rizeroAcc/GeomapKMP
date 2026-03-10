package com.rizero.feature_project_select.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_project_select.store.AddProjectDialogStore
import com.rizero.feature_project_select.ProjectSelectorState
import com.rizero.feature_project_select.component.DefaultAddProjectDialogComponent.ProjectRegistrationError
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AddProjectDialogComponent {
    val stateFlow : StateFlow<AddProjectDialogStore.State>
    fun onNewProjectNameChanged(name : String)
    fun onJoinCodeChanged(code : String)
    fun onSelectorStateChanged(selectorState: ProjectSelectorState)

    fun createProject()

    fun interface Factory{
        operator fun invoke(
            componentContext: ComponentContext,
            session: Session,
            projectCreatedCallback : (project : Project, registrationError : ProjectRegistrationError?) -> Unit,
        ) : AddProjectDialogComponent
    }
}

class MockAddProjectDialogComponent(state : AddProjectDialogStore.State) : AddProjectDialogComponent{
    override val stateFlow: StateFlow<AddProjectDialogStore.State> = MutableStateFlow(state)
    override fun onNewProjectNameChanged(name : String) = Unit
    override fun onJoinCodeChanged(code : String) = Unit
    override fun onSelectorStateChanged(selectorState: ProjectSelectorState) = Unit
    override fun createProject() = Unit
}