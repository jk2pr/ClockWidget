package com.hoppers.duoclock.appwidget.receivers
import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.hoppers.duoclock.appwidget.AppWidget

class GlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = AppWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // Start minute ticking when widget is added / restored
        ClockAlarmReceiver.scheduleNext(context)
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray
    ) {
        super.onDeleted(context, appWidgetIds)

        // Stop ticking when last widget is removed
        ClockAlarmReceiver.cancel(context)
    }
}