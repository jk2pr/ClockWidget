package com.hoppers.duoclock.appwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.hoppers.duoclock.appwidget.receivers.ClockAlarmReceiver
import com.hoppers.duoclock.utils.CITIES_JSON
import com.hoppers.duoclock.utils.DataStorePreferenceHandler
import com.hoppers.duoclock.utils.TICK_KEY
import com.hoppers.duoclock.utils.encodeCities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

class WidgetUpdater {
    companion object {
        private val scope = CoroutineScope(Dispatchers.Main)

        /**
         * Trigger a widget redraw for ALL widgets.
         * Does NOT modify city or page state.
         */
        fun updateNow(
            context: Context,
            appWidgetId: Int? = null
        ) {
            scope.launch {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(AppWidget::class.java)

                Log.d("WidgetUpdater", "updateNow() glanceIds=$glanceIds")

                val dataStore = GlobalContext
                    .get()
                    .get<DataStorePreferenceHandler>()   // 👈 from Koin

                val cities = dataStore.loadCitiesOnce()
                if (cities.isEmpty()) return@launch

                val citiesJson = encodeCities(cities)
                if (citiesJson.isEmpty()) {
                    Log.d("WidgetUpdater", "No cities to update widget")
                    return@launch
                }

                glanceIds.forEach { glanceId ->
                    updateAppWidgetState(context, glanceId) { prefs ->
                        prefs[CITIES_JSON] = citiesJson
                        prefs[TICK_KEY] = System.nanoTime() // force recomposition
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
                    Log.d("ClockAlarm", "Using exact alarm")
                } else {
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        nextMinute,
                        pendingIntent
                    )
                    Log.d("ClockAlarm", "Using inexact alarm fallback")
                }

            } catch (_: SecurityException) {
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

        private fun canUseExactAlarms(alarmManager: AlarmManager) =
            alarmManager.canScheduleExactAlarms()


        private fun shouldUpdate(context: Context): Boolean {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return !pm.isPowerSaveMode
        }
    }
}