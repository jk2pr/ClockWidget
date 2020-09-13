package com.jk.mr.duo.clock.di.components

import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.di.modules.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ (NetworkModule::class)])
interface AppComponent {
    fun inject(auth: AppWidgetConfigureActivity)
    fun inject(net: NetworkModule)
}
