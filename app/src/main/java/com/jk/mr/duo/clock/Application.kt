package com.jk.mr.duo.clock

import android.app.Application
import timber.log.Timber

class Application:Application(){


    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}