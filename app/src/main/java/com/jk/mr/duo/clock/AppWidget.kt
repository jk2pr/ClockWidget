package com.jk.mr.duo.clock

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toBitmap
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.*
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.*
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.layout.*
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.google.gson.Gson
import com.jk.mr.duo.clock.data.ColorScheme
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants
import com.jk.mr.duo.clock.utils.Utils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [AppWidgetConfigureActivity]
 */
class AppWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    private val colorProvider = ColorProvider(night = Color.White, day = Color.White)
    private val currentTimeZoneId: String = TimeZone.getDefault().id
    private val cornerRadius = 8
    private val backgroundAlpha = 0.5f

    @Composable
    override fun Content() {
        val packageName = LocalContext.current.packageName
        val remoteViews0 = RemoteViews(packageName, R.layout.text_clock_widget)
        val remoteViews1 = RemoteViews(packageName, R.layout.text_clock_widget)
        val prefs = currentState<Preferences>()
        val calDataString = prefs[stringPreferencesKey("calData")]

        /* val currentNightMode =
             LocalContext.current.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
         val isNight = currentNightMode == UI_MODE_NIGHT_YES
       */


        if (calDataString != null) {
            val calData = Gson().fromJson(calDataString, CalData::class.java)
            val selectedTimeZone = calData.currentCityTimeZoneId ?: TimeZone.getDefault().id
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = GlanceModifier
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
                        remoteViews = remoteViews0,
                        containerViewId = View.NO_ID,
                        content = {
                            remoteViews0.apply {
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
                        text = calData.displayTimeZoneCityById().toString()
                    )
                }
                // Separator
                Spacer(
                    modifier = GlanceModifier.width(2.dp).fillMaxHeight().background(colorProvider)
                )
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AndroidRemoteViews(
                        remoteViews = remoteViews1,
                        containerViewId = View.NO_ID
                    ) {
                        remoteViews1.apply {
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
                        text = calData.displayTimeZoneCityById(
                            calData.currentCityTimeZoneId ?: currentTimeZoneId
                        ).toString()
                    )
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
            this.background(BitmapImageProvider(bitmap))
        }
    }
}

class GlanceWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AppWidget()
    private val coroutineScope = MainScope()
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.d("AppWidget ", "onReceive: Intent action ===   ${intent.action} ")
        if (intent.action == "UPDATE_ACTION") {
            coroutineScope.launch {
                glanceAppWidget.updateAll(context)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d("AppWidget", "onUpdate: Called")
        coroutineScope.launch {
            glanceAppWidget.updateAll(context)
        }
    }
}
