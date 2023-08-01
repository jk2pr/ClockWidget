package com.jk.mr.duo.clock.data.caldata

import com.jk.mr.duo.clock.data.AddressSearchResult
import com.jk.mr.duo.clock.data.UiState
import kotlinx.coroutines.flow.StateFlow

data class DashBoardScreenArgs(
    val state: StateFlow<UiState>,
    val dataList: List<CalData>,
    val onRemove: () -> Unit = {},
    val reset: () -> Unit = {},
    val onDone: () -> Unit = {},
    val arrange: (CalData) -> Unit = {},
    val onSelect: (CalData) -> Unit = {},
    val onEvent: (AddressSearchResult) -> Unit = {},
    val appWidgetId: Int = 0,
    val onStart: () -> Unit = {},
    val onStop: () -> Unit = {}
)
