package com.hoppers.duoclock.appwidget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState

class NextPageAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) {
            it[PAGE_INDEX] = (it[PAGE_INDEX] ?: 0) + 1
        }
        AppWidget().update(context, glanceId)
    }
}

class PrevPageAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) {
            val current = it[PAGE_INDEX] ?: 0
            it[PAGE_INDEX] = if (current > 0) current - 1 else 0
        }
        AppWidget().update(context, glanceId)
    }
}