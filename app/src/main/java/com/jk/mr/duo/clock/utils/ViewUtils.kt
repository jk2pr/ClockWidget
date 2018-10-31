package com.jk.mr.duo.clock.utils

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat

object ViewUtils {
    fun getClockTextColor(context: Context, bgColorValue: Int): Int {
        return if (isLightColor(bgColorValue)) {
            ContextCompat.getColor(context, android.R.color.black)
        } else {
            ContextCompat.getColor(context, android.R.color.white)
        }
    } fun getClockTextTimeZoneColor(context: Context, bgColorValue: Int): Int {
        return if (isLightColor(bgColorValue)) {
            ContextCompat.getColor(context, android.R.color.darker_gray)
        } else {
            ContextCompat.getColor(context, android.R.color.background_light)
        }
    }

    private fun isLightColor(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }
}