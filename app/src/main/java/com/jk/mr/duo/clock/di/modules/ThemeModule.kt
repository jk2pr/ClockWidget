package com.jk.mr.duo.clock.di.modules

import com.jk.mr.duo.clock.utils.AppWidgetHelper
import com.jk.mr.duo.clock.utils.PreferenceHandler
import com.jk.mr.duo.clock.utils.ThemeHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ThemeModule {
    @Provides
    @Singleton
    fun getThemeHandler() = ThemeHandler()

    @Provides
    @Singleton
    fun getWidgetHelper(pref: PreferenceHandler, themeHandler: ThemeHandler) = AppWidgetHelper(pref, themeHandler)
}
