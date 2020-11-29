package com.jk.mr.duo.clock.network

import com.jk.mr.duo.clock.data.MResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IApi {

    @GET("REST/v1/timezone/{location}")
    fun getTimeZoneFromLatLong(
        @Path("location") query: String,
        @Query("key") key: String
    ): Observable<MResponse>
}
