package com.hoppers.duoclock.component

import android.widget.TextClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.hoppers.duoclock.common.ShowEmpty
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.utils.Utils
import com.jk.mr.duo.clock.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClockList(
    isEditActivated: Boolean = false,
    dataList: List<LocationItem>,
    setSelected: (LocationItem) -> Unit,
    onEditActivated: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (dataList.isEmpty()) {
            ShowEmpty()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
            ) {
                itemsIndexed(
                    items = dataList,
                    key = { _, data -> data.toString() }
                ) { index, item ->
                    val modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement()
                        .combinedClickable(
                            onLongClick = {
                                // Activate Editable
                                onEditActivated(true)
                            }
                        ) {
                            setSelected(item)
                        }
                    if (index == 0) HorizontalDivider()
                    ListItem(
                        modifier = modifier,
                        calData = item,
                        isEditableActivated = isEditActivated
                    )

                    HorizontalDivider()
                }
                //  itemContent = { ListItem(item, isEditActivated) }
            }
        }
    }
}

@Composable
private fun ListItem(
    modifier: Modifier,
    calData: LocationItem,
    isEditableActivated: Boolean
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            // .height(102.dp)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        val (svg, column, dragIcon) = createRefs()
        val contentColor = LocalContentColor.current
        val painter =
            rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(calData.flag)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .crossfade(enable = true)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentScale = ContentScale.Inside
            )
        Image(
            painter = painter,
            contentDescription = "SVG Image",
            modifier = Modifier
                .size(40.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .constrainAs(svg) {
                    start.linkTo(parent.start)
                    centerVerticallyTo(parent)
                },
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .constrainAs(column) {
                    start.linkTo(svg.end, margin = 16.dp)
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                maxLines = 1,
                text = calData.name,
                overflow = TextOverflow.Ellipsis,
                style = typography.bodyLarge
            )
            Text(
                maxLines = 1,
                text = calData.address,
                style = typography.bodyMedium
            )
            AndroidView(
                modifier = Modifier.align(Alignment.End),
                factory = { context ->
                    TextClock(context).apply {
                        format12Hour = Utils.getItem12HoursFormat()
                        format24Hour = Utils.get24HoursFormat()
                        timeZone = calData.currentCityTimeZoneId
                        setTextColor(contentColor.toArgb())
                    }
                }
            )
        }
        AnimatedVisibility(
            visible = isEditableActivated,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.constrainAs(dragIcon) {
                start.linkTo(column.end, margin = 8.dp)
                // start.linkTo(column.end)
                centerVerticallyTo(parent)
            }
        ) {
            Image(
                painter = painterResource(
                    if (calData.isSelected) {
                        R.drawable.check_box
                    } else {
                        R.drawable.check_double_solid
                    }

                ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "Drag Icon",
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

class CalDataPreviewParameterProvider : PreviewParameterProvider<List<LocationItem>> {

    override val values: Sequence<List<LocationItem>>
        get() = sequenceOf(
            listOf(
                LocationItem(
                    "Elise",
                    address = "Address",
                    currentCityTimeZoneId = null,
                    abbreviation = "Abb",
                    flag = "",
                    isSelected = true
                ),
                LocationItem(
                    "Elise 2",
                    address = "Address 2",
                    currentCityTimeZoneId = null,
                    abbreviation = "Abbrebation",
                    flag = "",
                    isSelected = false
                )
            )
        )
}

@Preview(showSystemUi = false)
@Composable
fun ClockListPreview(@PreviewParameter(CalDataPreviewParameterProvider::class) calData: List<LocationItem>) {
    ClockList(
        dataList = calData,
        onEditActivated = {},
        setSelected = {}
    )
}
