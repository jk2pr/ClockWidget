package com.jk.mr.duo.clock.ui.screen

import android.widget.TextClock
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
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
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.viewmodels.CalDataViewModel
import kotlinx.coroutines.launch


@Composable
fun ClockScreen(
    calData: CalData? = null,
    dataList: MutableList<CalData>,
    updateClock: ((CalData) -> Unit)?,
) {
    calData?.let { cd ->
        dataList.add(cd).apply { updateClock?.invoke(cd) }
    }
    CreateAdapter(dataList = dataList)
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateAdapter(dataList: MutableList<CalData>) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        itemsIndexed(items = dataList) { _, item ->
            lateinit var dismissState: DismissState

            dismissState = rememberDismissState {
                if (it == DismissedToEnd || it == DismissedToStart)
                    if (dataList.indexOf(item) != 0 ) {
                        scope.launch { snackBarHostState.showSnackbar("Deleted successfully") }

                    }
                    else
                        scope.launch {
                            dismissState.reset()
                            snackBarHostState.showSnackbar("First item can not be deleted", duration = SnackbarDuration.Indefinite)
                        }
                true
            }

            SwipeToDismiss(
                state = dismissState,
                modifier = Modifier.padding(vertical = 4.dp),
                directions = setOf(StartToEnd, EndToStart),
                dismissThresholds = { direction ->
                    FractionalThreshold(if (direction == StartToEnd) 0.25f else 0.5f)
                },
                background = {
                    val direction =
                        dismissState.dismissDirection ?: return@SwipeToDismiss
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            Default -> Color.LightGray
                            DismissedToEnd -> Color.Green
                            DismissedToStart -> Color.Red
                        }
                    )
                    val alignment = when (direction) {
                        StartToEnd -> Alignment.CenterStart
                        EndToStart -> Alignment.CenterEnd
                    }
                    val icon = when (direction) {
                        StartToEnd -> Icons.Default.Done
                        EndToStart -> Icons.Default.Delete
                    }
                    val scale by animateFloatAsState(if (dismissState.targetValue == Default) 0.75f else 1f)
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color)
                            .padding(horizontal = 20.dp),
                        contentAlignment = alignment
                    ) {
                        Icon(
                            icon,
                            contentDescription = "Localized description",
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {
                    Card(elevation = animateDpAsState(if (dismissState.dismissDirection != null) 4.dp else 0.dp).value) {
                        ListItem(item)
                    }
                })

        }

    }
    SnackbarHost(
        hostState = snackBarHostState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.Bottom)
    )
}

@Composable
private fun ListItem(calData: CalData) {

    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        ConstraintLayout(modifier = Modifier.padding(8.dp)) {

            val (svg, column, dragIcon) = createRefs()
            val painter =
                rememberAsyncImagePainter(model = ImageRequest.Builder(LocalContext.current)
                    .data(calData.flag)
                    .crossfade(true)
                    .decoderFactory(SvgDecoder.Factory())
                    .build(),
                    contentScale = ContentScale.Crop
                )
            Image(
                painter = painter,
                contentDescription = "SVG Image",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .constrainAs(svg) {
                        start.linkTo(parent.start)
                    },
                contentScale = ContentScale.FillBounds
            )


            Column(modifier = Modifier
                .constrainAs(column) {
                    start.linkTo(svg.end, margin = 8.dp)
                    centerVerticallyTo(parent)
                    end.linkTo(dragIcon.start)
                    width = Dimension.fillToConstraints
                }) {

                Text(
                    text = calData.name,
                    style = typography.h6,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = true,
                    maxLines = 1,
                )
                Text(text = calData.address, style = typography.caption)
                AndroidView(
                    factory = { context ->
                        TextClock(context).apply {
                            format12Hour?.let { this.format12Hour = it }
                            format24Hour?.let { this.format24Hour = it }
                            timeZone?.let { this.timeZone = it }
                        }
                    },
                )
            }
            Image(
                painter = painterResource(R.drawable.ic_drag),
                contentDescription = "Drag Icon",
                modifier = Modifier.constrainAs(dragIcon) {
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                },
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

class CalDataPreviewParameterProvider : PreviewParameterProvider<CalData> {

    override val values = sequenceOf(
        CalData("Elise",
            address = "Address",
            currentCityTimeZoneId = null,
            abbreviation = "Abb",
            flag = null,
            ),

        )

}

@Preview(showSystemUi = true)
@Composable
fun ComposablePreview(@PreviewParameter(CalDataPreviewParameterProvider::class) calData: CalData) {
    ClockScreen(calData = calData, mutableListOf()) {

    }
}

