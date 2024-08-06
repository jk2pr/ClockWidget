package com.hoppers.duoclock.network

import com.hoppers.duoclock.dashboard.data.TimeZoneResponse
import com.hoppers.duoclock.search.Place
import com.jk.mr.duo.clock.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import javax.inject.Inject

class IApiImpl @Inject constructor(private val client: HttpClient) : IApi {
    override suspend fun getTimeZoneFromLatLong(lat: String, long: String, key: String): TimeZoneResponse {
        return client.get {
            url(BuildConfig.MICROSOFT_TIMEZONE_BASE_URL + "REST/v1/timezone/$lat,$long")
            parameter("key", key)
        }.body()
    }

    override suspend fun getSearch(query: String): List<Place> {
        return client.get {
            url(BuildConfig.OPENSTREETMAP + "search?q=$query&format=json")
        }.body()
    }
}
