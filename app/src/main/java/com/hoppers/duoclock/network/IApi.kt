package com.hoppers.duoclock.network

import com.hoppers.duoclock.dashboard.data.TimeZoneResponse
import com.hoppers.duoclock.search.Place

interface IApi {

    suspend fun getTimeZoneFromLatLong(lat: String, long: String, key: String): TimeZoneResponse
    suspend fun getSearch(query: String): List<Place>
}
// https://countriesnow.space/api/v0.1/countries/flag/images
