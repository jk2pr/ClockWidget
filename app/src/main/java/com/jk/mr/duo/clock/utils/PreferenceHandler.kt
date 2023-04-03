package com.jk.mr.duo.clock.utils

import android.content.SharedPreferences
import com.google.gson.Gson
import com.jk.mr.duo.clock.data.caldata.CalData
import javax.inject.Inject

class PreferenceHandler @Inject constructor(private var prefs: SharedPreferences) {

    fun saveAppColorScheme(colorString: String) {
        prefs.edit().putString(Constants.APP_COLOR_SCHEME, colorString).apply()
    }
    fun getAppColorScheme() = prefs.getString(Constants.APP_COLOR_SCHEME, null)

    fun deleteAllPref() = prefs.edit().clear().apply()

    fun saveDateData(data: List<CalData>) = prefs.edit()
        .putString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), Gson().toJson(data))
        .apply()

    fun getDateData(): String? =
        prefs.getString(Constants.PREF_PREFIX_KEY.plus(Constants.PREF_PLACE_KEY), "")
}
