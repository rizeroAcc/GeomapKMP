package com.rizero.shared_core_data.exceptions

sealed interface ServerLoadFailure {
    class ConnectionError() : ServerLoadFailure
    class ServerError() : ServerLoadFailure
    class Unauthorized() : ServerLoadFailure
}