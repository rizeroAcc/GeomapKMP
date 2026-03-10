package com.rizero.feature_authorization.component.impl

import com.arkivanov.decompose.ComponentContext
import com.rizero.feature_authorization.component.OfflineContinueDialogComponent
import com.rizero.shared_core_data.model.Session
import kotlinx.serialization.Serializable

class InitialLoadingOfflineContinueDialogComponent (
    componentContext: ComponentContext,
    val oldSession : Session,
    override val cause: OfflineContinueDialogComponent.ErrorCause,
    private val continueOfflineCallback : (oldUserSession : Session) -> Unit,
    private val tryAuthorizeCallback : (oldUserSession : Session) -> Unit,
): OfflineContinueDialogComponent, ComponentContext by componentContext{
    override fun continueOffline() {
        continueOfflineCallback(oldSession)
    }
    override fun tryAuthorize() {
        tryAuthorizeCallback(oldSession)
    }
}
