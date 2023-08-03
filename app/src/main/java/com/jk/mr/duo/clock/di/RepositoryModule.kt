package com.jk.mr.duo.clock.di

import android.content.Context
import com.google.gson.Gson
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.network.IApi
import com.jk.mr.duo.clock.repositories.CalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    fun getCalRepository(iApi: IApi): CalRepository = CalRepository(iApi)

    @Provides
    fun provideFlagData(@ApplicationContext context: Context): FlagResponse {
        var jsonString = ""
        try {
            jsonString = context.assets.open("flags/flag.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            print(ioException.message)
        }
        return Gson().fromJson(jsonString, FlagResponse::class.java)
    }
}
