package com.hoppers.duoclock.utils

import com.hoppers.duoclock.dashboard.data.LocationItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val widgetJson = Json {
    ignoreUnknownKeys = true
}

fun decodeCities(json: String?): List<LocationItem> {
    if (json.isNullOrEmpty()) return emptyList()

    return try {
        widgetJson.decodeFromString(json)
    } catch (e: Exception) {
        emptyList()
    }
}
fun encodeCities(cities: List<LocationItem>): String {
    return widgetJson.encodeToString(cities)
}