package com.hoppers.duoclock.dashboard.repositories

import com.hoppers.duoclock.network.IApi
import com.jk.mr.duo.clock.BuildConfig

class DashboardRepository(private val api: IApi) {
    suspend fun getTimeZone(lat: String, long: String) = api.getTimeZoneFromLatLong(
        lat = lat,
        long = long,
        key = BuildConfig.BING_MAP_KEY
    )
}
