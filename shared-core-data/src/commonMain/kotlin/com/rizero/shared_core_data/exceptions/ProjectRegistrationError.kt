package com.rizero.shared_core_data.exceptions

sealed interface ProjectRegistrationError {
    class ConnectionError() : ProjectRegistrationError
    class ServerError() : ProjectRegistrationError
    class Unauthorized() : ProjectRegistrationError
}