package com.rizero.feature_user_profile.component

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_user_profile.store.LogOutDialogStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface LogOutDialogComponent {
    val stateFlow : StateFlow<LogOutDialogStore.State>
    val labels : Flow<LogOutDialogStore.Label>
    fun cancel()
    fun performLogOut()
    fun performLocalLogOut()

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onCancel : () -> Unit,
            onLoggedOut : () -> Unit,
        ) : LogOutDialogComponent
    }
}

class MockkLogOutDialogComponent : LogOutDialogComponent {
    override val stateFlow: StateFlow<LogOutDialogStore.State>
        get() = MutableStateFlow(LogOutDialogStore.State())
    override val labels: Flow<LogOutDialogStore.Label>
        get() = MutableSharedFlow()
    override fun cancel() = Unit
    override fun performLogOut() = Unit
    override fun performLocalLogOut() = Unit
}