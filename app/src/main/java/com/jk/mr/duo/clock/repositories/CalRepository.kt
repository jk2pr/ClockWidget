package com.jk.mr.duo.clock.repositories

import com.jk.mr.duo.clock.BuildConfig
import com.jk.mr.duo.clock.network.IApi

class CalRepository(private val api: IApi) {
    suspend fun getTimeZone(lat: String, long: String) = api.getTimeZoneFromLatLong(
        lat = lat,
        long = long,
        key = BuildConfig.BING_MAP_KEY
    )
}
