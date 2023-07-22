package com.jk.mr.duo.clock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jk.mr.duo.clock.DispatcherProvider
import com.jk.mr.duo.clock.data.AddressSearchResult
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.MResponse
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.repositories.CalRepository
import com.jk.mr.duo.clock.utils.PreferenceHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface ComplexViewModelInterface

@HiltViewModel
class CalDataViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val calRepository: CalRepository,
    var preferenceHandler: PreferenceHandler,
    private val flags: FlagResponse,
) : ViewModel(), ComplexViewModelInterface {

    val uiState = MutableStateFlow<UiState>(UiState.Empty)

    companion object {
        val calDataViewModel by lazy {
            object : ComplexViewModelInterface {
            }
        }
    }

    fun getData(searchResult: AddressSearchResult, lat: String, long: String) =
        viewModelScope.launch {
            flow {
                emit(UiState.Loading)
                var mResponse: MResponse? = null
                // var flagResponse: FlagResponse? = null
                val country = searchResult.searchAddress?.country ?: "Unknown"
                try {
                    mResponse = calRepository.getTimeZone(lat, long)
                    // flagResponse = calRepository.getFlag(countryString = country)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    mResponse?.let {
                        val localResource = it.resourceSets.getOrNull(0)?.resources?.getOrNull(0)
                        val timeZoneId =
                            localResource?.timeZoneAtLocation?.first()?.timeZone?.ianaTimeZoneId
                                ?: localResource?.timeZone?.ianaTimeZoneId
                        val traceId = it.traceId // [0].resources[0].trimeZone.tra
                        val abbreviation =
                            localResource?.timeZoneAtLocation?.first()?.timeZone?.abbreviation
                                ?: localResource?.timeZone?.abbreviation

                        val flag = flags.data.firstOrNull { response ->
                            response.name == (searchResult.searchAddress?.country
                                ?: searchResult.name)
                        }
                        val calData = CalData(
                            name = searchResult.name,
                            abbreviation = abbreviation.orEmpty(),
                            address = country,
                            currentCityTimeZoneId = timeZoneId,
                            flag = flag?.flag,
                            isSelected = false
                        )
                        emit(UiState.Content(calData))
                    } ?: emit(UiState.Error("Error"))
                }
            }.flowOn(dispatchers.main).collect {
                uiState.value = it
            }
        }
}
