package com.rizero.shared_core_data.exceptions

sealed interface RefreshSessionError {
    object NetworkUnavailable : RefreshSessionError
    object TokenExpired : RefreshSessionError
    object ServerError : RefreshSessionError
}