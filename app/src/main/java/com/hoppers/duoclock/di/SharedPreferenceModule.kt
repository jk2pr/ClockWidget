package com.hoppers.duoclock.di

import com.hoppers.duoclock.utils.DataStorePreferenceHandler
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedPreferenceModule = module { single { DataStorePreferenceHandler(androidContext()) } }
