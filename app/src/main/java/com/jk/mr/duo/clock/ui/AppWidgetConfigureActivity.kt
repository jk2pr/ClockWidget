package com.jk.mr.duo.clock.ui

import Start
import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.component.ComposeLocalWrapper
import com.jk.mr.duo.clock.ui.theme.ClockTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
@AndroidEntryPoint
class AppWidgetConfigureActivity : ComponentActivity() {

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        intent.extras?.let {
            mAppWidgetId = it.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        setContent {
            ComposeLocalWrapper {
                ClockTheme { Start(this) }
            }
        }
    }
}
