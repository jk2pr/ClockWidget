/*
package com.hoppers.duoclock.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.glance.material3.ColorProviders

private val lightColorScheme = lightColorScheme(
    primary = Primary80,
    secondary = Secondary80,
    tertiary = Tertiary80,
    surface = Surface80,
    surfaceVariant = SurfaceVariant80,
    outline = Outline80

)

private val darkColorScheme = darkColorScheme(
    primary = Primary40,
    primaryContainer = PrimaryContainerColor40,
    onPrimary = OnPrimary40,
    secondary = Secondary40,
    tertiary = Tertiary40,
    surface = Surface40,
    surfaceVariant = SurfaceVariant40,
    outline = Outline40

    */
/* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    *//*

)

object ClockGlanceColorScheme {

    val colors = ColorProviders(
        light = lightColorScheme,
        dark = darkColorScheme
    )
}

@Composable
fun ClockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    MaterialTheme(

        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
*/
