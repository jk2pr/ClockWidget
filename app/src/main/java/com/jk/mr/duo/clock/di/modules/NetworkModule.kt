package com.jk.mr.duo.clock.di.modules

import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.services.IApi
import com.jk.mr.duo.clock.utils.Constants.appComponent
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun getRetrofit(): IApi {
        appComponent.inject(this)
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
               // .baseUrl("https://maps.googleapis.com/") Google
                .baseUrl("https://dev.virtualearth.net/") // Microsoft
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(IApi::class.java)

    }

}