package com.jk.mr.duo.clock.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun ClockDashBoard() {
    val currentTimeZone = TimeZone.getDefault().id
    var tz = currentTimeZone
    if (tz.contains("/")) {
        tz = currentTimeZone.split("/").first().replace("_", " ")
    }

    val contentColor = MaterialTheme.colors.surface
    val modifier = Modifier
        // .clip(RectangleShape)
        .size(180.dp)
        /* .graphicsLayer {
             clip = true
             shape = CircleShape
             translationY = 0.dp.toPx()
         }*/
        .drawBehind {
            drawArc(
                color = Color(contentColor.toArgb()),
                startAngle = 140f,
                sweepAngle = 260f,
                useCenter = false,
                style = Stroke(10.dp.toPx(), cap = StrokeCap.Round),
                size = Size(size.width, size.height)
            )
        }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextClock(textColor = contentColor)
            Text(
                style = MaterialTheme.typography.body1.copy(color = contentColor),
                text = tz
            )
        }
    }
}
