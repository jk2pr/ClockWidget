package com.jk.mr.duo.clock.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jk.mr.duo.clock.data.CalRepository
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.MResponse
import com.jk.mr.duo.clock.data.caldata.CalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalDataViewModel @Inject constructor(
    private val calRepository: CalRepository
) : ViewModel() {

    val mutableState = MutableLiveData<CalData>()

    fun getData(address: String, country: String, location: String) {
        viewModelScope.launch {
            try {
                val mResponse: MResponse = calRepository.getTimeZone(location)
                val requestBody: MutableMap<String, String> = HashMap()
                requestBody["country"] = country.trim()
                print(requestBody)
                val flagResponse: FlagResponse = calRepository.getFlag(requestBody)
                val timeZoneId = mResponse.resourceSets[0].resources[0].timeZone.ianaTimeZoneId
                val abbreviation = mResponse.resourceSets[0].resources[0].timeZone.windowsTimeZoneId
                val calData = CalData(address, country, timeZoneId, abbreviation, false, flagResponse.data.flag)
                mutableState.postValue(calData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        /*   fun getTimeZone(address: String, country: String, lat: String, long: String) {

        // val tsLong = System.currentTimeMillis() / 1000
        //  val ts = tsLong.toString()
        val location = lat.plus(",").plus(long)
        val subscribeOn = calRepository.getTimeZone(lat.plus(",").plus(long))
            .subscribeOn(Schedulers.io())
            .flatMap {

                api.getFlag(query = countryString)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ abc ->
                val timeZoneId =
                    abc.resourceSets[0].resources[0].timeZone.convertedTime.timeZoneDisplayName
                val abbreviation = abc.resourceSets[0].resources[0].timeZone.windowsTimeZoneId
                print("abbreviation $abbreviation")
                sendBackResult(
                    address.plus(Constants.SEPARATOR).plus(country).plus(Constants.SEPARATOR).plus(timeZoneId)
                        .plus(Constants.SEPARATOR).plus(abbreviation)
                )
            }) {}
        subscriptions.add(subscribeOn)
    }*/
    }
}
