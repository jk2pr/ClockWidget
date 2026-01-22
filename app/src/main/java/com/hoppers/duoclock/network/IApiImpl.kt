package com.hoppers.duoclock.network

import com.hoppers.duoclock.dashboard.data.TimeZoneResponse
import com.hoppers.duoclock.search.Place
import com.jk.mr.duo.clock.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class IApiImpl (private val client: HttpClient) : IApi {
    override suspend fun getTimeZoneFromLatLong(lat: String, long: String): TimeZoneResponse {
        return client.get {
            url(BuildConfig.GEONAME_URL)
            parameter("lat", lat)
            parameter("lng", long)
            parameter("username", "gogit")
        }.body()
    }

    override suspend fun getSearch(query: String): List<Place> {
        return client.get {
            url(BuildConfig.OPENSTREETMAP)
            parameter("q", query)
            parameter("accept-language", "en")
            parameter("format", "json")
        }.body()
    }
}
