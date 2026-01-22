package com.hoppers.duoclock.utils

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.provider.Settings

object UiUtils {
    fun getFinlandicaRegularTypeFace(activity: Context): Typeface = Typeface.createFromAsset(activity.assets, "fonts/finlandica.ttf")
    fun openExactAlarmSystemSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        )
    }
}
