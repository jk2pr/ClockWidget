package com.hoppers.duoclock.dashboard.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoppers.duoclock.DispatcherProvider
import com.hoppers.duoclock.dashboard.data.FlagResponse
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.dashboard.repositories.DashboardRepository
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
    private val dashboardRepository: DashboardRepository,
    private var preferenceHandler: PreferenceHandler,
    private val flags: FlagResponse
) : ViewModel() {

    private val _dataList = mutableStateListOf<LocationItem>()
    val dataList: List<LocationItem> get() = _dataList
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun getData(searchResult: Place) =
        viewModelScope.launch(dispatchers.main) {
            flow {
                emit(UiState.Loading)
                val country = run { searchResult.displayName.split(",").last().trim() }
                try {
                    val lat = searchResult.latitude
                    val long = searchResult.longitude
                    dashboardRepository.getTimeZone(lat, long)
                        .let {
                            val localResource =
                                it.resourceSets.getOrNull(0)?.resources?.getOrNull(0)
                            val timeZoneId =
                                localResource?.timeZoneAtLocation?.first()?.timeZone?.ianaTimeZoneId
                                    ?: localResource?.timeZone?.ianaTimeZoneId
                            val abbreviation =
                                localResource?.timeZoneAtLocation?.first()?.timeZone?.abbreviation
                                    ?: localResource?.timeZone?.abbreviation

                            val flag =
                                flags.data.firstOrNull { response -> response.name == country }
                            val calData = LocationItem(
                                name = searchResult.name,
                                abbreviation = abbreviation.orEmpty(),
                                address = country,
                                currentCityTimeZoneId = timeZoneId,
                                flag = flag?.flag,
                                isSelected = false
                            )
                            val result = addItems(listOf(calData))
                            emit(UiState.Content(result))
                        }
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

    private fun addItems(newItem: List<LocationItem>) =
        if (_dataList.containsAll(newItem)) {
            "Already added location, Please try with different location"
        } else {
            _dataList.addAll(0, newItem).also {
                return "Added new City"
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
        _dataList[_dataList.indexOf(locationItem)] =
            (if (locationItem.isSelected) locationItem.copy(isSelected = false) else locationItem.copy(
                isSelected = true
            ))
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
