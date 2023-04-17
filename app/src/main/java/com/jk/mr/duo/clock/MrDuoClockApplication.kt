package com.jk.mr.duo.clock

import android.app.Application
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.gson.Gson
import com.jk.mr.duo.clock.data.ColorScheme
import com.jk.mr.duo.clock.data.colors
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants.ACTION_ADD_CLOCK
import com.jk.mr.duo.clock.utils.PreferenceHandler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MrDuoClockApplication : Application() {

    var defaultScheme: ColorScheme by mutableStateOf(colors[0])

    @Inject
    lateinit var preferenceHandler: PreferenceHandler
    fun changeColorScheme(newPair: ColorScheme) {
        defaultScheme = newPair
    }

    override fun onCreate() {
        super.onCreate()

        val savedScheme =
            Gson().fromJson(preferenceHandler.getAppColorScheme(), ColorScheme::class.java)
        if (savedScheme == null) {
            // First Launch
            preferenceHandler.saveAppColorScheme(defaultScheme.toJSON())
        } else {
            defaultScheme = savedScheme
        }

        val it =
            Intent(this, AppWidgetConfigureActivity::class.java).apply { action = ACTION_ADD_CLOCK }
        val shortcut = ShortcutInfoCompat.Builder(this, "id1")
            .setShortLabel("Clock")
            .setLongLabel("Add new Clock")
            .setIcon(IconCompat.createWithResource(this, R.drawable.ic_add_circle_black_24dp))
            .setIntent(it)
            .build()

        ShortcutManagerCompat.addDynamicShortcuts(this, listOf(shortcut))
    }

    // }
}
