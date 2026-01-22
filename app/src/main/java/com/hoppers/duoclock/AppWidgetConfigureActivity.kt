package com.hoppers.duoclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.hoppers.duoclock.common.component.ComposeLocalWrapper
import com.hoppers.duoclock.navigation.Start
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
                    ClockTheme { Start() }
                }
            }
        }
    }
}
