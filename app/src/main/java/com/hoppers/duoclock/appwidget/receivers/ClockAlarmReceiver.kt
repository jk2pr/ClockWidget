package com.hoppers.duoclock.appwidget.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.hoppers.duoclock.appwidget.AppWidget
import com.hoppers.duoclock.appwidget.CITIES_JSON
import com.hoppers.duoclock.appwidget.TICK_KEY
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.utils.Constants.TAG
import com.hoppers.duoclock.utils.encodeCities
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ClockAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        Log.d(TAG, "ClockAlarmReceiver: onReceive")

        try {
            updateNow(context)
        } finally {
            scheduleNext(context)
            pendingResult.finish()
        }
    }

    companion object {

        /**
         * Trigger a widget redraw for ALL widgets.
         * Does NOT modify city or page state.
         */
        fun updateNow(context: Context, calDataList: List<LocationItem> = emptyList()) {
            MainScope().launch {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(AppWidget::class.java)

                Log.d(TAG, "updateNow: widgets=$glanceIds")

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) {
                        // Dummy tick to force recomposition
                          it[TICK_KEY] = System.nanoTime()
                        if (calDataList.isNotEmpty())
                            it[CITIES_JSON] = encodeCities(calDataList)
                    }
                    AppWidget().update(context, glanceId)
                }
            }
        }

        /**
         * Schedule next exact minute tick
         */
        fun scheduleNext(context: Context) {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ClockAlarmReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val nextMinute =
                (System.currentTimeMillis() / 60000 + 1) * 60000

            Log.d(
                "ClockAlarm",
                "canScheduleExactAlarms=${canUseExactAlarms(alarmManager)}"
            )

            try {
                if (canUseExactAlarms(alarmManager)) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextMinute,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        nextMinute,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextMinute,
                    pendingIntent
                )
            }
        }

        fun cancel(context: Context) {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, ClockAlarmReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
        }

        private fun canUseExactAlarms(alarmManager: AlarmManager): Boolean {
            return alarmManager.canScheduleExactAlarms()
        }

        private fun shouldUpdate(context: Context): Boolean {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return !pm.isPowerSaveMode
        }
    }
}