package com.hoppers.duoclock.extenstions

import com.hoppers.duoclock.dashboard.data.LocationItem

fun List<LocationItem>.hasSwappableItem(): LocationItem? {
    val filtered = filter { it.isSelected }
    return if (filtered.size == 1 && !first().isSelected) filtered.first() else null
}