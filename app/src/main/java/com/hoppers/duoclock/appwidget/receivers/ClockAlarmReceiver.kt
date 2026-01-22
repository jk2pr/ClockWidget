package com.hoppers.duoclock.appwidget.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.action.action
import com.hoppers.duoclock.appwidget.WidgetUpdater.Companion.scheduleNext
import com.hoppers.duoclock.appwidget.WidgetUpdater.Companion.updateNow
import com.hoppers.duoclock.utils.Constants.TAG

class ClockAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        Log.d(TAG, "ClockAlarmReceiver: onReceive, ${intent.action}")

        try {
            updateNow(context)
        } finally {
            scheduleNext(context)
            pendingResult.finish()
        }
    }


}