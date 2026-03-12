package com.rizero.feature_project_select.component

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.rizero.feature_project_select.store.ProjectListStore
import com.rizero.shared_core_component.decompose.IconButtonTopBarComponent
import com.rizero.shared_core_data.model.Project
import com.rizero.shared_core_data.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ProjectSelectComponent {
    val stateFlow : StateFlow<ProjectListStore.State>
    val topBarComponent : IconButtonTopBarComponent
    val addProjectDialog : Value<ChildSlot<*, AddProjectDialogComponent>>
    val projectRegistrationError : StateFlow<ProjectRegistrationError?>

    fun refreshProjectList()
    fun openAddProjectDialog()
    fun closeAddProjectDialog()

    fun openSelectedProject(project: Project)
    fun interface Factory{
        operator fun invoke(
            session: Session,
            componentContext: ComponentContext,
            onProfileIconClick : ()-> Unit,
            onSessionExpired: (Session) -> Unit,
            onProjectSelected : (Project) -> Unit,
        ) : ProjectSelectComponent
    }

    enum class ProjectRegistrationError {
        NETWORK,
        SERVER,
        UNAUTHORIZED,
    }
}

class PreviewProjectSelectComponent(
    state : ProjectListStore.State,
    dialogComponent: AddProjectDialogComponent? = null,
    val registrationError: ProjectSelectComponent.ProjectRegistrationError? = null,
    override val topBarComponent: IconButtonTopBarComponent
) : ProjectSelectComponent{

    override val addProjectDialog: Value<ChildSlot<Any, AddProjectDialogComponent>> =
        MutableValue(ChildSlot(dialogComponent?.let {
            Child.Created(Any(),dialogComponent)
        }))
    override val projectRegistrationError: StateFlow<ProjectSelectComponent.ProjectRegistrationError?>
        get() = MutableStateFlow(registrationError)

    override val stateFlow: StateFlow<ProjectListStore.State> = MutableStateFlow(state)
    override fun refreshProjectList() = Unit
    override fun openAddProjectDialog() = Unit
    override fun closeAddProjectDialog() = Unit
    override fun openSelectedProject(project: Project) = Unit
}