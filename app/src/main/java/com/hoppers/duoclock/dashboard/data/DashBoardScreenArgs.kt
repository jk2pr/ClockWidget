package com.hoppers.duoclock.dashboard.data

import com.hoppers.duoclock.search.Place
import kotlinx.coroutines.flow.StateFlow

data class DashBoardScreenArgs(
    val state: StateFlow<UiState>,
    val dataList: List<LocationItem>,
    val onRemove: () -> Unit = {},
    val reset: () -> Unit = {},
    val onDone: () -> Unit = {},
    val arrange: (LocationItem) -> Unit = {},
    val onSelect: (LocationItem) -> Unit = {},
    val onEvent: (Place) -> Unit = {},
    val appWidgetId: Int = 0,
    val onStart: () -> Unit = {},
    val onStop: () -> Unit = {}
)
