package com.jk.mr.duo.clock.di.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.jk.mr.duo.clock.services.IApi
import okhttp3.Cache
import java.io.File
import java.io.IOException
import javax.inject.Inject


/**
 * Created by M2353204 on 07/08/2017.
 */

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun getRetrofit(): IApi {
        AppWidgetConfigureActivity.appComponent.inject(this)
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        /*val httpCacheDirectory = File(app.getCacheDir(), "responses")

        var cache: Cache? = null
        try {
            cache = Cache(httpCacheDirectory, 10 * 1024 * 1024)
        } catch (e: IOException) {
            e.printStackTrace()
        }*/

        val client = OkHttpClient.Builder()
                .addInterceptor(logging)

               /* .addInterceptor { chain ->
                    val ongoing = chain.request().newBuilder()
                    ongoing.addHeader("Accept", "application/json;versions=1")

//                    val preference=
                 //   val token= BuildConfig.VERSION_CODE
                  //  if (token!=null)
                //        ongoing.addHeader("Authorization", "AIzaSyCW17oz4_SD2armDEYVyi1U93vLsDXw-Lw")

                    chain.proceed(ongoing.build())
                }*/
             //   .cache(cache)
                .build()

        return Retrofit.Builder()
                //http://ip.jsontest.com/?mime=6
                .baseUrl("https://maps.googleapis.com/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(IApi::class.java)

    }


}