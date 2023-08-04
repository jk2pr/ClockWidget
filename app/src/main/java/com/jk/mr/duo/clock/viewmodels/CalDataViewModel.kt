package com.jk.mr.duo.clock.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jk.mr.duo.clock.DispatcherProvider
import com.jk.mr.duo.clock.data.AddressSearchResult
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.repositories.CalRepository
import com.jk.mr.duo.clock.utils.PreferenceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class CalDataViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val calRepository: CalRepository,
    private var preferenceHandler: PreferenceHandler,
    private val flags: FlagResponse
) : ViewModel() {

    private val _dataList = mutableStateListOf<CalData>()
    val dataList: List<CalData> get() = _dataList
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun getData(searchResult: AddressSearchResult) =
        viewModelScope.launch(dispatchers.main) {
            flow {
                emit(UiState.Loading)
                val country = searchResult.searchAddress?.country ?: "Unknown"
                try {
                    val lat = searchResult.coordinate.latitude().toString()
                    val long = searchResult.coordinate.longitude().toString()
                    calRepository.getTimeZone(lat, long)
                        .let {
                            val localResource =
                                it.resourceSets.getOrNull(0)?.resources?.getOrNull(0)
                            val timeZoneId =
                                localResource?.timeZoneAtLocation?.first()?.timeZone?.ianaTimeZoneId
                                    ?: localResource?.timeZone?.ianaTimeZoneId
                            val abbreviation =
                                localResource?.timeZoneAtLocation?.first()?.timeZone?.abbreviation
                                    ?: localResource?.timeZone?.abbreviation

                            val flag = flags.data.firstOrNull { response ->
                                response.name == (
                                    searchResult.searchAddress?.country
                                        ?: searchResult.name
                                    )
                            }
                            val calData = CalData(
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
        jsonString?.let {
            val listType = object : TypeToken<List<CalData>>() {}.type
            Gson().fromJson<List<CalData>>(it, listType)?.let { list ->
                _dataList.clear()
                _dataList.addAll(list)
            }
        }
    }

    private fun addItems(newItem: List<CalData>) =
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

    fun onSelect(calData: CalData) {
        _dataList[_dataList.indexOf(calData)] =
            (if (calData.isSelected) calData.copy(isSelected = false) else calData.copy(isSelected = true))
    }

    fun doOnStop() =
        preferenceHandler.saveDateData(_dataList)

    fun arrange(calData: CalData) = viewModelScope.launch(dispatchers.main) {
        flow {
            emit(UiState.Loading)
            if (_dataList.isNotEmpty()) {
                Collections.swap(_dataList, 0, _dataList.indexOf(calData))
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
