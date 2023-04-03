package com.jk.mr.duo.clock.network

import com.jk.mr.duo.clock.data.MResponse
import io.ktor.client.*
import io.ktor.client.engine.android.*
import retrofit2.http.*

interface IApi {

    @GET("REST/v1/timezone/{lat},{long}")
    suspend fun getTimeZoneFromLatLong(
        @Path("lat") lat: String,
        @Path("long") long: String,
        @Query("key") key: String,
    ): MResponse
}
// https://countriesnow.space/api/v0.1/countries/flag/images
