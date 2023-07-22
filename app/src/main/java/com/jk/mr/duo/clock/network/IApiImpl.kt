package com.jk.mr.duo.clock.network

import com.jk.mr.duo.clock.BuildConfig
import com.jk.mr.duo.clock.data.MResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import javax.inject.Inject

class IApiImpl @Inject constructor(private val client: HttpClient) : IApi {
    override suspend fun getTimeZoneFromLatLong(lat: String, long: String, key: String): MResponse {
        return client.get {
            url(BuildConfig.MICROSOFT_TIMEZONE_BASE_URL + "REST/v1/timezone/$lat,$long")
            parameter("key", key)
        }.body()
    }
}
