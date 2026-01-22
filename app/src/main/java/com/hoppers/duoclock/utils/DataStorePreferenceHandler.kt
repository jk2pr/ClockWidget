package com.hoppers.duoclock.utils

// DataStorePreferenceHandler.kt

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.first
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hoppers.duoclock.dashboard.data.LocationItem
import dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val PIN_PROMPT_SHOWN = booleanPreferencesKey("pin_prompt_shown")
val CITIES_JSON = stringPreferencesKey("cities_json")
val PAGE_INDEX = intPreferencesKey("page_index")
val TICK_KEY = longPreferencesKey("tick")
val EXACT_ALARM_GUIDE_SHOWN = booleanPreferencesKey("exact_alarm_guide_shown")

class DataStorePreferenceHandler(
    private val context: Context
) {

    suspend fun loadCitiesOnce(): List<LocationItem> {
        val prefs = context.dataStore.data.first()
        val json = prefs[CITIES_JSON] ?: return emptyList()
        return decodeCities(json)
    }

    val citiesFlow: Flow<List<LocationItem>> =
        context.dataStore.data
            .map { prefs ->
                prefs[CITIES_JSON]?.let { decodeCities(it) } ?: emptyList()
            }

    suspend fun saveCities(cities: List<LocationItem>) {
        context.dataStore.edit { prefs ->
            prefs[CITIES_JSON] = encodeCities(cities)
        }
    }

    suspend fun shouldPromptForWidget(): Boolean {
        val prefs = context.appDataStore.data.first()
        return prefs[PIN_PROMPT_SHOWN] != true
    }

    suspend fun markPromptShown() {
        context.appDataStore.edit {
            it[PIN_PROMPT_SHOWN] = true
        }
    }

    suspend fun shouldShowExactAlarmGuide(): Boolean {
        val prefs = context.appDataStore.data.first()
        return prefs[EXACT_ALARM_GUIDE_SHOWN] != true
    }


}