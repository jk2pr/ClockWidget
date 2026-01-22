package com.hoppers.duoclock.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoppers.duoclock.DispatcherProvider
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.search.repositories.SearchRepository
import com.hoppers.duoclock.search.repositories.toUserMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        observeSearch()
    }

    fun doSearch(query: String) {
        queryFlow.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch(dispatcherProvider.main) {
            queryFlow
                .debounce(500)                 // ⏱ wait user typing
                .filter { it.length >= 2 }     // 🔍 min chars
                .distinctUntilChanged()
                .collectLatest { query ->
                    search(query)
                }
        }
    }

    private suspend fun search(query: String) {
        _uiState.value = UiState.Loading

        try {
            val response = withContext(dispatcherProvider.io) {
                searchRepository.doSearch(query)
            }
            _uiState.value = UiState.Content(response)

        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.toUserMessage())
        }
    }
}
