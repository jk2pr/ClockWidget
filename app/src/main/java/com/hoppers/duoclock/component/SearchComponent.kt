package com.hoppers.duoclock.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hoppers.duoclock.common.localproviders.LocalNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchComponent(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit,
    content: @Composable (ColumnScope.() -> Unit) = {}
) {
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var isSearchActivated by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val onActiveChanged: (Boolean) -> Unit = { isSearchActivated = it }
    val onQueryChange: (String) -> Unit = {
        searchText = it
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            delay(1000) // Debounce time in milliseconds
            onSearch(it)
        }
    }
    val localNavController = LocalNavController.current

    SearchBar(
        modifier = modifier,
        leadingIcon = {
            Icon(
                modifier = Modifier.clickable {
                    if (!isSearchActivated) {
                        localNavController.popBackStack()
                    } else {
                        isSearchActivated = false
                    }
                },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            if (isSearchActivated) {
                Icon(
                    modifier = Modifier.clickable {
                        searchText = ""
                        onQueryChange(searchText)
                        isSearchActivated = false
                    },
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        },
        query = searchText,
        onQueryChange = onQueryChange,
        placeholder = { Text(text = "Type to start search") },
        onSearch = onSearch,
        active = isSearchActivated,
        onActiveChange = onActiveChanged,
        content = content

    )
}
