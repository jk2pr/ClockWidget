package com.jk.mr.duo.clock

import android.app.Application
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants.ACTION_ADD_CLOCK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MrDuoClockApplication : Application() {

    override fun onCreate() {
        super.onCreate()
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
