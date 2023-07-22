package com.jk.mr.duo.clock.data

import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchAddress
import java.io.Serializable

data class AddressSearchResult(
    val searchAddress: SearchAddress?,
    val name: String,
    val coordinate: Point
) : Serializable
