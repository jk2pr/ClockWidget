package com.jk.mr.duo.clock.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

class ColorScheme(
    var isDark: Boolean = false,
    primaryColor: Color,
    secondaryColor: Color,
) {
    val colors: Colors =
        if (isDark) darkColors(
            primary = primaryColor, // Primary
            // primaryVariant = primaryColor.copy(alpha = 1.0f), // Status BAR AND NAVIGATION BAR
            secondary = secondaryColor
            // Color(0xFFBE2D34),
        ) else lightColors(
            primary = primaryColor.copy(alpha = 0.5f), // Primary
            secondary = secondaryColor
            // Color(0xFFBA6269),
        )
}

@Composable
fun ClockTheme(
    primaryColor: Color,
    secondaryColor: Color,
    content: @Composable() () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val lightPrimary = primaryColor.copy(alpha = 0.5f).toArgb()
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            (view.context as Activity).window.apply { ->
                statusBarColor =
                    if (isDark) primaryColor.toArgb() else lightPrimary
                navigationBarColor =
                    if (isDark) primaryColor.toArgb() else lightPrimary

                WindowCompat.getInsetsController(this, view).apply {
                    isAppearanceLightStatusBars = !isDark
                    isAppearanceLightNavigationBars = !isDark
                }
            }
        }
    }
    MaterialTheme(
        colors = ColorScheme(
            isDark = isDark,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor
        ).colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
