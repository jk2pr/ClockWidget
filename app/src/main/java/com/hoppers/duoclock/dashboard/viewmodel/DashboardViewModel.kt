package com.hoppers.duoclock.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.hoppers.duoclock.DispatcherProvider
import com.hoppers.duoclock.appwidget.WidgetUpdater
import com.hoppers.duoclock.dashboard.components.WidgetPinner
import com.hoppers.duoclock.dashboard.data.CitiesUiState
import com.hoppers.duoclock.dashboard.data.Country
import com.hoppers.duoclock.dashboard.data.DeleteDialogState
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.internal.TimezoneMapper
import com.hoppers.duoclock.search.Place
import com.hoppers.duoclock.utils.Constants.MAX_PINNED
import com.hoppers.duoclock.utils.DataStorePreferenceHandler
import com.hoppers.duoclock.utils.isFromWidgetAddFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val dispatchers: DispatcherProvider,
    private val dataStore: DataStorePreferenceHandler,
    private val countries: List<Country>,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _deleteDialogState =
        MutableStateFlow<DeleteDialogState>(DeleteDialogState.Hidden)
    val dialogState = _deleteDialogState.asStateFlow()

    // 🔑 SINGLE SOURCE OF TRUTH
    val citiesUiState: StateFlow<CitiesUiState> =
        dataStore.citiesFlow
            .map<List<LocationItem>, CitiesUiState> { cities ->
                CitiesUiState.Ready(cities)
            }
            .onStart {
                emit(CitiesUiState.Loading)
             //   delay(2000)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = CitiesUiState.Loading
            )

    /* ---------------- ADD CITY ---------------- */

    fun addLocationFromPlace(searchResult: Place) {
        viewModelScope.launch(dispatchers.main) {
            _uiState.value = UiState.Loading

            try {
                val country = searchResult.displayName.split(",").last().trim()
                val timeZone = TimezoneMapper.latLngToTimezoneString(
                    searchResult.latitude.toDouble(),
                    searchResult.longitude.toDouble()
                )

                val flag = countries.firstOrNull { it.equals(country) }

                val newCity = LocationItem(
                    name = searchResult.name,
                    displayName = searchResult.displayName,
                    country = country,
                    remoteCityTimeZone = timeZone,
                    flag = flag?.flag,
                    isSelected = false
                )

                val current = dataStore.citiesFlow.first()
                val wasEmpty = current.isEmpty()

                val updated = buildList {
                    add(if (wasEmpty) newCity.copy(isPinned = true) else newCity)
                    addAll(current)
                }

                persistAndUpdateWidget(updated)

             //   if (wasEmpty) triggerWidgetPinIfNeeded()

                _uiState.value = UiState.Content("Added new city")

            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error")
            }
        }
    }

    /* ---------------- DELETE ---------------- */

    fun requestRemove(item: LocationItem) {
        if (item.isPinned) {
            _uiState.value = UiState.Error("Pinned clocks cannot be deleted")
            return
        }
        _deleteDialogState.value = DeleteDialogState.Confirm(item)
    }

    fun confirmRemove() {
        val state = _deleteDialogState.value
        if (state !is DeleteDialogState.Confirm) return

        viewModelScope.launch {
            val cities = dataStore.citiesFlow.first()
            val updated = cities.filterNot { it.id == state.item.id }

            persistAndUpdateWidget(updated)
            _deleteDialogState.value = DeleteDialogState.Hidden
        }
    }

    fun cancelRemove() {
        _deleteDialogState.value = DeleteDialogState.Hidden
    }

    /* ---------------- PIN / UNPIN ---------------- */

    fun onTogglePinned(item: LocationItem, onError: (String) -> Unit) {
        viewModelScope.launch {
            val cities = dataStore.citiesFlow.first()
            val pinnedCount = cities.count { it.isPinned }

            val updated = cities.map {
                if (it.id != item.id) return@map it

                when {
                    it.isPinned && pinnedCount == 1 -> {
                        onError("At least one clock must stay pinned")
                        return@launch
                    }

                    !it.isPinned && pinnedCount >= MAX_PINNED -> {
                        onError("You can pin up to $MAX_PINNED clocks only")
                        return@launch
                    }

                    else -> it.copy(isPinned = !it.isPinned)
                }
            }

            val safeList =
                if (updated.none { it.isPinned } && updated.isNotEmpty()) {
                    updated.toMutableList().apply {
                        this[0] = this[0].copy(isPinned = true)
                    }
                } else updated

            persistAndUpdateWidget(safeList)
        }
    }

    /* ---------------- SHARED HELPERS ---------------- */

    private suspend fun persistAndUpdateWidget(list: List<LocationItem>) {
        dataStore.saveCities(list)
        WidgetUpdater.updateNow(getApplication())
    }

    private fun triggerWidgetPinIfNeeded() {
        viewModelScope.launch {
            if (!dataStore.shouldPromptForWidget()) return@launch

            // 🚫 DO NOT prompt if widget is already being added
            if (application.isFromWidgetAddFlow()) return@launch

            WidgetPinner.requestPin(application)
            dataStore.markPromptShown()
        }
    }
}