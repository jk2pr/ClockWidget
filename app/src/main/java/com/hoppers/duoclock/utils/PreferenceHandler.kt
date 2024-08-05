package com.hoppers.duoclock.utils

import android.content.SharedPreferences
import com.hoppers.duoclock.dashboard.data.LocationItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class PreferenceHandler @Inject constructor(private var prefs: SharedPreferences) {

    fun saveDateData(data: List<LocationItem>) = prefs.edit()
        .putString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), Json.encodeToString(data))
        .apply()

    fun getDateData(): String? =
        prefs.getString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), "")
}
