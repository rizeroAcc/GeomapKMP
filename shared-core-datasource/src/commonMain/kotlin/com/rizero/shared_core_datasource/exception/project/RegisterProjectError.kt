package com.rizero.shared_core_datasource.exception.project

import com.rizero.shared_core_datasource.exception.auth.SignInError

sealed class RegisterProjectError : Throwable() {
    class InternalServerError(val details : String?) : RegisterProjectError()
    class UnexpectedServerResponse() : RegisterProjectError()
    class Unauthorized() : RegisterProjectError()
}