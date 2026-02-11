package com.rizero.feature_project_select.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_project_select.store.ProjectSelectionStore
import com.rizero.feature_project_select.store.ProjectSelectionStoreFactory
import com.rizero.shared_core_component.decompose.DefaultIconButtonTopBarComponent
import com.rizero.shared_core_data.repository.ProjectRepository
import com.rizero.shared_core_data.repository.SessionRepository
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

class DefaultProjectSelectComponent(
    componentContext : ComponentContext,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val sessionRepository: SessionRepository,
    private val projectRepository: ProjectRepository,
    private val addProjectDialogComponentFactory: AddProjectDialogComponent.Factory,
    private val onProfileIconClick : ()-> Unit
) : ProjectSelectComponent, ComponentContext by componentContext {

    override val topBarComponent = DefaultIconButtonTopBarComponent(
        componentContext = childContext("ProjectScreenTopBar"),
        onButtonClickedCallback = {
            onProfileIconClick()
        },
        headerText = "Проекты"
    )
    private val dialogNavigation = SlotNavigation<AddProjectDialogConfig>()

    override val addProjectDialog: Value<ChildSlot<*, AddProjectDialogComponent>> = childSlot(
        source = dialogNavigation,
        serializer = AddProjectDialogConfig.serializer(),
        handleBackButton = true,
    ){ _, childComponentContext->
        addProjectDialogComponentFactory(
            componentContext = childComponentContext
        )
    }
    val store : ProjectSelectionStore = instanceKeeper.getStore {
        ProjectSelectionStoreFactory(
            storeFactory = storeFactory,
            projectRepository = projectRepository,
            sessionRepository = sessionRepository
        ).create()
    }

    override val stateFlow = store.stateFlow(lifecycle)

    override fun refreshProjectList(){
        store.accept(ProjectSelectionStore.Intent.ReloadProjectList)
    }

    override fun openAddProjectDialog() {
        dialogNavigation.activate(AddProjectDialogConfig("AddProjectDialog"))
    }


    override fun closeAddProjectDialog() {
        dialogNavigation.dismiss()
    }

    @Serializable
    private data class AddProjectDialogConfig(
        val tag : String
    )

    @Factory
    class ComponentFactory(
        val sessionRepository: SessionRepository,
        val projectRepository: ProjectRepository,
        val addProjectDialogComponentFactory: AddProjectDialogComponent.Factory,
    ) : ProjectSelectComponent.Factory{
        override fun invoke(
            componentContext: ComponentContext,
            onProfileIconClick: () -> Unit,
        ) = DefaultProjectSelectComponent(
            componentContext = componentContext,
            sessionRepository = sessionRepository,
            projectRepository = projectRepository,
            addProjectDialogComponentFactory = addProjectDialogComponentFactory,
            onProfileIconClick = onProfileIconClick
        )
    }
}