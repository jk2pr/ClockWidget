package com.hoppers.duoclock.di

import android.content.Context
import android.content.SharedPreferences
import com.hoppers.duoclock.utils.Constants
import com.hoppers.duoclock.utils.PreferenceHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPreferenceModule = module {
    single { provideSharedPreferences(androidContext()) }
    single { PreferenceHandler(get()) }
}

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
}
