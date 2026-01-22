package com.hoppers.duoclock.dashboard.data

sealed interface DeleteDialogState {
    object Hidden : DeleteDialogState
    data class Confirm(val item: LocationItem) : DeleteDialogState
}