package com.jk.mr.duo.clock

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.jk.mr.duo.clock.AppWidgetConfigureActivity.Companion.TEXT_AM
import com.jk.mr.duo.clock.AppWidgetConfigureActivity.Companion.TEXT_PM
import com.jk.mr.duo.clock.utils.Utils
import java.text.DateFormatSymbols
import java.text.DecimalFormat
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

        private fun setThem(context: Context, views: RemoteViews, theme: String) {
            // Already dark
            var co = ContextCompat.getColor(context, android.R.color.white)
            when (theme) {
                AppWidgetConfigureActivity.THEME_DARK -> //Background
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.dark_widget_bg)
                //Textcolor
                //  co = ContextCompat.getColor(context, android.R.color.darker_gray)
                // LinearLayout(context).setBackgroundResource(R.drawable.widget_bg)
                AppWidgetConfigureActivity.THEME_LIGHT -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.light_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.black)
                }
                AppWidgetConfigureActivity.THEME_RED -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.red_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                AppWidgetConfigureActivity.THEME_ORANGE -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.orange_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                AppWidgetConfigureActivity.THEME_YELLOW -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.yellow_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.black)
                }
                AppWidgetConfigureActivity.THEME_GREEN -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.green_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                AppWidgetConfigureActivity.THEME_BLUE -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.blue_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                AppWidgetConfigureActivity.THEME_INDIGO -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.indigo_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }

                //Default

                //Selected
            }

            //Default
           /* views.setTextColor(R.id.hour0, co)
            views.setTextColor(R.id.minute0, co)
            views.setTextColor(R.id.am_pm0, co)
            views.setTextColor(R.id.txt_day0, co)*/
       //     views.setInt(R.id.separator, "setBackgroundColor", co)
            views.setTextColor(R.id.clock0, co)
            views.setTextColor(R.id.txt_timezone0, co)

            //Selected

          /*  views.setTextColor(R.id.hour1, co)
            views.setTextColor(R.id.minute1, co)
            views.setTextColor(R.id.am_pm1, co)
            views.setTextColor(R.id.txt_day1, co)*/
            views.setTextColor(R.id.clock1, co)
            views.setTextColor(R.id.txt_timezone1, co)


        }

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {
            val timeZone = AppWidgetConfigureActivity.loadTitlePref(context,appWidgetId)
            val theme = AppWidgetConfigureActivity.loadBgColorPref(context,appWidgetId)
            val views = RemoteViews(context.packageName, R.layout.app_widget)
            //  if (bgColor == 0)
            //    bgColor = ContextCompat.getColor(context, R.color.bgcolor)
            setThem(context, views, theme)


            //    views.setInt(R.id.widget_root, "setBackgroundColor", bgColor)

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



            views.setCharSequence (R.id.clock0,"setFormat12Hour", Utils.get12HoursFormat())
            views.setCharSequence (R.id.clock1,"setFormat12Hour", Utils.get12HoursFormat())


            views.setCharSequence (R.id.clock0,"setFormat24Hour", Utils.get24HoursFormat())
            views.setCharSequence (R.id.clock1,"setFormat24Hour", Utils.get24HoursFormat())

            views.setString(R.id.clock0, "setTimeZone", TimeZone.getDefault().id)
            views.setString(R.id.clock1, "setTimeZone", TimeZone.getTimeZone(timeZone).id)
          /*  val date = Calendar.getInstance()
            val h0 = if (date.get(Calendar.HOUR) == 0) 12 else date.get(Calendar.HOUR)
            views.setTextViewText(R.id.hour0, mFormat.format(h0))
            views.setTextViewText(R.id.minute0, ":".plus(mFormat.format(date.get(Calendar.MINUTE))))
            views.setTextViewText(R.id.am_pm0, getTimeInfix(date.get(Calendar.AM_PM)))

            views.setTextViewText(R.id.txt_day0, getFullDate(date))*/


// Selected Timezone


            val newDate = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
            Log.d("Appwidget", newDate.timeZone.toString())

         /*   val h1 = if (newDate.get(Calendar.HOUR) == 0) 12 else newDate.get(Calendar.HOUR)
            views.setTextViewText(R.id.hour1, mFormat.format(h1))
            views.setTextViewText(R.id.minute1, ":".plus(mFormat.format(newDate.get(Calendar.MINUTE))))
            views.setTextViewText(R.id.am_pm1, getTimeInfix(newDate.get(Calendar.AM_PM)))
            views.setTextViewText(R.id.txt_day1, getFullDate(newDate))*/

            var txt0 = TimeZone.getDefault().id
            if (txt0.contains("/"))
                txt0 = txt0.split("/")[1].replace("_", " ")

            var txt1 = TimeZone.getTimeZone(timeZone).id
            if (txt1.contains("/")) {
                val ch = txt1.split("/")
                txt1 = ch[ch.size - 1].replace("_", " ")
            }


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

            return if (am_pm == 0) TEXT_AM else TEXT_PM
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
            return weekday/*.subSequence(0, 3).toString()*/
                    .plus(", ")
                    .plus(date.toString())
                    .plus(" ")
                    .plus(month.subSequence(0, 3))

        }
    }
}

