package com.rizero.feature_project_select.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_project_select.store.AddProjectDialogStore
import com.rizero.feature_project_select.ProjectSelectorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface AddProjectDialogComponent {
    val stateFlow : StateFlow<AddProjectDialogStore.State>
    fun onNewProjectNameChanged(name : String)
    fun onJoinCodeChanged(code : String)
    fun onSelectorStateChanged(selectorState: ProjectSelectorState)

    fun interface Factory{
        operator fun invoke(
            componentContext: ComponentContext,
        ) : AddProjectDialogComponent
    }
}

class MockAddProjectDialogComponent(state : AddProjectDialogStore.State) : AddProjectDialogComponent{
    override val stateFlow: StateFlow<AddProjectDialogStore.State> = MutableStateFlow(state)
    override fun onNewProjectNameChanged(name : String) = Unit
    override fun onJoinCodeChanged(code : String) = Unit
    override fun onSelectorStateChanged(selectorState: ProjectSelectorState) = Unit
}