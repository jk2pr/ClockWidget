package com.hoppers.duoclock.common.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import com.hoppers.duoclock.common.localproviders.LocalNavController
import kotlinx.coroutines.Job
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
    var isSearchActivated by remember { mutableStateOf(true) }
    var searchText by remember { mutableStateOf("") }
    val onActiveChanged: (Boolean) -> Unit = { isSearchActivated = it }
    val onQueryChange: (String) -> Unit = {
        searchText = it
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            onSearch(it)
        }
    }
    val localNavController = LocalNavController.current

    val inputColor = SearchBarDefaults.colors()
    val focusRequester = remember { FocusRequester() }
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.focusRequester(focusRequester),
                query = searchText,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = isSearchActivated,
                onExpandedChange = onActiveChanged,
                placeholder = { Text(text = "Type to start search") },
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
                colors = inputColor.inputFieldColors,
                interactionSource = null,
            )
        },
        expanded = isSearchActivated,
        onExpandedChange = onActiveChanged,
        modifier = modifier,
        shape = SearchBarDefaults.inputFieldShape,
        colors = inputColor,
        windowInsets = SearchBarDefaults.windowInsets,
        content = content,
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun SearchComponentPreview() {

    ComposeLocalWrapper {
        SearchComponent(
            modifier = Modifier,
            onSearch = { }
        ) { }
    }
}