package com.jk.mr.duo.clock

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.jk.mr.duo.clock.utils.Constants.SEPARATOR
import com.jk.mr.duo.clock.utils.Constants.TEXT_AM
import com.jk.mr.duo.clock.utils.Constants.TEXT_PM
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
import com.jk.mr.duo.clock.utils.Constants.loadTitlePref
import com.jk.mr.duo.clock.utils.Utils
import java.text.DateFormatSymbols
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
        super.onDeleted(context, appWidgetIds)
        deleteAllPref(context)

    }

    companion object {

        private fun setThem(context: Context, views: RemoteViews, theme: String) {
            // Already dark
            var co = ContextCompat.getColor(context, android.R.color.white)
            when (theme) {
                THEME_DARK -> //Background
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.dark_widget_bg)
                //Textcolor
                //  co = ContextCompat.getColor(context, android.R.color.darker_gray)
                // LinearLayout(context).setBackgroundResource(R.drawable.widget_bg)
                THEME_LIGHT -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.light_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.black)
                }
                THEME_RED -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.red_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                THEME_ORANGE -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.orange_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                THEME_YELLOW -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.yellow_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.black)
                }
                THEME_GREEN -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.green_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                THEME_BLUE -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.blue_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }
                THEME_INDIGO -> {
                    views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.indigo_widget_bg)
                    co = ContextCompat.getColor(context, android.R.color.white)
                }

                //Default


            }

            //Selected

            views.setInt(R.id.separator, "setBackgroundColor", co)
            views.setTextColor(R.id.clock0, co)
            views.setTextColor(R.id.txt_timezone0, co)

            //Selected

            views.setTextColor(R.id.clock1, co)
            views.setTextColor(R.id.txt_timezone1, co)


        }


        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val data = loadTitlePref(context)


            val d = data.split(SEPARATOR)

            //  val country = d[0]
            var timeZone = TimeZone.getDefault().id
            if (d.isNotEmpty() && d.size > 2)
                timeZone = d[2]


            val theme = getThemePref(context)
            val views = RemoteViews(context.packageName, R.layout.app_widget)


            //  if (bgColor == 0)
            //    bgColor = ContextCompat.getColor(context, R.color.bgcolor)
            setThem(context, views, theme)



            views.setCharSequence(R.id.clock0, "setFormat12Hour", Utils.get12HoursFormat())
            views.setCharSequence(R.id.clock1, "setFormat12Hour", Utils.get12HoursFormat())


            views.setCharSequence(R.id.clock0, "setFormat24Hour", Utils.get24HoursFormat())
            views.setCharSequence(R.id.clock1, "setFormat24Hour", Utils.get24HoursFormat())

            views.setString(R.id.clock0, "setTimeZone", TimeZone.getDefault().id)
            views.setString(R.id.clock1, "setTimeZone", TimeZone.getTimeZone(timeZone).id)


            var txt0 = StringBuilder(TimeZone.getDefault().id)
            if (txt0.contains("/"))
                txt0 = StringBuilder(txt0.toString().split("/")[1].replace("_", " ").trim())


            if (txt0.split(" ").size > 2) {
                txt0 = txt0.replace(txt0.lastIndexOf(" "), txt0.lastIndexOf(" ") + 1, "\n")

            }

            views.setCharSequence(R.id.txt_timezone0, "setText", txt0)


            var txt1 = StringBuilder(TimeZone.getTimeZone(timeZone).id)
            when {
                (txt1.contains("/")) ->
                    txt1 = StringBuilder(txt1.toString().split("/")[1].replace("_", " ").trim())
            }
            views.setCharSequence(R.id.txt_timezone1, "setText", txt1)

            /*if (d.size > 1) {

                var add=d[0]
                if (add.isEmpty())
                    add=d[1]
                val txt1 = StringBuilder(add.trim())
                *//*if (txt1.split(" ").size > 2) {
                    txt1 = txt1.replace(txt1.lastIndexOf(" "), txt1.lastIndexOf(" ") + 1, "\n")

                }*//*

                views.setCharSequence(R.id.txt_timezone1, "setText", txt1)
                *//* //TimeZone.getTimeZone(timeZone).id
                 if (txt1.contains("/")) {
                     val ch = txt1.split("/")
                     txt1 = ch[ch.size - 1].replace("_", " ")
                 }*//*
            }*/


            val intent = Intent(context, AppWidgetConfigureActivity::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            views.setOnClickPendingIntent(R.id.rel_custom_time, pendingIntent)


            //Click listner of individual


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

