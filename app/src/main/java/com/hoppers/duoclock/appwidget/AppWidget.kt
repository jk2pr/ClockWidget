package com.hoppers.duoclock.appwidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.hoppers.duoclock.AppWidgetConfigureActivity
import com.hoppers.duoclock.appwidget.components.EmptyWidgetState
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.utils.CITIES_JSON
import com.hoppers.duoclock.utils.Constants.MAX_PINNED
import com.hoppers.duoclock.utils.PAGE_INDEX
import com.hoppers.duoclock.utils.Utils.getFormattedTime
import com.hoppers.duoclock.utils.decodeCities
import java.util.TimeZone

class AppWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    private val localTimeZone = TimeZone.getDefault().id

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val pageIndex = prefs[PAGE_INDEX] ?: 0
            val cities = decodeCities(prefs[CITIES_JSON])
            val pinnedCities = cities
                .filter { it.isPinned }
                .take(MAX_PINNED)

            if (pinnedCities.isEmpty()) {
                GlanceTheme { EmptyWidgetState() }
                return@provideContent
            }

            val safeIndex = pageIndex % pinnedCities.size
            val remoteCity = pinnedCities[safeIndex]

            GlanceTheme {
                WidgetRoot(
                    localTimeZone = localTimeZone,
                    remoteCity = remoteCity,
                    pageIndex = safeIndex,
                    pageCount = pinnedCities.size
                )
            }
        }
    }
}


@Composable
private fun WidgetRoot(
    localTimeZone: String,
    remoteCity: LocationItem,
    pageIndex: Int,
    pageCount: Int
) {
    Column(
        modifier = GlanceModifier.size(216.dp)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        // 🔒 LOCAL CLOCK (FIXED)
        ClockBlock(
            timeZoneId = localTimeZone,
            label = "Local",
            onClick = actionStartActivity<AppWidgetConfigureActivity>()

        )
        Spacer(GlanceModifier.height(8.dp))
        // 🔁 REMOTE CLOCK (PAGED)
        ClockBlock(
            timeZoneId = remoteCity.remoteCityTimeZone ?: localTimeZone,
            label = remoteCity.name,
            onClick = actionRunCallback<NextPageAction>()
        )

        Spacer(GlanceModifier.height(8.dp))
        Box(GlanceModifier.height(8.dp), contentAlignment = Alignment.Center) {
            if (pageCount > 1)
                PageIndicator(index = pageIndex, count = pageCount)
        }
    }
}

@Composable
fun ClockBlock(
    timeZoneId: String, label: String, onClick: Action
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.cornerRadius(24.dp).fillMaxWidth()
            .height(84.dp)
            .background(GlanceTheme.colors.surface)
            .padding(16.dp).clickable(onClick),
    ) {
        val text = if (label
                .split(",").size > 1
        ) label.split(",").first().trim() else label
        Text(
            text = getFormattedTime(timeZoneId, context = LocalContext.current),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GlanceTheme.colors.primary
            )
        )

        Text(
            text = text,
            maxLines = 1,
            style = TextStyle(
                fontSize = 14.sp, color = GlanceTheme.colors.secondary
            )
        )

    }
}

@Composable
private fun PageIndicator(
    index: Int, count: Int
) {
    Row(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(count) { i ->
            Box(
                modifier = GlanceModifier.size(if (i == index) 6.dp else 4.dp).cornerRadius(50.dp)
                    .background(GlanceTheme.colors.onBackground)
            ) {}
            if (i < count - 1) Spacer(GlanceModifier.width(6.dp))
        }
    }
}
/* ---------- Paging Tap Zones ---------- */
