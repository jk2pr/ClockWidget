package com.hoppers.duoclock.extenstions

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val currentTimeKey = stringPreferencesKey("currentTimeKey")
val currentTimeZoneKey = stringPreferencesKey("currentTimeZoneKey")
val remoteTimeKey = stringPreferencesKey("remoteTimeKey")
val remoteTimeZoneKey = stringPreferencesKey("remoteTimeZoneKey")
//val currentQuoteKey = stringPreferencesKey("calData")

fun saveMapToDataStore(context: Context, key: Preferences.Key<String>, map: Map<String, String>) {
    val jsonString = Json.encodeToString(map)
    runBlocking {
        context.dataStore.edit { preferences ->
            preferences[key] = jsonString
        }
    }
}

fun getMapFromDataStore(context: Context, key: Preferences.Key<String>): Map<String, String>? {
    return runBlocking {
        val jsonString = context.dataStore.data
            .map { preferences -> preferences[key] }
            .first()
        jsonString?.let { Json.decodeFromString(it) }
    }
}