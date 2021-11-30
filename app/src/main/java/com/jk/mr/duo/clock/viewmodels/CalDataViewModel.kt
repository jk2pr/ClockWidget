package com.jk.mr.duo.clock.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jk.mr.duo.clock.data.CalRepository
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.MResponse
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.data.caldata.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalDataViewModel @Inject constructor(
    private val calRepository: CalRepository
) : ViewModel() {

    val mutableState = MutableLiveData<Resource>()

    fun getData(address: String, country: String, location: String) = viewModelScope.launch {

        flow {
            emit(Resource.Loading)
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
                    val abbreviation =
                        it.resourceSets[0].resources[0].timeZone.windowsTimeZoneId
                    val calData = CalData(
                        address,
                        country,
                        timeZoneId,
                        abbreviation,
                        false,
                        flagResponse?.data?.flag
                    )
                    emit(Resource.Success(calData))
                } ?: emit(Resource.Error("Error"))
            }
        }.flowOn(Dispatchers.IO).collect {
            mutableState.value = it
        }
    }
}
