package com.hoppers.duoclock.dashboard.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.utils.Utils.getFormattedTime
import java.util.TimeZone

@Composable
fun PinnedClockCard(
    modifier: Modifier,
    item: LocationItem,
    onLongPress: () -> Unit
) {

    ElevatedCard(
            modifier = modifier
                .width(100.dp)
                .height(60.dp)
               // .clip(MaterialTheme.shapes.medium)
                .combinedClickable(
                    onClick = {},
                    onLongClick = onLongPress
                ),
           /* colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            elevation = CardDefaults.elevatedCardElevation(2.dp)
       */ ) {
            Column(
                modifier = modifier.padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = getFormattedTime(
                        item.remoteCityTimeZone ?: TimeZone.getDefault().id,
                        context = LocalContext.current
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )

                Text(
                    text = "${item.flag.orEmpty()} ${item.name.ifBlank { item.country }}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
}

@Preview
@Composable
fun PinnedClockPreview() {
    PinnedClockCard(
        modifier = Modifier,
        item = LocationItem(
            name = "Delhi",
            country = "India",
            remoteCityTimeZone = TimeZone.getDefault().id,
            isSelected = false,
            isPinned = true,
            pinnedOrder = -1,
            displayName = "Delhi",
            flag = null,
            id = "1",
        ),
        {},
    )
}