package com.hoppers.duoclock.dashboard.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: Name,
    val flag: String
)

@Serializable
data class Name(
    val common: String,
    val official: String,
    @SerialName("nativeName") val nativeName: Map<String, NativeName>
)

@Serializable
data class NativeName(
    val official: String,
    val common: String
)
