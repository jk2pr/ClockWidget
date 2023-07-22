package com.jk.mr.duo.clock.viewmodels

import app.cash.turbine.test
import com.jk.mr.duo.clock.TestDispatchers
import com.jk.mr.duo.clock.data.AddressSearchResult
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.MResponse
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.repositories.CalRepository
import com.jk.mr.duo.clock.utils.PreferenceHandler
import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchAddress
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CalDataViewModelTest {

    private val calRepository = mockk<CalRepository>()

    private var preferenceHandler = mockk<PreferenceHandler>()
    private var testDispatchers = TestDispatchers()
    private var flags = mockk<FlagResponse>()
    private val lat = "0.0"
    private val long = "0.0"
    private val viewModel = CalDataViewModel(testDispatchers, calRepository, preferenceHandler, flags)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatchers.main)
    }

    @Test
    fun getData_emits_Content_with_valid_AddressSearchResult() =
        runTest {
            // Create a fake AddressSearchResult to use in the test
            val addressSearchResult = AddressSearchResult(
                name = "Fake Name",
                searchAddress = SearchAddress("Fake Address", "Fake Country"),
                coordinate = Point.fromLngLat(0.0, 0.0)
            )
            val mResponse = MResponse(
                authenticationResultCode = "101",
                brandLogoUri = "brandLogoUri",
                copyright = "copyright",
                resourceSets = mutableListOf(),
                statusCode = 500,
                statusDescription = " Desc",
                traceId = "traceId"
            )
            coEvery { calRepository.getTimeZone(any(), any()) } returns mResponse
            coEvery { flags.data } returns mutableListOf()

            assertTrue(viewModel.uiState.value == UiState.Empty)

            viewModel.uiState.test {
                viewModel.getData(addressSearchResult, lat, long)
                assertEquals(UiState.Empty, awaitItem())
                assertEquals(UiState.Loading, awaitItem())
                assertEquals(
                    UiState.Content(
                        CalData(
                            name = "Fake Name",
                            abbreviation = "",
                            address = "Unknown",
                            currentCityTimeZoneId = null,
                            flag = null
                        )
                    ),
                    awaitItem()
                )
            }
            coVerify {
                calRepository.getTimeZone(lat, long)
            }
        }
}
