package com.jk.mr.duo.clock.data

sealed class UiState {

    data class Content(val data: Any, val tag: Long = System.currentTimeMillis()) : UiState()
    data class Error(val message: String, val tag: Long = System.currentTimeMillis()) : UiState()
    object Loading : UiState()
    object Empty : UiState()
}
