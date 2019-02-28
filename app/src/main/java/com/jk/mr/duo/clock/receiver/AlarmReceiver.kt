package com.jk.mr.duo.clock.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jk.mr.duo.clock.services.TextClockService

import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    // private static Context context;

    override fun onReceive(context: Context, intent: Intent) {
        /* enqueue the job */
        // this.context = context;
        TextClockService.enqueueWork(context, intent)
        alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    companion object {

        const val CUSTOM_INTENT = "com.jk.duo.clock.intent.action.ALARM"

        internal var alarm: AlarmManager? = null

        fun cancelAlarm(context: Context) {
            /* cancel any pending alarm */
            if (alarm != null)
                alarm!!.cancel(getPendingIntent(context))
        }

        fun setAlarm(force: Boolean, context: Context) {
            cancelAlarm(context)
            // EVERY X MINUTES
            val delay = (1000 * 60 * 10).toLong()
            var `when` = System.currentTimeMillis()
            if (!force) {
                `when` += delay
            }


            val date = Calendar.getInstance()
            date.set(Calendar.SECOND, 0)
            date.set(Calendar.MILLISECOND, 0)
            date.add(Calendar.MINUTE, 0)
            /* fire the broadcast */
            if (alarm == null)
                alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarm!!.setRepeating(AlarmManager.RTC, date.timeInMillis, 60*1000, getPendingIntent(context))
            // alarm.set(AlarmManager.RTC_WAKEUP, when, getPendingIntent(context));
        }

        private fun getPendingIntent(context: Context): PendingIntent {
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            alarmIntent.action = CUSTOM_INTENT
            return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        }
    }
}