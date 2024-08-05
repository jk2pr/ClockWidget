package com.hoppers.duoclock.appwidget

import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.hoppers.duoclock.AppWidgetConfigureActivity
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.theme.ClockGlanceColorScheme
import com.hoppers.duoclock.utils.Constants
import com.hoppers.duoclock.utils.Utils
import com.jk.mr.duo.clock.R
import kotlinx.serialization.json.Json
import java.util.TimeZone

class AppWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    private val colorProvider = ColorProvider(night = Color.White, day = Color.White)
    private val currentTimeZoneId: String = TimeZone.getDefault().id
    private val cornerRadius = 8
    private val backgroundAlpha = 0.5f
    private val currentQuoteKey = stringPreferencesKey("calData")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val packageName = context.packageName
        val remoteViewsDefault = RemoteViews(packageName, R.layout.text_clock_widget)
        val remoteViewsSelected = RemoteViews(packageName, R.layout.text_clock_widget)

        provideContent {
            val preferences = currentState<Preferences>()
            val calDataString = preferences[currentQuoteKey]
            if (calDataString != null) {
                val locationItem = Json.decodeFromString<LocationItem>(calDataString)
                val selectedTimeZone = locationItem.currentCityTimeZoneId ?: TimeZone.getDefault().id
                GlanceTheme(
                    colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) GlanceTheme.colors else ClockGlanceColorScheme.colors
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(
                                actionStartActivity<AppWidgetConfigureActivity>(
                                    parameters = actionParametersOf(
                                        // Fake parameter
                                        ActionParameters.Key<Double>("") to 2.0
                                    )
                                )
                            ).appWidgetBackground().cornerRadiusCompat(
                                cornerRadius = cornerRadius,
                                color = MaterialTheme.colorScheme.primary.toArgb(),
                                backgroundAlpha = backgroundAlpha
                            )
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AndroidRemoteViews(
                                remoteViews = remoteViewsDefault,
                                containerViewId = View.NO_ID,
                                content = {
                                    remoteViewsDefault.apply {
                                        setCharSequence(
                                            R.id.c_clock,
                                            Constants.SET_FORMAT24HOUR,
                                            Utils.get24HoursFormat()
                                        )
                                        setCharSequence(
                                            R.id.c_clock,
                                            Constants.SET_FORMAT12HOUR,
                                            Utils.get12HoursFormat()
                                        )
                                        setString(
                                            R.id.c_clock,
                                            Constants.SET_TIME_ZONE,
                                            currentTimeZoneId
                                        )
                                    }
                                }
                            )
                            Text(
                                style = TextStyle(color = colorProvider),
                                text = locationItem.displayTimeZoneCityById().toString()
                            )
                        }
                        // Separator
                        Spacer(
                            modifier = GlanceModifier.width(2.dp).fillMaxHeight()
                                .background(colorProvider)
                        )
                        Column(
                            modifier = GlanceModifier.defaultWeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AndroidRemoteViews(
                                remoteViews = remoteViewsSelected,
                                containerViewId = View.NO_ID
                            ) {
                                remoteViewsSelected.apply {
                                    setCharSequence(
                                        R.id.c_clock,
                                        Constants.SET_FORMAT24HOUR,
                                        Utils.get24HoursFormat()
                                    )
                                    setCharSequence(
                                        R.id.c_clock,
                                        Constants.SET_FORMAT12HOUR,
                                        Utils.get12HoursFormat()
                                    )
                                    setString(
                                        R.id.c_clock,
                                        Constants.SET_TIME_ZONE,
                                        TimeZone.getTimeZone(selectedTimeZone).id
                                    )
                                }
                            }
                            Text(
                                style = TextStyle(color = colorProvider),
                                text = locationItem.displayTimeZoneCityById(
                                    timeZoneId = locationItem.currentCityTimeZoneId ?: currentTimeZoneId
                                ).toString()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun GlanceModifier.cornerRadiusCompat(
        cornerRadius: Int,
        @ColorInt color: Int,
        @FloatRange(from = 0.0, to = 1.0) backgroundAlpha: Float = 1f
    ): GlanceModifier {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.background(Color(color).copy(alpha = backgroundAlpha))
                .cornerRadius(cornerRadius.dp)
        } else {
            val radii = FloatArray(8) { cornerRadius.toFloat() }
            val shape = ShapeDrawable(RoundRectShape(radii, null, null))
            shape.paint.color = ColorUtils.setAlphaComponent(color, (255 * backgroundAlpha).toInt())
            val bitmap = shape.toBitmap(width = 150, height = 75)
            this.background(imageProvider = ImageProvider(bitmap))
        }
    }
}
