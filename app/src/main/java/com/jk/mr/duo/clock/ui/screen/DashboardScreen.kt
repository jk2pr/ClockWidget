package com.jk.mr.duo.clock.ui.screen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.common.Loading
import com.jk.mr.duo.clock.common.localproviders.LocalNavController
import com.jk.mr.duo.clock.common.localproviders.LocalSnackBarHostState
import com.jk.mr.duo.clock.component.ClockDashBoard
import com.jk.mr.duo.clock.component.ClockList
import com.jk.mr.duo.clock.component.DropdownMenuItemContent
import com.jk.mr.duo.clock.component.Page
import com.jk.mr.duo.clock.data.AddressSearchResult
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.data.caldata.DashBoardScreenArgs
import com.jk.mr.duo.clock.extenstions.hasSwappableItem
import com.jk.mr.duo.clock.extenstions.toast
import com.jk.mr.duo.clock.navigation.AppScreens
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants.TAG
import com.mapbox.search.result.SearchResult
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

@Composable
fun DashBoardScreen(args: DashBoardScreenArgs) {
    var isEditActivated: Boolean by rememberSaveable { mutableStateOf(false) }
    val context: Context = LocalContext.current
    val navController: NavController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current

    val scope = rememberCoroutineScope()
    val lifCycleOwner = LocalLifecycleOwner.current

    val dataList = args.dataList
    Page(
        menuItems = mutableListOf(
            createMenus(
                dataList = dataList,
                isEditActivated = isEditActivated,
                onRemove = args.onRemove,
                arrange = args.arrange,
                onEditChange = {
                    isEditActivated = it
                    // if (!isEditActivated) args.onDone
                }
            )
        ),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(24.dp),
                shape = CircleShape,
                onClick = { navController.navigate(AppScreens.SearchLocation.route) },
                content = { Icon(imageVector = Icons.Default.Add, "") }
            )
        }
    ) {
        mAppWidgetId = args.appWidgetId
        HandleOnResult(
            navController = navController,
            lifCycleOwner = lifCycleOwner,
            args = args.onEvent
        )
        ManageLifeCycle(
            lifCycleOwner = lifCycleOwner,
            onStart = args.onStart,
            onStop = args.onStop
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClockDashBoard()
            ClockList(
                isEditActivated = isEditActivated,
                dataList = dataList,
                onEditActivated = {
                    isEditActivated = it
                },
                setSelected = {
                    if (isEditActivated) {
                        args.onSelect(it)
                    }
                }
            )
        }
        when (val result = args.state.collectAsState().value) {
            is UiState.Content ->
                (result.data as String).let {
                    LaunchedEffect(key1 = result.tag) {
                        context.toast(it)
                        isEditActivated = false
                        updateWidget(context = context, calData = dataList.first())
                    }
                }

            is UiState.Error ->
                LaunchedEffect(key1 = result.tag) {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = result.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

            is UiState.Loading -> Loading()
            is UiState.Empty -> {}
        }
    }
}

@Composable
fun createMenus(
    dataList: List<CalData>,
    isEditActivated: Boolean,
    onRemove: () -> Unit,
    arrange: (CalData) -> Unit,
    onEditChange: (Boolean) -> Unit
): DropdownMenuItemContent {
    return DropdownMenuItemContent {
        val icon = if (isEditActivated) R.drawable.twotone_done_24 else R.drawable.baseline_edit_24
        IconButton(
            onClick = { onEditChange(!isEditActivated) },
            content = {
                Icon(
                    contentDescription = "Edit icon",
                    imageVector = ImageVector.vectorResource(id = icon)
                )
            }
        )

        AnimatedVisibility(visible = !dataList.none { it.isSelected } && isEditActivated) {
            IconButton(
                onClick = { onRemove() },
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_outline_24),
                        contentDescription = "Delete Icon"
                    )
                }
            )
        }
        val swappableItem = dataList.hasSwappableItem()
        AnimatedVisibility(visible = swappableItem != null && isEditActivated) {
            IconButton(
                onClick = { swappableItem?.let { arrange(it) } },
                content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_vertical_align_top_24),
                        contentDescription = "Theme Icon"
                    )
                }
            )
        }
    }
}

@Composable
private fun HandleOnResult(
    navController: NavController,
    lifCycleOwner: LifecycleOwner,
    args: (AddressSearchResult) -> Unit
) {
    navController.currentBackStackEntry?.savedStateHandle
        ?.let {
            LaunchedEffect(key1 = it) {
                it.getLiveData<AddressSearchResult>("ADDRESS")
                    .observe(lifCycleOwner) { result ->
                        args(result)
                        it.remove<SearchResult>("ADDRESS")
                    }
            }
        }
}

@Composable
private fun ManageLifeCycle(
    lifCycleOwner: LifecycleOwner,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    DisposableEffect(key1 = lifCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_STOP -> onStop()
                else -> {}
            }
        }
        lifCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun updateWidget(context: Context, calData: CalData) {
    try {
        MainScope().launch {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceAppWidgetManager.getGlanceIds(AppWidget::class.java)
            Log.d(TAG, "updateClock: glanceIds : $glanceIds")
            if (glanceIds.isNotEmpty()) {
                val glanceId = glanceIds.last()
                updateAppWidgetState(context = context, glanceId = glanceId) {
                    it[stringPreferencesKey("calData")] = calData.toJSON()
                }
                val glanceAppWidget: GlanceAppWidget = AppWidget()

                val resultValue =
                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, glanceId.toString())
                (context as? AppWidgetConfigureActivity)?.setResult(Activity.RESULT_OK, resultValue)
                glanceAppWidget.updateAll(context)
            }
        }
    } catch (e: IllegalArgumentException) {
        Log.d(TAG, "No GlanceId found for this appWidgetId.")
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun DashboardPreview() {
    DashBoardScreen(
        args = DashBoardScreenArgs(
            state = MutableStateFlow(UiState.Loading),
            dataList = listOf()
        )
    )
}
