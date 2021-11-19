package com.jk.mr.duo.clock.data.caldata

import com.jk.mr.duo.clock.utils.Constants
import java.io.Serializable

data class CalData(
    val name: String,
    var address: String,
    var currentCityTimeZoneId: String?,
    var abbreviation: String,
    var isSelected: Boolean = false,
    val flag: String,
) : Serializable {
    override fun toString(): String {
        return "${address}${Constants.SEPARATOR}${name}${Constants.SEPARATOR}${currentCityTimeZoneId}${Constants.SEPARATOR}$abbreviation"
    }
}
