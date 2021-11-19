package com.jk.mr.duo.clock.di.modules

import com.jk.mr.duo.clock.data.CalRepository
import com.jk.mr.duo.clock.network.IApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    fun getCalRepository(iApi: IApi) = CalRepository(iApi)
}
