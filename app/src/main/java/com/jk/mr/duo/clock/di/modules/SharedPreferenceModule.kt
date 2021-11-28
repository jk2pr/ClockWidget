package com.jk.mr.duo.clock.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.jk.mr.duo.clock.utils.Constants
import com.jk.mr.duo.clock.utils.PreferenceHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferenceModule {

    @Provides
    @Singleton
    fun getSharedPreferences(@ApplicationContext context: Context): SharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, 0)

    @Provides
    @Singleton
    fun getPreferenceHandler(pref: SharedPreferences) = PreferenceHandler(pref)
}
