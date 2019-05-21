package com.jk.mr.duo.clock.services

import com.jk.mr.duo.clock.data.MResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface IApi {


       // @GET("maps/api/timezone/json")
        @GET("REST/v1/timezone/{location}")
        //fun getIp(@Query("mime") number: String): Observable<User>;
        fun getTimeZoneFromLatLong(@Path("location") query: String,
                                 //  @Query("timestamp") timestamp: String,
                                   @Query("key") key: String
                                   ): Observable<MResponse>




    }
