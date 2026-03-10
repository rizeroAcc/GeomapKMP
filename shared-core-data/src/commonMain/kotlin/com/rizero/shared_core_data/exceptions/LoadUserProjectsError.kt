package com.rizero.shared_core_data.exceptions

sealed interface LoadUserProjectsError {
    class ConnectionError() : LoadUserProjectsError
    class ServerError() : LoadUserProjectsError
    class Unauthorized() : LoadUserProjectsError
}