package com.rizero.shared_core_utils.exceptions

import kotlinx.io.IOException

class ConnectionException(val exceptionCause : IOException) : Throwable() {
    override val message: String
        get() = "Connection to server failed. Check network status"
}