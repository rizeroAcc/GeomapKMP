package com.rizero.feature_project_select.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_project_select.store.AddProjectDialogStore
import com.rizero.feature_project_select.store.AddProjectDialogStoreFactory
import com.rizero.feature_project_select.ProjectSelectorState
import org.koin.core.annotation.Factory

class DefaultAddProjectDialogComponent(
    componentContext : ComponentContext,
    storeFactory: StoreFactory = DefaultStoreFactory()
) : AddProjectDialogComponent, ComponentContext by componentContext{

    val store = instanceKeeper.getStore {
        AddProjectDialogStoreFactory(storeFactory).create()
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

    @Factory
    class ComponentFactory : AddProjectDialogComponent.Factory{
        override fun invoke(componentContext: ComponentContext) = DefaultAddProjectDialogComponent(
            componentContext = componentContext
        )
    }
}