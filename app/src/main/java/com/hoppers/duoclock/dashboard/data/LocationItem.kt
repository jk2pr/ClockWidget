package com.hoppers.duoclock.dashboard.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.TimeZone
import java.util.UUID

@Serializable
data class LocationItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    var country: String,
    var remoteCityTimeZone: String?,
    var isSelected: Boolean = false,
    var isPinned: Boolean = false,
    val pinnedOrder: Int = -1, // 0..4
    val flag: String?,
    val displayName: String
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
