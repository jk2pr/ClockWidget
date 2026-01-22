package com.hoppers.duoclock.dashboard.components

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import com.hoppers.duoclock.appwidget.AppWidget
import com.hoppers.duoclock.appwidget.receivers.GlanceWidgetReceiver
import com.hoppers.duoclock.dashboard.data.LocationItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.jvm.java

object WidgetPinner {

    fun requestPin(context: Context, dataList: List<LocationItem> = emptyList()) {
        CoroutineScope(Dispatchers.Main).launch {
            val manager = GlanceAppWidgetManager(context)
            val success = manager.requestPinGlanceAppWidget(receiver = GlanceWidgetReceiver::class.java)
            Log.d("WidgetPinner", "Pin requested: $success")
        }
    }
}