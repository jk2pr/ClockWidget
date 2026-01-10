package com.hoppers.duoclock

import android.app.Activity
import android.app.AlarmManager
import com.hoppers.duoclock.navigation.Start
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hoppers.duoclock.component.ComposeLocalWrapper
import com.hoppers.duoclock.theme.ClockTheme
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.core.annotation.KoinExperimentalAPI

class AppWidgetConfigureActivity : ComponentActivity() {

    @OptIn(KoinExperimentalAPI::class)
    public override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLocalWrapper {
                KoinAndroidContext {
                    ClockTheme { Start(this) }
                }
            }
        }
    }
}
