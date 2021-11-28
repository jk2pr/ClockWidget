package com.jk.mr.duo.clock

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import com.jk.mr.duo.clock.utils.AppWidgetHelper
import com.jk.mr.duo.clock.utils.PreferenceHandler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AppWidgetConfigureActivity]
 */
@AndroidEntryPoint
class AppWidget : AppWidgetProvider() {

    @Inject
    lateinit var preferenceHandler: PreferenceHandler
    @Inject
    lateinit var appWidgetHelper: AppWidgetHelper

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) appWidgetHelper.updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        super.onDeleted(context, appWidgetIds)
        preferenceHandler.deleteAllPref()
    }
}
