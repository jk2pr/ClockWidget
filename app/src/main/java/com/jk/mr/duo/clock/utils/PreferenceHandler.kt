package com.jk.mr.duo.clock.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.jk.mr.duo.clock.data.caldata.CalData
import java.util.*
import javax.inject.Inject

class PreferenceHandler @Inject constructor(var prefs: SharedPreferences) {

    fun saveTimeZonePref(text: String) =
        prefs.edit().putString(Constants.PREF_PREFIX_KEY, text).apply()

    fun saveThemePref(theme: String) =
        prefs.edit().putString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_INFIX_KEY), theme)
            .apply()

    fun getThemePref() =
        prefs.getString(
            Constants.PREF_PREFIX_KEY.plus(Constants.PREF_INFIX_KEY),
            Constants.THEME_BLUE
        )
            ?: Constants.THEME_BLUE

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    fun getTimeZonePref(context: Context): String {
        val titleValue = prefs.getString(Constants.PREF_PREFIX_KEY, null)
        return titleValue ?: TimeZone.getDefault().id
    }

    fun deleteAllPref() = prefs.edit().clear().apply()

    fun saveDateData(data: List<CalData>) = prefs.edit()
        .putString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), Gson().toJson(data))
        .apply()

    fun getDateData(): String? =
        prefs.getString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), "")
}
