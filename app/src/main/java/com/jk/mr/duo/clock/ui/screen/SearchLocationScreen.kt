package com.jk.mr.duo.clock.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.navigation.NavController
import com.jk.mr.duo.clock.BuildConfig
import com.jk.mr.duo.clock.common.localproviders.LocalNavController
import com.jk.mr.duo.clock.component.DropdownMenuItemContent
import com.jk.mr.duo.clock.component.Page
import com.jk.mr.duo.clock.component.SearchView
import com.jk.mr.duo.clock.data.AddressSearchResult
import com.mapbox.search.ApiType
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.offline.OfflineResponseInfo
import com.mapbox.search.offline.OfflineSearchEngine
import com.mapbox.search.offline.OfflineSearchEngineSettings
import com.mapbox.search.offline.OfflineSearchResult
import com.mapbox.search.record.HistoryRecord
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import com.mapbox.search.ui.adapter.engines.SearchEngineUiAdapter
import com.mapbox.search.ui.view.CommonSearchViewConfiguration
import com.mapbox.search.ui.view.DistanceUnitType
import com.mapbox.search.ui.view.SearchMode
import com.mapbox.search.ui.view.SearchResultsView

@Composable
fun SearchLocationScreen() {
    var searchEngineUiAdapter: SearchEngineUiAdapter? = null
    val localNavController = LocalNavController.current
    val searchResultsView = remember { mutableStateOf<SearchResultsView?>(null) }

    fun setUpViews() {
        val accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
        searchResultsView.value?.initialize(
            SearchResultsView.Configuration(CommonSearchViewConfiguration(DistanceUnitType.IMPERIAL))
        )
        searchResultsView.value?.setPadding(10)
        val searchEngine = SearchEngine.createSearchEngineWithBuiltInDataProviders(
            apiType = ApiType.GEOCODING,
            settings = SearchEngineSettings(accessToken)
        )

        val offlineSearchEngine =
            OfflineSearchEngine.create(OfflineSearchEngineSettings(accessToken))
        searchEngineUiAdapter = SearchEngineUiAdapter(
            view = searchResultsView.value!!,
            searchEngine = searchEngine,
            offlineSearchEngine = offlineSearchEngine
        )
        searchEngineUiAdapter?.let {
            it.searchMode = SearchMode.AUTO
            it.addSearchListener(
                listener = object : SearchEngineUiAdapter.SearchListener {
                    override fun onSuggestionsShown(
                        suggestions: List<SearchSuggestion>,
                        responseInfo: ResponseInfo
                    ) = print(suggestions)

                    override fun onCategoryResultsShown(
                        suggestion: SearchSuggestion,
                        results: List<SearchResult>,
                        responseInfo: ResponseInfo
                    ) = Unit

                    override fun onError(e: Exception) = Unit

                    override fun onOfflineSearchResultsShown(
                        results: List<OfflineSearchResult>,
                        responseInfo: OfflineResponseInfo
                    ) = Unit

                    override fun onSuggestionSelected(searchSuggestion: SearchSuggestion) = false

                    override fun onSearchResultSelected(
                        searchResult: SearchResult,
                        responseInfo: ResponseInfo
                    ) {
                        sendBack(
                            localNavController = localNavController,
                            addressSearchResult = AddressSearchResult(
                                searchAddress = searchResult.address,
                                name = searchResult.name,
                                coordinate = searchResult.coordinate
                            )
                        )
                    }

                    override fun onOfflineSearchResultSelected(
                        searchResult: OfflineSearchResult,
                        responseInfo: OfflineResponseInfo
                    ) = Unit

                    override fun onHistoryItemClick(historyRecord: HistoryRecord) {
                        sendBack(
                            localNavController = localNavController,
                            addressSearchResult = AddressSearchResult(
                                name = historyRecord.name,
                                searchAddress = historyRecord.address,
                                coordinate = historyRecord.coordinate
                            )
                        )
                    }

                    override fun onPopulateQueryClick(
                        suggestion: SearchSuggestion,
                        responseInfo: ResponseInfo
                    ) {
                    }

                    override fun onFeedbackItemClick(responseInfo: ResponseInfo) = Unit
                }
            )
        }
    }
    Page(
        menuItems = mutableListOf(
            DropdownMenuItemContent(menu = {
                SearchView {
                    searchEngineUiAdapter?.search(it)
                    searchResultsView.value?.isVisible = true
                }
            })
        )
    ) {
        AndroidView(
            factory = { context ->
                searchResultsView.value =
                    SearchResultsView(context)
                setUpViews()
                searchResultsView.value!!
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

private fun sendBack(localNavController: NavController, addressSearchResult: AddressSearchResult) {
    localNavController.previousBackStackEntry
        ?.savedStateHandle
        ?.set(
            "ADDRESS",
            addressSearchResult
        )
    localNavController.popBackStack()
}
