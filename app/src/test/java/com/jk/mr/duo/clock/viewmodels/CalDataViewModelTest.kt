package com.jk.mr.duo.clock.viewmodels

import app.cash.turbine.test
import com.jk.mr.duo.clock.MainDispatcherRule
import com.jk.mr.duo.clock.TestDispatchers
import com.jk.mr.duo.clock.data.AddressSearchResult
import com.jk.mr.duo.clock.data.FlagResponse
import com.jk.mr.duo.clock.data.MResponse
import com.jk.mr.duo.clock.data.ResourceSets
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.repositories.CalRepository
import com.jk.mr.duo.clock.utils.PreferenceHandler
import com.mapbox.geojson.Point
import com.mapbox.search.result.SearchAddress
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class CalDataViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: CalDataViewModel

    private val calRepository = mockk<CalRepository>()

    private var preferenceHandler = mockk<PreferenceHandler>()
    private lateinit var testDispatchers: TestDispatchers
    var flags = mockk<FlagResponse>()

    @Before
    fun setUp() {
        testDispatchers = TestDispatchers()
        viewModel = CalDataViewModel(testDispatchers, calRepository, preferenceHandler, flags)

    }

    private val lat = "0.0"
    private val long = "0.0"

    @Test
    fun getData_emits_Content_with_valid_AddressSearchResult() {
        runBlocking {

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
                resourceSets = mutableListOf<ResourceSets>(),
                statusCode = 500,
                statusDescription = " Desc",
                traceId = "traceId"
            )
            coEvery { calRepository.getTimeZone(any(), any()) } returns mResponse
            coEvery { flags.data } returns mutableListOf()
            viewModel.getData(addressSearchResult, lat, long)
            coVerify { calRepository.getTimeZone(lat, long) }

            val totalFlow = mutableListOf<UiState>()
            viewModel.uiState.test {
                assertEquals(UiState.Loading, awaitItem())
                viewModel.uiState.emit(UiState.Content(""))
                assertTrue(awaitItem() is UiState.Content)
                //   awaitComplete()
            }

            //assertEquals(UiState.Content(addressSearchResult), flowCollector[1])
        }
        // Create a fake MResponse to be returned by the repository
    }
}
