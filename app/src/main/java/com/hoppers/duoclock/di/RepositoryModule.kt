package com.hoppers.duoclock.di

import android.content.Context
import com.hoppers.duoclock.dashboard.data.Country
import com.hoppers.duoclock.dashboard.repositories.DashboardRepository
import com.hoppers.duoclock.dashboard.viewmodel.DashboardViewModel
import com.hoppers.duoclock.search.repositories.SearchRepository
import com.hoppers.duoclock.search.viewmodel.SearchViewModel
import kotlinx.serialization.json.Json.Default.decodeFromString
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

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
            countries = get()
        )
    }
}

fun provideFlagData(context: Context): List<Country> {
    return runCatching {
        val jsonString = context.assets.open("flags/flag.json")
            .bufferedReader()
            .use { it.readText() }
        return decodeFromString(jsonString)
    }.getOrNull() ?: emptyList()
}
