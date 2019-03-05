package com.jk.mr.duo.clock.services

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import android.util.Log
import android.widget.RemoteViews
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.R
import java.text.SimpleDateFormat
import java.util.*


class TextClockService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        if (intent.action == AppWidget.CUSTOM_INTENT) {

            val t = Timer()
//Set the schedule function and rate
            t.scheduleAtFixedRate(object : TimerTask() {

                override fun run() {
                    updateTime()
                    //Called each time when 1000 milliseconds (1 second) (the period parameter)
                }

            },
                    //Set how long before to start calling the TimerTask (in milliseconds)
                    0,
                    //Set the amount of time between each execution (in milliseconds)
                    1000)

         //   MyCountDownTimer(Long.MAX_VALUE,1).start()

            //updateTime(now)
        }
    }
   /* override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("JobIntentService.TAG", "onStartCommand")
        return Service.START_STICKY
    }*/

    private fun updateTime() {
        val date = Calendar.getInstance()
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




    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        private val TAG = "TextClockService"

        fun enqueueWork(ctx: Context, intent: Intent) {
            enqueueWork(ctx, TextClockService::class.java, 100, intent)
        }
    }
}