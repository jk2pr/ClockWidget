package com.jk.mr.duo.clock.extenstions

import com.jk.mr.duo.clock.data.caldata.CalData

fun List<CalData>.hasSwappableItem(): CalData? {
    val filtered = filter { it.isSelected }
    return if (filtered.size == 1 && !first().isSelected) filtered.first() else null
}
