package com.rizero.shared_core_datasource.exception.project

sealed class RegisterProjectListError : Throwable() {
    class InternalServerError(val details : String?) : RegisterProjectListError()
    class UnexpectedServerResponse() : RegisterProjectListError()
    class Unauthorized() : RegisterProjectListError()
}