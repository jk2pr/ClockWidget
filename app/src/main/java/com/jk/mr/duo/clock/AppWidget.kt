package com.jk.mr.duo.clock

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.jk.mr.duo.clock.utils.Constants.SEPARATOR
import com.jk.mr.duo.clock.utils.Constants.THEME_BLUE
import com.jk.mr.duo.clock.utils.Constants.THEME_DARK
import com.jk.mr.duo.clock.utils.Constants.THEME_GREEN
import com.jk.mr.duo.clock.utils.Constants.THEME_INDIGO
import com.jk.mr.duo.clock.utils.Constants.THEME_LIGHT
import com.jk.mr.duo.clock.utils.Constants.THEME_ORANGE
import com.jk.mr.duo.clock.utils.Constants.THEME_RED
import com.jk.mr.duo.clock.utils.Constants.THEME_YELLOW
import com.jk.mr.duo.clock.utils.Constants.deleteAllPref
import com.jk.mr.duo.clock.utils.Constants.getThemePref
import com.jk.mr.duo.clock.utils.Constants.getTimeZonePref
import com.jk.mr.duo.clock.utils.Utils
import java.util.TimeZone

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AppWidgetConfigureActivity]
 */
class AppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        super.onDeleted(context, appWidgetIds)
        deleteAllPref(context)
    }

    companion object {
        private fun setThem(context: Context, views: RemoteViews, theme: String) {
            var co = ContextCompat.getColor(context, android.R.color.white) // Default White
            views.apply {
                when (theme) {
                    // Background
                    THEME_DARK -> setInt(R.id.widget_root, "setBackgroundResource", R.drawable.dark_widget_bg)
                    THEME_GREEN -> setInt(R.id.widget_root, "setBackgroundResource", R.drawable.green_widget_bg)
                    THEME_BLUE -> setInt(R.id.widget_root, "setBackgroundResource", R.drawable.blue_widget_bg)
                    THEME_INDIGO -> setInt(R.id.widget_root, "setBackgroundResource", R.drawable.indigo_widget_bg)
                    THEME_RED -> setInt(R.id.widget_root, "setBackgroundResource", R.drawable.red_widget_bg)
                    THEME_ORANGE -> setInt(R.id.widget_root, "setBackgroundResource", R.drawable.orange_widget_bg)
                    THEME_LIGHT -> {
                        setInt(R.id.widget_root, "setBackgroundResource", R.drawable.light_widget_bg)
                        co = ContextCompat.getColor(context, android.R.color.black)
                    }
                    THEME_YELLOW -> {
                        setInt(R.id.widget_root, "setBackgroundResource", R.drawable.yellow_widget_bg)
                        co = ContextCompat.getColor(context, android.R.color.black)
                    }
                }
            }
            views.apply {
                // Normal Selected
                setInt(R.id.separator, "setBackgroundColor", co)
                setTextColor(R.id.clock0, co)
                setTextColor(R.id.txt_timezone0, co)
                // Selected
                setTextColor(R.id.clock1, co)
                setTextColor(R.id.txt_timezone1, co)
            }
        }

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            var timeZone = TimeZone.getDefault().id
            getTimeZonePref(context).split(SEPARATOR).run { if (isNotEmpty() && size > 2) timeZone = this[2] }
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            setThem(context, views, getThemePref(context))

            var txt0 = StringBuilder(TimeZone.getDefault().id)
            if (txt0.contains("/")) txt0 = StringBuilder(txt0.toString().split("/")[1].replace("_", " ").trim())
            if (txt0.split(" ").size > 2) txt0 = txt0.replace(txt0.lastIndexOf(" "), txt0.lastIndexOf(" ") + 1, "\n")
            var txt1 = StringBuilder(TimeZone.getTimeZone(timeZone).id)
            if (txt1.contains("/")) txt1 = StringBuilder(txt1.toString().split("/")[1].replace("_", " ").trim())

            views.apply {
                setCharSequence(R.id.clock0, "setFormat12Hour", Utils.get12HoursFormat())
                setCharSequence(R.id.clock1, "setFormat12Hour", Utils.get12HoursFormat())
                setCharSequence(R.id.clock0, "setFormat24Hour", Utils.get24HoursFormat())
                setCharSequence(R.id.clock1, "setFormat24Hour", Utils.get24HoursFormat())
                setCharSequence(R.id.txt_timezone0, "setText", txt0)
                setCharSequence(R.id.txt_timezone1, "setText", txt1)
                setString(R.id.clock0, "setTimeZone", TimeZone.getDefault().id)
                setString(R.id.clock1, "setTimeZone", TimeZone.getTimeZone(timeZone).id)
            }

            val intent = Intent(context, AppWidgetConfigureActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.rel_custom_time, pendingIntent)
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
