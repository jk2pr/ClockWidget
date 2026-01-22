package com.hoppers.duoclock.dashboard.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.jk.mr.duo.clock.R

@Composable
fun PinnedClocksRow(
    pinnedItems: List<LocationItem>,
    onUnpinRequested: (LocationItem) -> Unit
) {

    val listState = rememberLazyListState()
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                ImageVector.vectorResource(id = R.drawable.pin_26),
                contentDescription = "",
                modifier = Modifier.size(16.dp),
            )
            Text(text = "Pinned")
        }
        LaunchedEffect(pinnedItems.size) {
            // Always keep first pinned item visible
            if (pinnedItems.isNotEmpty()) {
                listState.animateScrollToItem(0)
            }
        }
        if (pinnedItems.isEmpty()) {
            PinnedEmptyStateCard()
            return
        }
        LazyRow(
            // contentPadding = PaddingValues(end = 8.dp),
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            items(
                pinnedItems,
                key = { item -> item.id }) { item ->
                PinnedClockCard(
                    modifier = Modifier.animateItem(
                        placementSpec = spring(
                            stiffness = Spring.StiffnessMediumLow,
                            visibilityThreshold = IntOffset.VisibilityThreshold
                        )
                    ),
                    item = item,
                    onLongPress = {
                        onUnpinRequested(item)
                    })
            }
        }
    }
}

@Preview
@Composable
private fun PinnedClockRowPreview() {
    PinnedClocksRow(
        pinnedItems = listOf(
            LocationItem(
                id = "1",
                "Elise",
                country = "Address",
                remoteCityTimeZone = "Asia/Kolkata",
                isSelected = true,
                flag = "",
                displayName = "Display 1  "
            )
        )
    ) {

    }
}
