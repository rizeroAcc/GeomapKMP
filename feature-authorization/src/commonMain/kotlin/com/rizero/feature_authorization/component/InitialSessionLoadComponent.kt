package com.rizero.feature_authorization.component

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.rizero.feature_authorization.SessionLoadStore
import com.rizero.shared_core_data.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface InitialSessionLoadComponent{
    val stateFlow : StateFlow<SessionLoadStore.State>
    val continueOfflineDialog : Value<ChildSlot<*, OfflineContinueDialogComponent>>

    fun interface Factory{
        operator fun invoke(
            componentContext : ComponentContext,
            authorizedCallback: (session: Session, refreshed: Boolean) -> Unit,
            onOfflineContinue: (oldSession: Session) -> Unit,
            navigateToAuthorization: (oldSession: Session?) -> Unit
        ) : InitialSessionLoadComponent
    }
}

class MockInitialSessionLoadComponent(
    val offlineContinueDialog : OfflineContinueDialogComponent? = null
) : InitialSessionLoadComponent{
    override val stateFlow: StateFlow<SessionLoadStore.State>
        get() = MutableStateFlow(SessionLoadStore.State(loadingStatus = SessionLoadStore.State.LoadingStatus.LoadingCachedSession))
    override val continueOfflineDialog: Value<ChildSlot<*, OfflineContinueDialogComponent>>
        get() = MutableValue(ChildSlot<Any, OfflineContinueDialogComponent>(
            offlineContinueDialog?.let { Child.Created(Unit,it) }
        ))
}