package com.hoppers.duoclock.di

import com.hoppers.duoclock.DefaultDispatchers
import com.hoppers.duoclock.DispatcherProvider
import com.hoppers.duoclock.network.IApi
import com.hoppers.duoclock.network.IApiImpl
import com.hoppers.duoclock.network.ktorHttpClient
import org.koin.dsl.module

val networkModule = module {
    single { ktorHttpClient }
    single<IApi> { IApiImpl(get()) }
    single<DispatcherProvider> { DefaultDispatchers() }
}