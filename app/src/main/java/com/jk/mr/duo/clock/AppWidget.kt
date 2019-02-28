package com.jk.mr.duo.clock

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.content.ContextCompat
import android.widget.RelativeLayout
import android.widget.RemoteViews
import com.jk.mr.duo.clock.receiver.AlarmReceiver
import com.jk.mr.duo.clock.services.TextClockService
import com.jk.mr.duo.clock.utils.ViewUtils
import java.text.DateFormatSymbols
import java.text.DecimalFormat
import java.util.*


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AppWidgetConfigureActivity]
 */
class AppWidget : AppWidgetProvider() {


    private var broadcastReceiver: BroadcastReceiver = AlarmReceiver()

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        context.applicationContext.registerReceiver(broadcastReceiver, IntentFilter(AlarmReceiver.CUSTOM_INTENT))

        val update = Intent(context, TextClockService::class.java)
        update.action = AlarmReceiver.CUSTOM_INTENT
        //  context.startService(update);
        TextClockService.enqueueWork(context, update)

        AlarmReceiver.setAlarm(false, context)


        /*  for (appWidgetId in appWidgetIds) {
              updateAppWidget(context, appWidgetManager, appWidgetId)
          }*/
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
            var bgColor = AppWidgetConfigureActivity.loadBgColorPref(context)
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            if (bgColor == 0)
                bgColor = ContextCompat.getColor(context, R.color.bgcolor)

            views.setInt(R.id.widget_root, "setBackgroundColor", bgColor)

            // Construct the RemoteViews object


            //  Change background color

            /*  val wallpaperManager = WallpaperManager.getInstance(context)
              val wallpaperDrawable = wallpaperManager.drawable
              val bitmap = (wallpaperDrawable as BitmapDrawable).bitmap

              val colClock = ViewUtils.getClockTextColor(context, Palette.from(bitmap).generate().getVibrantColor(ContextCompat.getColor(context, android.R.color.white)))

              *//**//*  val r = Color.red(colClock)
              val g = Color.green(colClock)
              val b = Color.blue(colClock)

              val invertedRed = 255 - 0;
              val invertedGreen = 255 - 130;
              val invertedBlue = 255 - 20;

              val finalInvertedColor = Color.rgb(invertedRed, invertedGreen, invertedBlue)*//**//*


            val colTimeZone = ViewUtils.getClockTextTimeZoneColor(context, Palette.from(bitmap).generate().getVibrantColor(ContextCompat.getColor(context, android.R.color.white)))
            views.setTextColor(R.id.clock0, colClock)
            views.setTextColor(R.id.clock1, colClock)
            views.setTextColor(R.id.txt_timezone0, colTimeZone)
            views.setTextColor(R.id.txt_timezone1, colTimeZone)*/
            //  views.setString(R.id.clock0, "setTimeZone", TimeZone.getDefault().id)
            // views.setString(R.id.clock1, "setTimeZone", TimeZone.getTimeZone(timeZone).id)
            val mFormat = DecimalFormat("00")

            //Default Clock
            val date = Calendar.getInstance()
            views.setTextViewText(R.id.hour0, mFormat.format(date.get(Calendar.HOUR)).toString())
            views.setTextViewText(R.id.minute0, ":".plus(mFormat.format(date.get(Calendar.MINUTE))))
            views.setTextViewText(R.id.am_pm0, getTimeInfix(date.get(Calendar.AM_PM)))

            views.setTextViewText(R.id.txt_day0, getFullDate(date))


// Selected Timezone

            val newDate = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
            views.setTextViewText(R.id.hour1, mFormat.format(newDate.get(Calendar.HOUR)).toString())
            views.setTextViewText(R.id.minute1, ":".plus(mFormat.format(newDate.get(Calendar.MINUTE))))
            views.setTextViewText(R.id.am_pm1, getTimeInfix(newDate.get(Calendar.AM_PM)))
            views.setTextViewText(R.id.txt_day1, getFullDate(newDate))

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
            views.setOnClickPendingIntent(R.id.rel_custom_time, pendingIntent)

            //views.setTextViewText(R.id.appwidget_text, widgetText)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }


        private fun getTimeInfix(am_pm: Int): String {

            return if (am_pm == 0) "AM" else "PM"
        }

        private fun getFullDate(cal: Calendar): String {

            val day = cal.get(Calendar.DAY_OF_WEEK)


            val weekday = DateFormatSymbols().shortWeekdays[day]

            val date = cal.get(Calendar.DATE)
            val num = cal.get(Calendar.MONTH)

            var month = "wrong"
            val dfs = DateFormatSymbols()
            val months = dfs.months
            if (num in 0..11) {
                month = months[num]
            }
            return weekday.subSequence(0, 3).toString()
                    .plus(", ")
                    .plus(date.toString())
                    .plus(" ")
                    .plus(month.subSequence(0, 3))

        }
    }
}

