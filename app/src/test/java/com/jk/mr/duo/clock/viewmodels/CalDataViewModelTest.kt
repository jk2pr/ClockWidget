package com.jk.mr.duo.clock.viewmodels

import app.cash.turbine.test
import com.google.gson.JsonSyntaxException
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
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertThrows
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
    private val viewModel =
        CalDataViewModel(testDispatchers, calRepository, preferenceHandler, flags)

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
                viewModel.getData(addressSearchResult)
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

    @Test
    fun `When given string should return a object`() {
        coEvery { preferenceHandler.getDateData() } returns "[{\"abbreviation\":\"\",\"address\":\"Argentina\",\"currentCityTimeZoneId\":\"America/Argentina/Buenos_Aires\",\"flag\":\"https://upload.wikimedia.org/wikipedia/commons/1/1a/Flag_of_Argentina.svg\",\"isSelected\":false,\"name\":\"Goa / La France\"}]"
        val calData = viewModel.doOnStart()
        coVerify { preferenceHandler.getDateData() }
        assertTrue(calData.isNotEmpty())
        val b = calData.first {
            it.address == "Argentina"
        }
        assertNotNull(b)
    }

    @Test
    fun `When given empty string should return a null`() {
        coEvery { preferenceHandler.getDateData() } returns ""
        val calData = viewModel.doOnStart()
        coVerify { preferenceHandler.getDateData() }
        assertTrue(calData.isEmpty())
    }

    @Test
    fun `When given wrong string should throw Exception`() {
        coEvery { preferenceHandler.getDateData() } returns "Wrong string"
        assertThrows(JsonSyntaxException::class.java) {
            viewModel.doOnStart()
        }
        coVerify { preferenceHandler.getDateData() }
    }

    @Test
    fun `doOnStop should call saveDateData with correct parameter`() {
        // Arrange: Prepare the test data
        val dataList = listOf(
            CalData(abbreviation = "", currentCityTimeZoneId = "", flag = "", name = "abc", address = ""),
            CalData(abbreviation = "", currentCityTimeZoneId = "", flag = "", name = "xyz", address = "")
            // Add more CalData items as needed
        )
        coEvery { preferenceHandler.saveDateData(dataList) } returns Unit
        // Act: Call the function being tested
        viewModel.doOnStop(dataList)

        // Assert: Verify that saveDateData was called with the correct parameter
        coVerify { preferenceHandler.saveDateData(dataList) }
    }
}
