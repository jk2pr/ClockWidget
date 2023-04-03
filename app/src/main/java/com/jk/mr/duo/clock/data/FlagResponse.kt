package com.jk.mr.duo.clock.data

import kotlinx.serialization.Serializable

@Serializable
data class FlagResponse(
    val error: Boolean,
    val msg: String,
    val data: List<Data>
)
@Serializable
data class Data(val name: String, val flag: String, val iso2: String, val iso3: String)
