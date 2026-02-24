package com.rizero.feature_user_profile.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.rizero.feature_user_profile.store.LogOutDialogStore
import com.rizero.feature_user_profile.store.LogOutDialogStoreFactory
import com.rizero.shared_core_data.repository.SessionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

class DefaultLogOutDialogComponent(
    private val sessionRepository: SessionRepository,

    private val onCancel: () -> Unit,
    private val onLoggedOut: () -> Unit,
    componentContext: ComponentContext,
    private val storeFactory : StoreFactory =  DefaultStoreFactory()
) : LogOutDialogComponent, ComponentContext by componentContext {
    val scope = coroutineScope(Dispatchers.Main)

    init {
        scope.launch {
            labels.collect { label ->
                when(label){
                    is LogOutDialogStore.Label.LogOutComplete -> { onLoggedOut() }
                }
            }
        }
    }
    val store : LogOutDialogStore = LogOutDialogStoreFactory(
        storeFactory = storeFactory,
        sessionRepository = sessionRepository
    ).create()

    override val stateFlow: StateFlow<LogOutDialogStore.State> = store.stateFlow(lifecycle)
    override val labels: Flow<LogOutDialogStore.Label> = store.labels

    override fun cancel() { onCancel() }

    override fun performLogOut() {
        store.accept(LogOutDialogStore.Intent.LogOut())
    }

    override fun performLocalLogOut() {
        store.accept(LogOutDialogStore.Intent.LocalLogOut())
    }

    @Single
    class ComponentFactory(
        val sessionRepository: SessionRepository
    ) : LogOutDialogComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            onCancel: () -> Unit,
            onLoggedOut: () -> Unit
        ): LogOutDialogComponent {
            return DefaultLogOutDialogComponent(
                sessionRepository = sessionRepository,
                onCancel = onCancel,
                onLoggedOut = onLoggedOut,
                componentContext = componentContext
            )
        }
    }
}