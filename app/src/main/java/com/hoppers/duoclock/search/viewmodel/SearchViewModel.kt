package com.hoppers.duoclock.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoppers.duoclock.DispatcherProvider
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.search.repositories.SearchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val dispatcherProvider: DispatcherProvider,
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun doSearch(query: String) {
        viewModelScope.launch(dispatcherProvider.io) {
            flow {
                emit(UiState.Loading)
                val response = searchRepository.doSearch(query)
                emit(UiState.Content(response))
            }.flowOn(dispatcherProvider.main).collect {
                _uiState.value = it
            }
        }
    }
}
