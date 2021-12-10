package com.jk.mr.duo.clock.utils

import android.content.Context
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.utils.Constants.SET_BACKGROUND_COLOR
import com.jk.mr.duo.clock.utils.Constants.SET_BACKGROUND_RESOURCE
import com.jk.mr.duo.clock.utils.Constants.THEME_BLUE
import com.jk.mr.duo.clock.utils.Constants.THEME_DARK
import com.jk.mr.duo.clock.utils.Constants.THEME_GREEN
import com.jk.mr.duo.clock.utils.Constants.THEME_INDIGO
import com.jk.mr.duo.clock.utils.Constants.THEME_LIGHT
import com.jk.mr.duo.clock.utils.Constants.THEME_ORANGE
import com.jk.mr.duo.clock.utils.Constants.THEME_RED
import com.jk.mr.duo.clock.utils.Constants.THEME_YELLOW

class ThemeHandler {
    fun setWidgetTheme(context: Context, views: RemoteViews, theme: String) {
        var color = ContextCompat.getColor(context, android.R.color.white) // Default White
        views.apply {
            when (theme) {
                // Background
                THEME_DARK -> setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.dark_widget_bg)
                THEME_GREEN -> setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.green_widget_bg)
                THEME_BLUE -> setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.blue_widget_bg)
                THEME_INDIGO -> setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.indigo_widget_bg)
                THEME_RED -> setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.red_widget_bg)
                THEME_ORANGE -> setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.orange_widget_bg)
                THEME_LIGHT -> {
                    setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.light_widget_bg)
                    color = ContextCompat.getColor(context, android.R.color.black)
                }
                THEME_YELLOW -> {
                    setInt(R.id.widget_root, SET_BACKGROUND_RESOURCE, R.drawable.yellow_widget_bg)
                }
            }
        }
        views.apply {
            // default Clock
            setInt(R.id.separator, SET_BACKGROUND_COLOR, color)
            setTextColor(R.id.clock0, color)
            setTextColor(R.id.txt_timezone0, color)
            // Selected Clock
            setTextColor(R.id.clock1, color)
            setTextColor(R.id.txt_timezone1, color)
        }
    }
}
