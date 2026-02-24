package com.rizero.shared_core_data.exceptions

interface LogOutError {
    class ConnectionError() : LogOutError
    class ServerError() : LogOutError
    class UnexpectedResponse() : LogOutError
}