package com.rizero.shared_core_datasource.exception.auth
sealed class SignInError : Throwable() {
    class InternalServerError(val details : String?) : SignInError()
    class InvalidCredentialsError() : SignInError()
    class UnexpectedServerResponse() : SignInError()
}