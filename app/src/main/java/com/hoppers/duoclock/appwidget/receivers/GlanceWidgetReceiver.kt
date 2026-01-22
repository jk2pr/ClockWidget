package com.hoppers.duoclock.appwidget.receivers
import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.hoppers.duoclock.appwidget.AppWidget
import com.hoppers.duoclock.appwidget.WidgetUpdater

class GlanceWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = AppWidget()

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        WidgetUpdater.updateNow(context)
        // Start minute ticking when widget is added / restored
        WidgetUpdater.scheduleNext(context)
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray
    ) {
        super.onDeleted(context, appWidgetIds)

        // Stop ticking when last widget is removed
        WidgetUpdater.cancel(context)
    }
}