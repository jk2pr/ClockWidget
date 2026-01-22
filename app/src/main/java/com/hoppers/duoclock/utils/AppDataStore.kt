// AppDataStore.kt
package com.hoppers.duoclock.utils

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

private const val DATASTORE_NAME = "duoclock_store"

val Context.appDataStore by preferencesDataStore(
    name = DATASTORE_NAME
)