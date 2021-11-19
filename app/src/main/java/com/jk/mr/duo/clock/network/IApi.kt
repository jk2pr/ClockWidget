package com.jk.mr.duo.clock.network

import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.MResponse
import retrofit2.http.*

interface IApi {

    @GET("REST/v1/timezone/{location}")
    suspend fun getTimeZoneFromLatLong(
        @Path("location") query: String,
        @Query("key") key: String
    ): MResponse

    // https://countriesnow.space/api/v0.1/countries/flag/images

    @POST
    suspend fun getFlag(
        @Url url: String = "https://countriesnow.space/api/v0.1/countries/flag/images",
        @Body query: MutableMap<String, String>
    ): FlagResponse
}
