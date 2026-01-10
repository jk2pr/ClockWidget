@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.hoppers.duoclock.dashboard.data

import kotlinx.serialization.Serializable

@Serializable
data class Country(
    val name: Name,
    val flag: String? = null
) {
     fun equals(country: String): Boolean {
        return country.equals(name.common, true) ||
                country.equals(name.official, true) ||
                name.nativeName.values.any { nativeName ->
                    nativeName.common.equals(country, true) ||
                            nativeName.official.equals(country, true)
                }
    }
}

@Serializable
data class Name(
    val common: String,
    val official: String,
    val nativeName: Map<String, NativeName> = emptyMap()
)

@Serializable
data class NativeName(
    val official: String,
    val common: String
)
