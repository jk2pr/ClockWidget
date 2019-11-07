package com.jk.mr.duo.clock

import com.jk.mr.duo.clock.utils.Constants.THEME_BLUE
import com.jk.mr.duo.clock.utils.Constants.THEME_DARK
import com.jk.mr.duo.clock.utils.Constants.THEME_GREEN
import com.jk.mr.duo.clock.utils.Constants.THEME_INDIGO
import com.jk.mr.duo.clock.utils.Constants.THEME_LIGHT
import com.jk.mr.duo.clock.utils.Constants.THEME_ORANGE
import com.jk.mr.duo.clock.utils.Constants.THEME_RED
import com.jk.mr.duo.clock.utils.Constants.THEME_YELLOW
import com.jk.mr.duo.clock.utils.Constants.getThemePref


fun AppWidgetConfigureActivity.getTintFromTheme(theme: Int):Int{

    return when (theme) {
        R.style.AppThemeLight -> android.R.color.black
        R.style.AppThemeDark -> android.R.color.white
        R.style.AppThemeRed -> android.R.color.holo_red_light
        R.style.AppThemeYellow -> R.color.yellow_dark
        R.style.AppThemeGreen -> R.color.green_light
        R.style.AppThemeBlue -> android.R.color.holo_blue_light
        R.style.AppThemeIndigo -> R.color.indigo_light
        R.style.AppThemeOrange -> android.R.color.holo_orange_light

        else -> android.R.color.background_light
    }
}

 fun AppWidgetConfigureActivity.getThemePref(): Int {
    return when (getThemePref(this)) {
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