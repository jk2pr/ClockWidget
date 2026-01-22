package com.hoppers.duoclock.search.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoppers.duoclock.common.component.ComposeLocalWrapper
import com.hoppers.duoclock.common.component.Loading
import com.hoppers.duoclock.common.localproviders.LocalNavController
import com.hoppers.duoclock.common.component.SearchComponent
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.search.Place

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocationScreen(
    result: UiState,
    onSearch: (String) -> Unit
) {
    val localNavController = LocalNavController.current
    Scaffold(
        topBar = {
            SearchComponent(
                modifier = Modifier
                    .fillMaxWidth(),
                onSearch = onSearch,
                content = {
                    SearchContent(
                        resp = result,
                        onItemClick = {
                            localNavController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("ADDRESS", it)
                            localNavController.popBackStack()
                        }
                    )
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
            }
        }
    )
}

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    resp: UiState,
    onItemClick: (url: Place) -> Unit
) {
    LazyColumn(modifier = modifier.padding(8.dp)) {
        when (resp) {
            is UiState.Error -> item {
                FooterItem(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    text = resp.message
                )
            }

            is UiState.Loading -> item {
                Loading()
            }

            is UiState.Content -> {
                val places = resp.data as List<*>
                if (places.isEmpty()) {
                    item {
                        FooterItem(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            text = "No Address Found"
                        )
                    }
                } else {
                    itemsIndexed(places) { index, place ->
                        if (index > 0)
                            HorizontalDivider()
                        if (place is Place)
                            UserItem(place = place, onItemClick = onItemClick)
                    }
                }
            }

            UiState.Empty -> {}
        }
    }
}

@Composable
fun FooterItem(modifier: Modifier = Modifier, text: String) {
    Text(
        textAlign = TextAlign.Center,
        modifier = modifier,
        text = text
    )
}

@Composable
private fun UserItem(place: Place, onItemClick: (url: Place) -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable {
                onItemClick(place)
            }

    ) {
        Text(text = place.name)
        Text(text = place.displayName)
    }
}

@Preview
@Composable
private fun SearchLocationScreenPreview() {
    ComposeLocalWrapper {
        SearchLocationScreen(result = UiState.Content(data = Unit)) { }
    }
}