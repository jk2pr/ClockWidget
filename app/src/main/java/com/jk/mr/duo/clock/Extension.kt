package com.jk.mr.duo.clock

import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants.THEME_BLUE
import com.jk.mr.duo.clock.utils.Constants.THEME_DARK
import com.jk.mr.duo.clock.utils.Constants.THEME_GREEN
import com.jk.mr.duo.clock.utils.Constants.THEME_INDIGO
import com.jk.mr.duo.clock.utils.Constants.THEME_LIGHT
import com.jk.mr.duo.clock.utils.Constants.THEME_ORANGE
import com.jk.mr.duo.clock.utils.Constants.THEME_RED
import com.jk.mr.duo.clock.utils.Constants.THEME_YELLOW

fun AppWidgetConfigureActivity.getThemePref(): Int {
    return when (preferenceHandler.getThemePref()) {
        THEME_LIGHT -> R.style.AppThemeLight
        THEME_DARK -> R.style.AppThemeDark
        THEME_RED -> R.style.AppThemeRed
        THEME_YELLOW -> R.style.AppThemeYellow
        THEME_GREEN -> R.style.AppThemeGreen
        THEME_BLUE -> R.style.AppThemeBlue
        THEME_INDIGO -> R.style.AppThemeIndigo
        THEME_ORANGE -> R.style.AppThemeOrange
        else -> R.style.AppThemeLight
    }
}
