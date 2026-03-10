package com.rizero.shared_core_data.exceptions

sealed interface ValidateTokenError {
    class ConnectionError : ValidateTokenError
}