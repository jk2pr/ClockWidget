package com.hoppers.duoclock.dashboard.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.TimeZone

@Serializable
data class LocationItem(
    val name: String,
    var address: String,
    var currentCityTimeZoneId: String?,
    var abbreviation: String,
    var isSelected: Boolean = false,
    val flag: String?
) {
    fun toJSON(): String {
        return Json.encodeToString(this)
    }

    fun displayTimeZoneCityById(timeZoneId: String = TimeZone.getDefault().id): StringBuilder {
        var timeZoneDefaultClock = StringBuilder(timeZoneId)
        if (timeZoneDefaultClock.contains("/")) {
            timeZoneDefaultClock =
                StringBuilder(
                    timeZoneDefaultClock.toString().split("/")[1].replace("_", " ").trim()
                )
        }
        if (timeZoneDefaultClock.split(" ").size > 2) {
            timeZoneDefaultClock =
                timeZoneDefaultClock.replace(
                    timeZoneDefaultClock.lastIndexOf(" "),
                    timeZoneDefaultClock.lastIndexOf(" ") + 1,
                    "\n"
                )
        }

        return timeZoneDefaultClock
    }
}
