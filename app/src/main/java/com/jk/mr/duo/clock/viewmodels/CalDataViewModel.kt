package com.jk.mr.duo.clock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jk.mr.duo.clock.data.CalRepository
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.MResponse
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.data.caldata.CalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ComplexViewModelInterface {

}

@HiltViewModel
class CalDataViewModel @Inject constructor(
    private val calRepository: CalRepository,
) : ViewModel(), ComplexViewModelInterface {

    var data: MutableList<CalData> = mutableListOf()
    val uiState = MutableStateFlow<UiState>(UiState.Empty)

    companion object {
        val calDataViewModel by lazy {
            object : ComplexViewModelInterface {

            }
        }
    }

    fun getData(address: String, country: String, location: String) = viewModelScope.launch {

        flow {
            emit(UiState.Loading)
            var mResponse: MResponse? = null
            var flagResponse: FlagResponse? = null
            try {
                mResponse = calRepository.getTimeZone(location)
                val requestBody: MutableMap<String, String> = HashMap()
                requestBody["country"] = country.trim()
                flagResponse = calRepository.getFlag(requestBody)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mResponse?.let {
                    val timeZoneId = it.resourceSets[0].resources[0].timeZone.ianaTimeZoneId
                    val traceId = it.traceId//[0].resources[0].trimeZone.tra
                    val abbreviation =
                        it.resourceSets[0].resources[0].timeZone.windowsTimeZoneId
                    val calData = CalData(name = address,
                        abbreviation = abbreviation,
                        address = country,
                        currentCityTimeZoneId = timeZoneId,
                        flag = flagResponse?.data?.flag,
                        isSelected = false,
                        )
                    emit(UiState.Content(calData))
                } ?: emit(UiState.Error("Error"))
            }
        }.flowOn(Dispatchers.IO).collect {
            uiState.value = it
        }
    }
}
