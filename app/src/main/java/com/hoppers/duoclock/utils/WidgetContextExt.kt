package com.hoppers.duoclock.utils

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context

fun Context.isFromWidgetAddFlow(): Boolean =
    (this as? Activity)?.intent?.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID) == true
