package com.jk.mr.duo.clock.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.jk.mr.duo.clock.data.caldata.CalData
import javax.inject.Inject

class PreferenceHandler @Inject constructor(private var prefs: SharedPreferences) {

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

    fun getTimeZonePref(): String? = prefs.getString(Constants.PREF_PREFIX_KEY, null)

    fun deleteAllPref() = prefs.edit().clear().apply()

    fun saveDateData(data: List<CalData>) = prefs.edit()
        .putString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), Gson().toJson(data))
        .apply()

    fun getDateData(): String? =
        prefs.getString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), "")
}
