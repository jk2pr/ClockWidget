package com.jk.mr.duo.clock.data.caldata

import com.google.gson.Gson
import java.io.Serializable
import java.util.*

data class CalData(
    val name: String,
    var address: String,
    var currentCityTimeZoneId: String?,
    var abbreviation: String,
    var isSelected: Boolean = false,
    val flag: String?
) : Serializable {
    fun toJSON(): String {
        return Gson().toJson(this)
    }
    fun displayTimeZoneCityById(timeZoneId: String = TimeZone.getDefault().id): StringBuilder {
        var timeZoneDefaultClock = StringBuilder(timeZoneId)
        if (timeZoneDefaultClock.contains("/")) {
            timeZoneDefaultClock =
                StringBuilder(timeZoneDefaultClock.toString().split("/")[1].replace("_", " ").trim())
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
