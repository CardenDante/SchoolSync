package com.mihs.schoolsync.utils

sealed class NetworkError {
    data class AuthenticationError(val message: String) : NetworkError()
    data class NetworkConnectionError(val message: String) : NetworkError()
    data class ServerError(val message: String) : NetworkError()
    data class ValidationError(val message: String) : NetworkError()
    object UnknownError : NetworkError()
}

fun handleNetworkError(throwable: Throwable): NetworkError {
    return when (throwable) {
        is retrofit2.HttpException -> {
            when (throwable.code()) {
                401 -> NetworkError.AuthenticationError("Unauthorized. Please log in again.")
                403 -> NetworkError.AuthenticationError("Access forbidden")
                404 -> NetworkError.ServerError("Resource not found")
                500 -> NetworkError.ServerError("Internal server error")
                else -> NetworkError.ServerError("An unexpected error occurred")
            }
        }
        is java.net.ConnectException -> NetworkError.NetworkConnectionError("No internet connection")
        is java.net.SocketTimeoutException -> NetworkError.NetworkConnectionError("Connection timeout")
        else -> NetworkError.UnknownError
    }
}