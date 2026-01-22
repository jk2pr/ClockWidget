package com.hoppers.duoclock.dashboard.components

import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextClock
import android.widget.TextView
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.hoppers.duoclock.utils.UiUtils
import com.hoppers.duoclock.utils.Utils
import java.util.TimeZone

@Composable
fun TextClock(
    modifier: Modifier = Modifier,
    timeZone: String = TimeZone.getDefault().id,
    textColor: Color = LocalContentColor.current,
) {
    val tz = timeZone
        .substringAfterLast("/")
        .replace("_", " ")

    AndroidView(
        modifier = modifier,
        factory = { context ->

            LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                // CLOCK
                addView(
                    TextClock(context).apply {
                        format12Hour = Utils.getDashBoard12HoursFormat()
                        format24Hour = Utils.getDashBoard24HoursFormat()
                        this.timeZone = timeZone
                        typeface = UiUtils.getFinlandicaTypeFace(context)
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16.0f)
                        setTextColor(textColor.toArgb())
                        gravity = Gravity.CENTER
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                        )
                    }
                )
                // LABEL
                addView(
                    TextView(context).apply {
                        text = tz
                        gravity = Gravity.CENTER
                        typeface = UiUtils.getFinlandicaTypeFace(context)
                        setTextColor(textColor.toArgb())
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                )
            }
        }
    )
}
