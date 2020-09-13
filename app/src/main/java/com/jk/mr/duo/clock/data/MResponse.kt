package com.jk.mr.duo.clock.data

import com.google.gson.annotations.SerializedName

data class ConvertedTime(

    @SerializedName("localTime") val localTime: String,
    @SerializedName("utcOffsetWithDst") val utcOffsetWithDst: String,
    @SerializedName("timeZoneDisplayName") val timeZoneDisplayName: String,
    @SerializedName("timeZoneDisplayAbbr") val timeZoneDisplayAbbr: String
)

data class MResponse(

    @SerializedName("authenticationResultCode") val authenticationResultCode: String,
    @SerializedName("brandLogoUri") val brandLogoUri: String,
    @SerializedName("copyright") val copyright: String,
    @SerializedName("resourceSets") val resourceSets: List<ResourceSets>,
    @SerializedName("statusCode") val statusCode: Int,
    @SerializedName("statusDescription") val statusDescription: String,
    @SerializedName("traceId") val traceId: String
)
data class Resources(

    @SerializedName("__type") val __type: String,
    @SerializedName("timeZone") val timeZone: TimeZone
)
data class ResourceSets(

    @SerializedName("estimatedTotal") val estimatedTotal: Int,
    @SerializedName("resources") val resources: List<Resources>
)

data class TimeZone(

    @SerializedName("genericName") val genericName: String,
    @SerializedName("abbreviation") val abbreviation: String?,
    @SerializedName("ianaTimeZoneId") val ianaTimeZoneId: String?,
    @SerializedName("windowsTimeZoneId") val windowsTimeZoneId: String,
    @SerializedName("utcOffset") val utcOffset: String,
    @SerializedName("convertedTime") val convertedTime: ConvertedTime
)
