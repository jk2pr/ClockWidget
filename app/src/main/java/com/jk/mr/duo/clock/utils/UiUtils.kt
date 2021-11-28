package com.jk.mr.duo.clock.utils

import android.content.Context
import android.graphics.Typeface

object UiUtils {
    fun getBebasneueRegularTypeFace(activity: Context): Typeface =
        Typeface.createFromAsset(activity.assets, "fonts/bebasneue_regular.ttf")
}
