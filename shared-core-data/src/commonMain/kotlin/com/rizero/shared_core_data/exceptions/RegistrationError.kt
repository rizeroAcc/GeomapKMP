package com.rizero.shared_core_data.exceptions

sealed interface RegistrationError {
    class ConnectionError() : RegistrationError
    class ServerError() : RegistrationError
    class UserAlreadyExists(val phone : String) : RegistrationError
}