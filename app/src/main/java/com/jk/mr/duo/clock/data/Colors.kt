package com.jk.mr.duo.clock.data

import androidx.compose.ui.graphics.Color
import com.google.gson.Gson

val colors = listOf(
    ColorScheme(primaryColor = Color(color = 0xFF00796B), secondaryColor = Color(0xFFFFC107)),
    ColorScheme(primaryColor = Color(0xFF5D4037), secondaryColor = Color(0xFF607D8B)),
    ColorScheme(primaryColor = Color(0xFF303F9F), secondaryColor = Color(0xFFFF5252)),
    ColorScheme(primaryColor = Color(0xFFFFCC80), secondaryColor = Color(0xFFEF9A9A)),
    ColorScheme(primaryColor = Color(0xFFFFAB91), secondaryColor = Color(0xFF91E5FF)),
    ColorScheme(primaryColor = Color(0xFFFFC107), secondaryColor = Color(0xFF0745FF)),
    ColorScheme(primaryColor = Color(0xFFC8111C), secondaryColor = Color(0xFF11C8BC)),
    ColorScheme(primaryColor = Color(0xFFB39DDB), secondaryColor = Color(0xFFC5DB9D)),
)

data class ColorScheme(val primaryColor: Color, val secondaryColor: Color) {
    fun toJSON(): String {
        return Gson().toJson(this)
    }
}
