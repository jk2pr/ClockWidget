package com.jk.mr.duo.clock.data

data class FlagResponse(
    val error: Boolean,
    val msg: String,
    val data: Data
)
data class Data(val name: String, val flag: String)
