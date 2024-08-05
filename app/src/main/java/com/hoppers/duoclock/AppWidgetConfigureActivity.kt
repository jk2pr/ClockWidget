package com.hoppers.duoclock

import Start
import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hoppers.duoclock.component.ComposeLocalWrapper
import com.hoppers.duoclock.theme.ClockTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class AppWidgetConfigureActivity : ComponentActivity() {

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    @OptIn(KoinExperimentalAPI::class)
    public override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        intent.extras?.let {
            mAppWidgetId = it.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        setContent {
            ComposeLocalWrapper {
                KoinAndroidContext() {
                    ClockTheme { Start(this) }
                }
            }
        }
    }
}
