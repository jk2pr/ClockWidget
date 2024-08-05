package com.hoppers.duoclock.utils

import android.content.Context
import android.graphics.Typeface

object UiUtils {
    fun getBebasneueRegularTypeFace(activity: Context): Typeface = Typeface.createFromAsset(activity.assets, "fonts/bebasnue_bold.ttf")
    fun getAbelRegularTypeFace(activity: Context): Typeface = Typeface.createFromAsset(activity.assets, "fonts/abel_regular.ttf")
}
