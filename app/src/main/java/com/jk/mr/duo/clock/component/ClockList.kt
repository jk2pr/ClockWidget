package com.jk.mr.duo.clock.component

import android.widget.TextClock
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.DismissValue.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.common.ShowEmpty
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.utils.Utils
import java.util.*

@Composable
fun ClockList(
    isEditActivated: Boolean = false,
    dataList: MutableList<CalData>,
    onEditActivated: (Boolean) -> Unit
) {
    CreateAdapter(
        dataList = dataList,
        onEditActivated = onEditActivated,
        isEditActivated = isEditActivated
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CreateAdapter(
    onEditActivated: (Boolean) -> Unit,
    dataList: MutableList<CalData>,
    isEditActivated: Boolean
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (dataList.isEmpty()) {
            ShowEmpty()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy((1).dp, Alignment.Top),
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                itemsIndexed(
                    items = dataList,
                    key = { _, data -> data.toString() }
                ) { index, item ->

                    val backgroundColor =
                        if (item.isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else if (index == 0) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    val contentColor = contentColorFor(backgroundColor = backgroundColor)
                    val modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateItemPlacement()
                        .background(backgroundColor)
                        .combinedClickable(
                            onLongClick = {
                                // Activate Editable
                                onEditActivated(true)
                            }
                        ) {
                            if (isEditActivated) {
                                if (item.isSelected) {
                                    dataList[index] = item.copy(isSelected = false)
                                } else {
                                    dataList[index] = item.copy(isSelected = true)
                                }
                            }
                        }
                    ListItem(calData = item, isEditableActivated = isEditActivated,   contentColor = contentColor, modifier = modifier)
                }
                //  itemContent = { ListItem(item, isEditActivated) }
            }
        }
    }
}

@Composable
private fun ListItem(calData: CalData, contentColor : Color,isEditableActivated: Boolean, modifier: Modifier) {
    ConstraintLayout(modifier = modifier.padding(8.dp)) {
        val (svg, column, dragIcon) = createRefs()
        val painter =
            rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(calData.flag)
                    .placeholder(R.drawable.ic_image_black_24dp)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .crossfade(enable = true)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                contentScale = ContentScale.Crop
            )
        Image(
            painter = painter,
            contentDescription = "SVG Image",
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(svg) {
                    start.linkTo(parent.start, margin = 16.dp)
                    centerVerticallyTo(parent)
                },
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .constrainAs(column) {
                    start.linkTo(svg.end, margin = 16.dp)
                    centerVerticallyTo(parent)
                    if (isEditableActivated) {
                        end.linkTo(dragIcon.start)
                    } else {
                        end.linkTo(parent.end, margin = 16.dp)
                    }
                    width = Dimension.fillToConstraints
                }
        ) {
            Text(
                text = calData.name,
                style = typography.bodyMedium.merge(TextStyle(contentColor))
            )
            Text(
                text = calData.address,
                style = typography.bodyMedium.merge(TextStyle(contentColor))
            )
            AndroidView(
                factory = { context ->
                    TextClock(context).apply {
                        format12Hour = Utils.get12HoursFormat()
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
                end.linkTo(parent.end)
                centerVerticallyTo(parent)
            }
        ) {
            Image(
                painter = painterResource(
                    if (calData.isSelected) {
                        R.drawable.baseline_check_circle_24
                    } else {
                        R.drawable.twotone_radio_button_unchecked_24
                    }
                ),
                colorFilter = ColorFilter.tint(contentColor),
                contentDescription = "Drag Icon",
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

class CalDataPreviewParameterProvider : PreviewParameterProvider<CalData> {

    override val values = sequenceOf(
        CalData(
            "Elise",
            address = "Address",
            currentCityTimeZoneId = null,
            abbreviation = "Abb",
            flag = ""
        )

    )
}

@Preview(showSystemUi = false)
@Composable
fun ComposablePreview(@PreviewParameter(CalDataPreviewParameterProvider::class) calData: CalData) {
    ClockList(
        dataList = mutableListOf(),
        onEditActivated = {}
    )
}
