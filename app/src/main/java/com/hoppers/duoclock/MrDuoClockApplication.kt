package com.hoppers.duoclock

import android.app.Application
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.hoppers.duoclock.di.networkModule
import com.hoppers.duoclock.di.repositoryModule
import com.hoppers.duoclock.di.sharedPreferenceModule
import com.hoppers.duoclock.utils.Constants.ACTION_ADD_CLOCK
import com.jk.mr.duo.clock.R
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MrDuoClockApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MrDuoClockApplication)
            modules(networkModule, repositoryModule, sharedPreferenceModule)
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
