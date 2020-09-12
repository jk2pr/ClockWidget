package com.jk.mr.duo.clock.utils

import android.content.Context
import android.graphics.Typeface
import com.google.gson.Gson
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.di.components.AppComponent
import java.util.*


object Constants {


    const val SEPARATOR = "*"
    private const val PREFS_NAME = "com.jk.mr.dualclock.widget.AppWidget"
    private const val PREF_PREFIX_KEY = "appwidget_"
    private const val PREF_INFIX_KEY = "background_"
    private const val PREF_PLACE_KEY = "PLACE_JSON"


    const val THEME_DARK = "THEME_DARK"
    const val THEME_LIGHT = "THEME_LIGHT"
    const val THEME_RED = "THEME_RED"
    const val THEME_ORANGE = "THEME_ORANGE"
    const val THEME_YELLOW = "THEME_YELLOW"
    const val THEME_GREEN = "THEME_GREEN"
    const val THEME_BLUE = "THEME_BLUE"
    const val THEME_INDIGO = "THEME_INDIGO"

    val themeArray= arrayOf(THEME_BLUE, THEME_DARK, THEME_GREEN, THEME_INDIGO, THEME_LIGHT, THEME_ORANGE, THEME_RED, THEME_YELLOW,)

    const val TEXT_AM = "am"
    const val TEXT_PM = "pm"


    const val ACTION_ADD_CLOCK = "ACTION_ADD_CLOCK"




    const val TAG = "AppWidgetConfigure"


    lateinit var appComponent: AppComponent

    // Write the prefix to the SharedPreferences object for this widget
    internal fun saveTimeZonePref(context: Context, text: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
        prefs.putString(PREF_PREFIX_KEY, text).apply()
    }


    internal fun saveThemePref(context: Context, theme: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
        prefs.putString(PREF_PREFIX_KEY.plus(PREF_INFIX_KEY), theme).apply()
    }

    internal fun getThemePref(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(PREF_PREFIX_KEY.plus(PREF_INFIX_KEY), THEME_LIGHT)
                ?: THEME_LIGHT
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    internal fun getTimeZonePref(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        val titleValue = prefs.getString(PREF_PREFIX_KEY, null)
        return titleValue ?: TimeZone.getDefault().id
    }


    internal fun deleteAllPref(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
        prefs.clear().apply()

    }


    internal fun saveDateData(context: Context, data: List<CalData>) {

        val json = Gson().toJson(data)
        val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
        prefs.putString(PREF_PREFIX_KEY.plus(PREF_PLACE_KEY), json).apply()

    }

    internal fun getDateData(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, 0)
        return prefs.getString(PREF_PREFIX_KEY.plus(PREF_PLACE_KEY), "")
    }

    internal fun getBebasneueRegularTypeFace(activity: Context)=
        Typeface.createFromAsset(activity.assets, "fonts/bebasneue_regular.ttf")

}
