package com.jk.mr.duo.clock.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

interface ComplexViewModelInterface

@HiltViewModel
class CalDataViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val calRepository: CalRepository,
    var preferenceHandler: PreferenceHandler,
    private val flags: FlagResponse
) : ViewModel(), ComplexViewModelInterface {

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    companion object {
        val calDataViewModel by lazy {
            object : ComplexViewModelInterface {
            }
        }
    }

    fun getData(searchResult: AddressSearchResult, lat: String, long: String) =
        viewModelScope.launch(dispatchers.main) {
            flow {
                emit(UiState.Loading)
                val country = searchResult.searchAddress?.country ?: "Unknown"
                try {
                    calRepository.getTimeZone(lat, long)
                        .let {
                            val localResource = it.resourceSets.getOrNull(0)?.resources?.getOrNull(0)
                            val timeZoneId =
                                localResource?.timeZoneAtLocation?.first()?.timeZone?.ianaTimeZoneId
                                    ?: localResource?.timeZone?.ianaTimeZoneId
                            val traceId = it.traceId // [0].resources[0].trimeZone.tra
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
                            emit(UiState.Content(calData))
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emit(UiState.Error("Error"))
                }
            }.flowOn(dispatchers.main).collect {
                _uiState.value = it
            }
        }
}
