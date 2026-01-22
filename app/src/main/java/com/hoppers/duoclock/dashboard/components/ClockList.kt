package com.hoppers.duoclock.dashboard.components

import android.widget.TextClock
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.hoppers.duoclock.common.component.ShowEmpty
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.utils.Utils
import java.util.TimeZone

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClockList(
    modifier: Modifier = Modifier,
    dataList: List<LocationItem>,
    onTogglePin: (LocationItem) -> Unit = {},
    onDeleteItem: (LocationItem) -> Unit = {},
) {
    val listState = rememberLazyListState()

    LaunchedEffect(dataList.size) {
        if (dataList.isNotEmpty()) listState.animateScrollToItem(0)
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (dataList.isEmpty())
            ShowEmpty()
        else
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                state = listState,
            ) {
                items(
                    items = dataList,
                    key = { i -> i.id }
                ) { item ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(
                                placementSpec = spring(
                                    stiffness = Spring.StiffnessMediumLow,
                                    visibilityThreshold = IntOffset.VisibilityThreshold
                                )
                            ),
                        item = item,
                        onTogglePin = onTogglePin,
                        onDelete = { onDeleteItem(item) }
                    )
                }
            }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier,
    item: LocationItem,
    onTogglePin: (LocationItem) -> Unit = {},
    onDelete: (LocationItem) -> Unit,
) {

    val contentColor = LocalContentColor.current
    Column(
        modifier = modifier,
        //    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)

                ) {
                    Text(
                        text = item.flag.orEmpty(),
                        fontSize = 16.sp
                    )
                }
                Text(
                    maxLines = 1,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    text = item.country,
                    overflow = TextOverflow.Ellipsis,
                    style = typography.titleMedium.merge(MaterialTheme.colorScheme.primary)
                )
            }
            Row(
                //  horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    modifier = Modifier.size(16.dp),
                    onClick = { onTogglePin(item) }
                ) {
                    Icon(
                        imageVector = if (item.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin clock",
                        tint = if (item.isPinned)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OverFlowIcon {
                    onDelete(item)
                }
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {

                Text(
                    text = item.displayName,
                    overflow = TextOverflow.Ellipsis,
                    style = typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.weight(0.1f))

            Column(horizontalAlignment = Alignment.End) {
                AndroidView(
                    factory = { context ->
                        TextClock(context).apply {
                            format12Hour =
                                Utils.getItem12HoursFormat(item.remoteCityTimeZone.orEmpty())
                            format24Hour = Utils.getItem24HoursFormat()
                            timeZone = item.remoteCityTimeZone
                            setTextColor(contentColor.toArgb())
                        }
                    }
                )
                Text(
                    text = Utils.getTimeDifferenceString(item.remoteCityTimeZone),
                    style = typography.labelSmall.copy(color = MaterialTheme.colorScheme.tertiary)
                )
            }
        }
    }
}

@Composable
fun OverFlowIcon(onDelete: () -> Unit = {}) {
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More"
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("Delete") },
            onClick = {
                expanded = false
                onDelete()
            }
        )

        // Future
        // DropdownMenuItem(
        //     text = { Text("View on map") },
        //     onClick = { ... }
        // )
    }
}

class CalDataPreviewParameterProvider : PreviewParameterProvider<List<LocationItem>> {

    override val values: Sequence<List<LocationItem>>
        get() = sequenceOf(
            listOf(
                LocationItem(
                    id = "1",
                    "Elise",
                    country = "Address",
                    remoteCityTimeZone = TimeZone.getDefault().id,
                    isSelected = true,
                    flag = "",
                    displayName = "Display 1  "
                ),
                LocationItem(
                    id = "2",
                    "Elise 2",
                    country = "Address 2",
                    remoteCityTimeZone = TimeZone.getDefault().id,
                    flag = "",
                    displayName = "DDDDD 234"
                )
            )
        )
}

@Preview(showSystemUi = true)
@Composable
fun ClockListPreview(@PreviewParameter(CalDataPreviewParameterProvider::class) calData: List<LocationItem>) {
    ClockList(
        dataList = calData
    )
}
