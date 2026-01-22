package com.hoppers.duoclock.appwidget.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.hoppers.duoclock.AppWidgetConfigureActivity
import com.hoppers.duoclock.utils.Utils.getFormattedTime
import java.util.TimeZone

@Composable
fun EmptyWidgetState() {
    val localTimeZone = TimeZone.getDefault().id
    val tzLabel = localTimeZone
        .substringAfterLast("/")
        .replace("_", " ")

    Column(
        modifier = GlanceModifier
            .width(216.dp)
            .height(168.dp)
            .padding(8.dp)
            .cornerRadius(24.dp)
            .background(GlanceTheme.colors.surface)
            .clickable(actionStartActivity<AppWidgetConfigureActivity>()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Local clock
        Text(
            text = getFormattedTime(localTimeZone, context = LocalContext.current),
            style = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.primary
            )
        )

        Spacer(GlanceModifier.height(4.dp))

        Text(
            text = tzLabel,
            style = TextStyle(
                fontSize = 14.sp,
                color = GlanceTheme.colors.secondary
            )
        )

        Spacer(GlanceModifier.height(12.dp))

        Text(
            text = "+ Add clocks",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = GlanceTheme.colors.primary
            )
        )
    }
}