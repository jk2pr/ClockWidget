package com.jk.mr.duo.clock.utils

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable



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

     fun setLayerShadow(color: Int): LayerDrawable {
        var shadow: GradientDrawable
        val strokeValue = 6
        val radiousValue = 2
        try {
            val colors1 = intArrayOf(color, Color.parseColor("#FFFFFF"))
            shadow = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors1)
            shadow.cornerRadius = radiousValue.toFloat()
        } catch (e: Exception) {
            val colors1 = intArrayOf(Color.parseColor("#419ED9"), Color.parseColor("#419ED9"))
            shadow = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors1)
            shadow.cornerRadius = radiousValue.toFloat()
            e.printStackTrace()
        }


        val colors = intArrayOf(Color.parseColor("#D8D8D8"), Color.parseColor("#E5E3E4"))

        val backColor = GradientDrawable(GradientDrawable.Orientation.BL_TR, colors)
        backColor.cornerRadius = radiousValue.toFloat()
        backColor.setStroke(strokeValue, Color.parseColor("#D8D8D8"))

        //finally c.reate a layer list and set them as background.
        val layers = arrayOfNulls<Drawable>(2)
        layers[0] = backColor
        layers[1] = shadow

        val layerList = LayerDrawable(layers)
        layerList.setLayerInset(0, 0, 0, 0, 0)
        layerList.setLayerInset(1, strokeValue, strokeValue, strokeValue, strokeValue)
        return layerList
    }
}