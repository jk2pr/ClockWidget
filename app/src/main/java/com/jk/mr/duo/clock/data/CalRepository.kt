package com.jk.mr.duo.clock.data

import com.jk.mr.duo.clock.BuildConfig
import com.jk.mr.duo.clock.network.IApi

class CalRepository(private val api: IApi) {
    suspend fun getTimeZone(location: String) = api.getTimeZoneFromLatLong(location, BuildConfig.BING_MAP_KEY)
    suspend fun getFlag(countryString: MutableMap<String, String>) = api.getFlag(query = countryString)
}
