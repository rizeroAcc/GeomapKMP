package com.rizero.shared_core_datasource.exception.auth
sealed class SignUpError : Throwable() {
    class UserAlreadyRegistered : SignUpError()
    class InternalServerError : SignUpError()
    class UnexpectedServerError : SignUpError()
}