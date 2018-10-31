package com.jk.mr.duo.clock

import android.app.PendingIntent
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.jk.mr.duo.clock.utils.ViewUtils
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

            val wallpaperManager = WallpaperManager.getInstance(context)
            val wallpaperDrawable = wallpaperManager.drawable
            val bitmap = (wallpaperDrawable as BitmapDrawable).bitmap

            val colClock = ViewUtils.getClockTextColor(context, Palette.from(bitmap).generate().getVibrantColor(ContextCompat.getColor(context, android.R.color.white)))

            /*  val r = Color.red(colClock)
              val g = Color.green(colClock)
              val b = Color.blue(colClock)

              val invertedRed = 255 - 0;
              val invertedGreen = 255 - 130;
              val invertedBlue = 255 - 20;

              val finalInvertedColor = Color.rgb(invertedRed, invertedGreen, invertedBlue)*/


            val colTimeZone = ViewUtils.getClockTextTimeZoneColor(context, Palette.from(bitmap).generate().getVibrantColor(ContextCompat.getColor(context, android.R.color.white)))
            views.setTextColor(R.id.clock0, colClock)
            views.setTextColor(R.id.clock1, colClock)
            views.setTextColor(R.id.txt_timezone0, colTimeZone)
            views.setTextColor(R.id.txt_timezone1, colTimeZone)
            views.setString(R.id.clock0, "setTimeZone", TimeZone.getDefault().id)
            views.setString(R.id.clock1, "setTimeZone", TimeZone.getTimeZone(timeZone).id)


            var txt0 = TimeZone.getDefault().id
            if (txt0.contains("/"))
                txt0 = txt0.split("/")[1]

            var txt1 = TimeZone.getTimeZone(timeZone).id
            if (txt1.contains("/"))
                txt1 = txt1.split("/")[1]

            views.setCharSequence(R.id.txt_timezone0, "setText", txt0)
            views.setCharSequence(R.id.txt_timezone1, "setText", txt1)


            val intent = Intent(context, AppWidgetConfigureActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            views.setOnClickPendingIntent(R.id.clock1, pendingIntent)

            //views.setTextViewText(R.id.appwidget_text, widgetText)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

