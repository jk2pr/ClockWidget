package com.jk.mr.duo.clock.services

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.widget.RemoteViews
import android.content.ComponentName
import android.appwidget.AppWidgetManager
import android.content.Context
import android.support.v4.app.JobIntentService
import android.util.Log
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.receiver.AlarmReceiver


class TextClockService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        if (intent.action == AlarmReceiver.CUSTOM_INTENT) {
            val now = Calendar.getInstance()
            updateTime(now)
        }
    }

    private fun updateTime(date: Calendar) {
        Log.d(TAG, "Update: " + dateFormat.format(date.time))
        val manager = AppWidgetManager.getInstance(this)
        val name = ComponentName(this, AppWidget::class.java)
        val appIds = manager.getAppWidgetIds(name)
        //  val words = TimeToWords.timeToWords(date)
        for (id in appIds) {
            val v = RemoteViews(packageName, R.layout.app_widget)
            //updateTimes(id,v)
            AppWidget.updateAppWidget(this,manager,id)
            //manager.updateAppWidget(id, v)
        }

    }

   /* private fun updateTimes(appWidgetId:Int,views: RemoteViews) {

        val timeZone = AppWidgetConfigureActivity.loadTitlePref(this, appWidgetId)

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


        val intent = Intent(this, AppWidgetConfigureActivity::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        views.setOnClickPendingIntent(R.id.clock1, pendingIntent)

           // v.setTextViewText(R.id.hours, date.get(Calendar.HOUR_OF_DAY) + "")
           // v.setTextViewText(R.id.minutes, date.get(Calendar.MINUTE) + "")
           // v.setTextViewText(R.id.tens, date.get(Calendar.AM_PM) + "")
            //  updateTime( date.getTime(), v );


    }*/


    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        private val TAG = "TextClockService"

        fun enqueueWork(ctx: Context, intent: Intent) {
            enqueueWork(ctx, TextClockService::class.java, 100, intent)
        }
    }
}