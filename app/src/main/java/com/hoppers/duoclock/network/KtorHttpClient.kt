package com.hoppers.duoclock.network

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val TIME_OUT = 60_000

val ktorHttpClient = HttpClient(Android) {
    followRedirects = false
    engine {
        connectTimeout = TIME_OUT
        socketTimeout = TIME_OUT
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
            }
        )

    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v("Logger Ktor =>", message)
            }
        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
            Log.d("HTTP status:", "${response.status.value}")
        }
    }

    install(DefaultRequest) {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
    }
    // ⭐ THIS IS THE IMPORTANT PART
    HttpResponseValidator {

        handleResponseExceptionWithRequest { cause, request ->
            Log.e(
                "KtorError",
                """
                URL: ${request.url}
                Method: ${request.method}
                Exception: ${cause::class.java.simpleName}
                Message: ${cause.message}
                """.trimIndent(),
                cause // ⭐ THIS prints full stacktrace
            )
        }
    }
}
