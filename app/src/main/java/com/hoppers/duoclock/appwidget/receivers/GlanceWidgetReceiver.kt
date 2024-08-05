package com.hoppers.duoclock.appwidget.receivers

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import com.hoppers.duoclock.appwidget.AppWidget
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class GlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppWidget()
    private val coroutineScope = MainScope()
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("AppWidget ", "onReceive: Intent action ===   ${intent.action} ")
        if (intent.action == "UPDATE_ACTION") {
            coroutineScope.launch {
                glanceAppWidget.updateAll(context)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d("AppWidget", "onUpdate: Called")
        coroutineScope.launch {
            glanceAppWidget.updateAll(context)
        }
    }
}
