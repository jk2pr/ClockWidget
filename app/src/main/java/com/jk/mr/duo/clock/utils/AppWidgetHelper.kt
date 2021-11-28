package com.jk.mr.duo.clock.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.gson.Gson
import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.utils.Constants.SET_FORMAT12HOUR
import com.jk.mr.duo.clock.utils.Constants.SET_FORMAT24HOUR
import com.jk.mr.duo.clock.utils.Constants.SET_TEXT
import com.jk.mr.duo.clock.utils.Constants.SET_TIME_ZONE
import java.util.*

class AppWidgetHelper constructor(
    val preferenceHandler: PreferenceHandler,
    private val themeHandler: ThemeHandler
) {
    internal fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val calDataJSON = preferenceHandler.getTimeZonePref(context)
        val calData = Gson().fromJson(calDataJSON, CalData::class.java)
        val selectedTimeZone = calData.currentCityTimeZoneId ?: TimeZone.getDefault().id

        val views = RemoteViews(context.packageName, R.layout.app_widget)
        themeHandler.setWidgetTheme(context, views, preferenceHandler.getThemePref())
        setUpViews(views, calData, selectedTimeZone)
        updateWidget(context, appWidgetId, views, appWidgetManager)
    }
}

private fun setUpViews(views: RemoteViews, calData: CalData, selectedTimeZone: String) {
    views.apply {
        setCharSequence(R.id.clock0, SET_FORMAT12HOUR, Utils.get12HoursFormat())
        setCharSequence(R.id.clock1, SET_FORMAT12HOUR, Utils.get12HoursFormat())
        setCharSequence(R.id.clock0, SET_FORMAT24HOUR, Utils.get24HoursFormat())
        setCharSequence(R.id.clock1, SET_FORMAT24HOUR, Utils.get24HoursFormat())
        setCharSequence(R.id.txt_timezone0, SET_TEXT, calData.displayTimeZoneCityById())
        setCharSequence(R.id.txt_timezone1, SET_TEXT, calData.displayTimeZoneCityById(selectedTimeZone))
        setString(R.id.clock0, SET_TIME_ZONE, TimeZone.getDefault().id)
        setString(R.id.clock1, SET_TIME_ZONE, TimeZone.getTimeZone(selectedTimeZone).id)
    }
}

private fun updateWidget(
    context: Context,
    appWidgetId: Int,
    views: RemoteViews,
    appWidgetManager: AppWidgetManager
) {
    val intent = Intent(context, AppWidgetConfigureActivity::class.java)
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
    views.setOnClickPendingIntent(R.id.rel_custom_time, pendingIntent)
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}
