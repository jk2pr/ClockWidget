package com.hoppers.duoclock.dashboard.data

@kotlinx.serialization.Serializable
data class ConvertedTime(

    val localTime: String,
    val utcOffsetWithDst: String,
    val timeZoneDisplayName: String,
    val timeZoneDisplayAbbr: String? = null
)

@kotlinx.serialization.Serializable
data class TimeZoneResponse(

    val authenticationResultCode: String,
    val brandLogoUri: String,
    val copyright: String,
    val resourceSets: List<ResourceSets>,
    val statusCode: Int,
    val statusDescription: String,
    val traceId: String
)

@kotlinx.serialization.Serializable
data class Resources(

    val __type: String,
    val timeZoneAtLocation: List<TimeZoneAtLocation>? = null,
    val timeZone: TimeZone // sometime timezone comes
)

@kotlinx.serialization.Serializable
data class TimeZoneAtLocation(

    val placeName: String,
    val timeZone: TimeZone
)

@kotlinx.serialization.Serializable
data class ResourceSets(

    val estimatedTotal: Int,
    val resources: List<Resources>
)

@kotlinx.serialization.Serializable
data class TimeZone(

    val genericName: String,

    val abbreviation: String? = null,
    val ianaTimeZoneId: String? = null,
    val windowsTimeZoneId: String,
    val utcOffset: String,
    val convertedTime: ConvertedTime
)
