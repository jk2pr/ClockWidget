package com.hoppers.duoclock.search.repositories

fun Throwable.toUserMessage(): String {
    return when (this) {
        is java.net.UnknownHostException -> "No internet connection. Please check your network."
        is java.net.SocketTimeoutException -> "Connection timed out. Please try again."
        is javax.net.ssl.SSLException -> "Secure connection failed. Try again later."
        else -> "Something went wrong. Please try again."
    }
}