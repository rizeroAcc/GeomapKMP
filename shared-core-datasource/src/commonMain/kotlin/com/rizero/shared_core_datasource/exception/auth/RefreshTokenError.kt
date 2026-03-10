package com.rizero.shared_core_datasource.exception.auth

sealed interface RefreshTokenError {
    class Unauthorized : RefreshTokenError
    class ServerError : RefreshTokenError
}