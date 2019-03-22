package com.jk.mr.duo.clock.services

import com.jk.mr.duo.clock.data.Results
import io.reactivex.Observable
import retrofit2.http.*


interface IApi {


        @GET("maps/api/timezone/json")
        //fun getIp(@Query("mime") number: String): Observable<User>;
        fun getTimeZoneFromLatLong(@Query("location") query: String,
                                   @Query("timestamp") timestamp: String,
                                   @Query("key") key: String
                                   ): Observable<Results>




    }
