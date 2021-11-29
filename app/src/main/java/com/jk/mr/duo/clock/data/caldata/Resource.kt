package com.jk.mr.duo.clock.data.caldata

sealed class Resource {
    data class Success(val calData: CalData) : Resource()
    data class Error(val message: String) : Resource()
    object Loading : Resource()
    object Empty : Resource()
}
