package com.rizero.shared_core_data.exceptions

sealed interface LogInError {
    class ConnectionError() : LogInError
    class ServerError() : LogInError
    class UnexpectedResponse() : LogInError
    class InvalidCredentials() : LogInError
}