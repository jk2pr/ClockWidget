package com.hoppers.duoclock.appwidget

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.utils.Utils.getFormattedTime
import com.hoppers.duoclock.utils.decodeCities
import java.util.TimeZone
import kotlin.compareTo


val PAGE_INDEX = intPreferencesKey("page_index")
val CITIES_JSON = stringPreferencesKey("cities_json")

val TICK_KEY = longPreferencesKey("tick")

class AppWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    private val localTimeZone = TimeZone.getDefault().id

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {

            val prefs = currentState<Preferences>()
            val pageIndex = prefs[PAGE_INDEX] ?: 0
            val cities = decodeCities(prefs[CITIES_JSON])
            val tick = prefs[TICK_KEY] ?: 0L
            val isFresh = System.currentTimeMillis() - tick < 2_000
            Log.d("AppWidget", "provideGlance: tick=$tick")


            if (cities.isEmpty()) return@provideContent

            val fix5 = if (cities.size > 5) 5 else cities.size
            val safeIndex = (pageIndex % fix5)
            val remoteCity = cities[safeIndex]

            GlanceTheme {
                WidgetRoot(
                    localTimeZone = localTimeZone,
                    remoteCity = remoteCity,
                    pageIndex = safeIndex,
                    pageCount = fix5
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
        modifier = GlanceModifier.fillMaxWidth().padding(8.dp).cornerRadius(32.dp),
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
            timeZoneId = remoteCity.currentCityTimeZoneId ?: localTimeZone,
            label = remoteCity.name,
            onClick = actionRunCallback<NextPageAction>()
        )

        Spacer(GlanceModifier.height(8.dp))

        if (pageCount > 1) {
            PageIndicator(
                index = pageIndex, count = pageCount
            )
        }

    }
}

@Composable
private fun ClockBlock(
    timeZoneId: String, label: String, onClick: Action, isRefresh: Boolean = false
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
            text = getFormattedTime(timeZoneId, is24Hour = false), style = TextStyle(
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

/* ---------- Page Indicator ---------- */

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
