package com.hoppers.duoclock.di

import android.content.Context
import com.hoppers.duoclock.dashboard.data.FlagResponse
import com.hoppers.duoclock.dashboard.repositories.DashboardRepository
import com.hoppers.duoclock.dashboard.viewmodel.DashboardViewModel
import com.hoppers.duoclock.search.repositories.SearchRepository
import com.hoppers.duoclock.search.viewmodel.SearchViewModel
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.IOException

val repositoryModule = module {
    single { provideFlagData(androidContext()) }
    single { DashboardRepository(get()) }
    single { SearchRepository(get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel {
        DashboardViewModel(
            dispatchers = get(),
            dashboardRepository = get(),
            preferenceHandler = get(),
            flags = get()
        )
    }
}

fun provideFlagData(context: Context): FlagResponse {
    var jsonString = ""
    try {
        jsonString = context.assets.open("flags/flag.json")
            .bufferedReader()
            .use { it.readText() }
    } catch (ioException: IOException) {
        print(ioException.message)
    }
    return decodeFromString(jsonString)
}
