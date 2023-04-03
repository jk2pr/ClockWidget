package com.jk.mr.duo.clock.component

import android.view.Gravity
import android.widget.TextClock
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.jk.mr.duo.clock.utils.UiUtils
import com.jk.mr.duo.clock.utils.Utils

@Composable
fun TextClock(
    modifier: Modifier = Modifier,
    timeZone: String = java.util.TimeZone.getDefault().id,
    textColor: Color = MaterialTheme.colors.secondary
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
