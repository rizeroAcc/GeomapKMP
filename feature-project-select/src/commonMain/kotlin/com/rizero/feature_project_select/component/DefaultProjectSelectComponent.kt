package com.rizero.feature_project_select.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_project_select.store.ProjectListStore
import com.rizero.feature_project_select.store.ProjectSelectionStoreFactory
import com.rizero.shared_core_component.decompose.DefaultIconButtonTopBarComponent
import com.rizero.shared_core_data.model.Session
import com.rizero.shared_core_data.repository.ProjectRepository
import com.rizero.shared_core_data.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

class DefaultProjectSelectComponent(
    componentContext : ComponentContext,
    private val session: Session,
    private val storeFactory: StoreFactory = DefaultStoreFactory(),
    private val projectRepository: ProjectRepository,
    private val addProjectDialogComponentFactory: AddProjectDialogComponent.Factory,
    private val onProfileIconClick : ()-> Unit,
    private val onSessionExpired: (Session) -> Unit
) : ProjectSelectComponent, ComponentContext by componentContext {

    val scope = coroutineScope()

    init {
        scope.launch() {
            store.labels.collect { label ->
                when(label) {
                    ProjectListStore.Label.SessionExpired -> {
                        onSessionExpired(session)
                    }
                }
            }
        }
    }

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
            componentContext = childComponentContext,
            session = session,
            projectCreatedCallback = { project, error->
                closeAddProjectDialog()
                store.accept(ProjectListStore.Intent.ReloadFromCache)
                if (error != null){
                    scope.launch(Dispatchers.Main) {
                        _projectRegistrationError.update {
                            when(error) {
                                DefaultAddProjectDialogComponent.ProjectRegistrationError.NETWORK_UNAVAILABLE -> ProjectSelectComponent.ProjectRegistrationError.NETWORK
                                DefaultAddProjectDialogComponent.ProjectRegistrationError.SERVER_ERROR -> ProjectSelectComponent.ProjectRegistrationError.SERVER
                                DefaultAddProjectDialogComponent.ProjectRegistrationError.UNAUTHORIZED -> ProjectSelectComponent.ProjectRegistrationError.UNAUTHORIZED
                            }
                        }
                        delay(3000)
                        _projectRegistrationError.update { null }
                    }
                }
            }
        )
    }
    private val _projectRegistrationError : MutableStateFlow<ProjectSelectComponent.ProjectRegistrationError?> = MutableStateFlow(null)
    override val projectRegistrationError: StateFlow<ProjectSelectComponent.ProjectRegistrationError?> = _projectRegistrationError.asStateFlow()
    val store : ProjectListStore = instanceKeeper.getStore {
        ProjectSelectionStoreFactory(
            storeFactory = storeFactory,
            projectRepository = projectRepository,
            currentSession = session,
        ).create()
    }

    override val stateFlow = store.stateFlow(lifecycle)

    override fun refreshProjectList(){
        store.accept(ProjectListStore.Intent.ReloadProjectList)
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
            session: Session,
            componentContext: ComponentContext,
            onProfileIconClick: () -> Unit,
            onSessionExpired: (Session) -> Unit,
        ) = DefaultProjectSelectComponent(
            session = session,
            componentContext = componentContext,
            projectRepository = projectRepository,
            addProjectDialogComponentFactory = addProjectDialogComponentFactory,
            onProfileIconClick = onProfileIconClick,
            onSessionExpired = onSessionExpired
        )
    }
}