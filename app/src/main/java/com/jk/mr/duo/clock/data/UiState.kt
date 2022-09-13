package com.jk.mr.duo.clock.data

sealed class UiState {
    data class Content(val data: Any) : UiState()
    data class Error(val message: String) : UiState()
    object Loading : UiState()
    object Empty : UiState()
}