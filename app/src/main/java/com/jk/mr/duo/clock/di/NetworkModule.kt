package com.jk.mr.duo.clock.di

import com.jk.mr.duo.clock.DefaultDispatchers
import com.jk.mr.duo.clock.DispatcherProvider
import com.jk.mr.duo.clock.network.IApi
import com.jk.mr.duo.clock.network.IApiImpl
import com.jk.mr.duo.clock.network.ktorHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun getIApiImpl(client: HttpClient): IApi = IApiImpl(client)

    @Provides
    @Singleton
    fun getHttpClient() = ktorHttpClient

    @Provides
    @Singleton
    fun getDispatcher(): DispatcherProvider = DefaultDispatchers()
}
