package com.rizero.feature_authorization.component

import kotlinx.serialization.Serializable

interface OfflineContinueDialogComponent {
    fun continueOffline() : Unit
    fun tryAuthorize() : Unit
    val cause : ErrorCause
    @Serializable
    enum class ErrorCause {
        TOKEN_NOT_VALID,
        NETWORK_UNAVAILABLE,
    }
}

class MockOfflineContinueDialogComponent(val errorCause: OfflineContinueDialogComponent.ErrorCause? = null) : OfflineContinueDialogComponent{
    override fun continueOffline() = Unit

    override fun tryAuthorize() = Unit

    override val cause: OfflineContinueDialogComponent.ErrorCause
        get() = errorCause?: OfflineContinueDialogComponent.ErrorCause.NETWORK_UNAVAILABLE
}
