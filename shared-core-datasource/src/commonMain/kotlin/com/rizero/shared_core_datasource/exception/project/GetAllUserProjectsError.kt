package com.rizero.shared_core_datasource.exception.project

sealed class GetAllUserProjectsError : Throwable() {
    class InternalServerError(val details : String?) : GetAllUserProjectsError()
    class UnexpectedServerResponse() : GetAllUserProjectsError()
    class Unauthorized() : GetAllUserProjectsError()
}