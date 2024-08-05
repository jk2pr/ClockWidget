package com.hoppers.duoclock.component

import android.view.Gravity
import android.widget.TextClock
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.hoppers.duoclock.utils.UiUtils
import com.hoppers.duoclock.utils.Utils

@Composable
fun TextClock(
    modifier: Modifier = Modifier,
    timeZone: String = java.util.TimeZone.getDefault().id,
    textColor: Color = LocalContentColor.current
) {
    AndroidView(
        factory = { context ->
            TextClock(context).apply {
                format12Hour = Utils.getDashBoard12HoursFormat()
                format24Hour = Utils.get24HoursFormat()
                this.timeZone = timeZone
                typeface = UiUtils.getBebasneueRegularTypeFace(context)
                textSize = 40.0f
                gravity = Gravity.CENTER
                setTextColor(textColor.toArgb())
            }
        },
        modifier = modifier
    )
}
