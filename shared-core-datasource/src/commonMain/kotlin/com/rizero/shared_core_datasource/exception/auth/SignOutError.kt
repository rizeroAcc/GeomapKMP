package com.rizero.shared_core_datasource.exception.auth

sealed class SignOutError : Throwable() {
    class InternalServerError() : SignOutError()
    class SessionAlreadyNotActive : SignOutError()
    class UnexpectedServerResponse : SignOutError()
}