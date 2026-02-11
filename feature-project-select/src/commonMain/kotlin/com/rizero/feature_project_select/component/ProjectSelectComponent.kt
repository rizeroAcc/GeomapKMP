package com.rizero.feature_project_select.component

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.rizero.feature_project_select.store.ProjectSelectionStore
import com.rizero.shared_core_component.decompose.IconButtonTopBarComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ProjectSelectComponent {
    val stateFlow : StateFlow<ProjectSelectionStore.State>
    val topBarComponent : IconButtonTopBarComponent

    val addProjectDialog : Value<ChildSlot<*, AddProjectDialogComponent>>

    fun refreshProjectList()

    fun openAddProjectDialog()

    fun closeAddProjectDialog()
    fun interface Factory{
        operator fun invoke(
            componentContext: ComponentContext,
            onProfileIconClick : ()-> Unit
        ) : ProjectSelectComponent
    }
}

class MockProjectSelectComponent(
    state : ProjectSelectionStore.State,
    dialogComponent: AddProjectDialogComponent? = null,
    override val topBarComponent: IconButtonTopBarComponent
) : ProjectSelectComponent{

    override val addProjectDialog: Value<ChildSlot<Any, AddProjectDialogComponent>> =
        MutableValue(ChildSlot(dialogComponent?.let {
            Child.Created(Any(),dialogComponent)
        }))
    override val stateFlow: StateFlow<ProjectSelectionStore.State> = MutableStateFlow(state)
    override fun refreshProjectList() = Unit
    override fun openAddProjectDialog() = Unit
    override fun closeAddProjectDialog() = Unit
}