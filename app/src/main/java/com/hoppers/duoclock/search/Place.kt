package com.hoppers.duoclock.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Place(
    @SerialName("place_id") val placeId: Long,
    @SerialName("licence") val license: String,
    @SerialName("osm_type") val osmType: String,
    @SerialName("osm_id") val osmId: Long,
    @SerialName("lat") val latitude: String,
    @SerialName("lon") val longitude: String,
    @SerialName("class") val className: String,
    @SerialName("type") val type: String,
    @SerialName("place_rank") val placeRank: Int,
    @SerialName("importance") val importance: Double,
    @SerialName("addresstype") val addressType: String,
    @SerialName("name") val name: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("boundingbox") val boundingBox: List<String>
) : java.io.Serializable
