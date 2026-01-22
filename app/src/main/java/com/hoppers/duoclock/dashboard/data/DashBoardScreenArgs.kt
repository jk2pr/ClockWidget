package com.hoppers.duoclock.dashboard.data

import com.hoppers.duoclock.search.Place
import kotlinx.coroutines.flow.StateFlow

data class DashBoardScreenArgs(
    val state: StateFlow<UiState>,
    val cityUiState: StateFlow<CitiesUiState>,
    val dialogState: StateFlow<DeleteDialogState>,
    val requestDelete: (LocationItem) -> Unit = {},
    val confirmDelete: () -> Unit = {},
    val cancelRemove: () -> Unit = {},
    val reset: () -> Unit = {},
    val onToggle: (LocationItem, onError: (String) -> Unit) -> Unit = { _, _ -> },
    val onSelect: (LocationItem) -> Unit = {},
    val onEvent: (Place) -> Unit = {},
)