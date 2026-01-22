package com.hoppers.duoclock.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.res.ResourcesCompat
import com.jk.mr.duo.clock.R

object UiUtils {
    fun getFinlandicaTypeFace(context: Context) = ResourcesCompat.getFont(context, R.font.finlandica)
    fun openExactAlarmSystemSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        )
    }
}
