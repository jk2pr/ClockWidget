package com.jk.mr.duo.clock.di.modules

import com.jk.mr.duo.clock.BuildConfig
import com.jk.mr.duo.clock.network.IApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun getRetrofit(): IApi {
        val logging = HttpLoggingInterceptor()
        logging.level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                response
            }.addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.MICROSOFT_TIMEZONE_BASE_URL) // Microsoft
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(IApi::class.java)
    }
}
