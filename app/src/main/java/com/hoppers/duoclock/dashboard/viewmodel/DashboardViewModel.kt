package com.hoppers.duoclock.dashboard.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoppers.duoclock.DispatcherProvider
import com.hoppers.duoclock.dashboard.data.Country
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.internal.TimezoneMapper
import com.hoppers.duoclock.search.Place
import com.hoppers.duoclock.utils.PreferenceHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Collections
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private var preferenceHandler: PreferenceHandler,
    private val countries: List<Country>
) : ViewModel() {

    private val _dataList = mutableStateListOf<LocationItem>()
    val dataList: List<LocationItem> get() = _dataList
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun addLocationFromPlace(searchResult: Place) =
        viewModelScope.launch(dispatchers.main) {
            flow {
                emit(UiState.Loading)
                val country = run { searchResult.displayName.split(",").last().trim() }
                try {
                    val lat = searchResult.latitude
                    val long = searchResult.longitude
                    Log.d("Lat, Long", "$lat,$long")
                    val resultTimeZone =
                        TimezoneMapper.latLngToTimezoneString(lat.toDouble(), long.toDouble())
                    val flag = countries.firstOrNull { it.equals(country) }

                    val calData = LocationItem(
                        name = searchResult.name,
                        abbreviation = "abbreviation.orEmpty()",
                        address = country,
                        currentCityTimeZoneId = resultTimeZone,
                        flag = flag?.flag,
                        isSelected = false
                    )
                    val result = addItems(listOf(calData))
                    emit(UiState.Content(result))

                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(UiState.Error("Error"))
                }
            }.flowOn(dispatchers.main).collect {
                _uiState.value = it
            }
        }

    fun doOnStart() {
        val jsonString = preferenceHandler.getDateData()
        if (jsonString.isNullOrEmpty()) return
        jsonString.let {
            Json.decodeFromString<List<LocationItem>>(it).let { list ->
                _dataList.clear()
                _dataList.addAll(list)
            }
        }
    }

    private fun addItems(newItem: List<LocationItem>): String {
        return if (_dataList.containsAll(newItem)) {
            "Already added location, Please try with different location"
        } else {
            _dataList.addAll(0, newItem)
            "Added new City"
        }
    }

    fun removeItems() =
        viewModelScope.launch(dispatchers.main) {
            flow {
                emit(UiState.Loading)
                if (_dataList.size > 1 && !_dataList.first().isSelected) {
                    val selected = dataList.filter { it.isSelected }
                    val isSuccess = _dataList.removeAll(selected)
                    if (isSuccess) {
                        emit(UiState.Content("Deleted successfully"))
                    } else {
                        emit(UiState.Error("Fail to Delete"))
                    }
                } else {
                    emit(UiState.Error("Can't delete secondary clock"))
                }
                resetDataList()
            }.flowOn(dispatchers.main).collect {
                _uiState.value = it
            }
        }

    fun onSelect(locationItem: LocationItem) {
        val index = _dataList.indexOf(locationItem)
        if (index != -1) {
            val item = _dataList[index]
            _dataList[index] = item.copy(isSelected = !item.isSelected)
        }
    }

    fun doOnStop() =
        preferenceHandler.saveDateData(_dataList)

    fun arrange(locationItem: LocationItem) = viewModelScope.launch(dispatchers.main) {
        flow {
            emit(UiState.Loading)
            if (_dataList.isNotEmpty()) {
                Collections.swap(_dataList, 0, _dataList.indexOf(locationItem))
            }
            emit(UiState.Content("Moved to top"))
            resetDataList()
        }.flowOn(dispatchers.main).collect {
            _uiState.value = it
        }
    }

    fun onDone() = viewModelScope.launch {
        flow {
            emit(UiState.Loading)
            resetDataList()
            emit(UiState.Content("Done"))
        }.flowOn(dispatchers.main).collect {
            _uiState.value = it
        }
    }

    private fun resetDataList() {
        for (index in 0 until _dataList.size) {
            _dataList[index] = _dataList[index].copy(isSelected = false)
        }
    }

    fun resetState() {
        _uiState.value = UiState.Empty
    }
}
