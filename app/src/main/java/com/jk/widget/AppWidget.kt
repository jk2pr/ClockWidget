package com.jk.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.text.Html
import android.widget.RemoteViews
import android.widget.TextClock
import android.text.TextUtils
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.SpannableString
import java.util.*


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AppWidgetConfigureActivity]
 */
class AppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            AppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val timeZone = AppWidgetConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.app_widget)


            views.setCharSequence(R.id.timzone0,"setText", TimeZone.getDefault().displayName)
            views.setCharSequence(R.id.timzone1,"setText", TimeZone.getTimeZone(timeZone).displayName)

            views.setCharSequence(R.id.clock0,"setFormat12Hour",context.resources.getString( R.string.time_format))
            views.setCharSequence(R.id.clock1,"setFormat12Hour",context.resources.getString( R.string.time_format))



            views.setString(R.id.clock1,"setTimeZone",timeZone)

            //views.setTextViewText(R.id.appwidget_text, widgetText)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

