package com.hoppers.duoclock.dashboard.screen

import android.app.Activity
import android.app.AlarmManager
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
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
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.hoppers.duoclock.AppWidgetConfigureActivity
import com.hoppers.duoclock.appwidget.AppWidget
import com.hoppers.duoclock.appwidget.receivers.ClockAlarmReceiver
import com.hoppers.duoclock.common.Loading
import com.hoppers.duoclock.common.localproviders.LocalNavController
import com.hoppers.duoclock.common.localproviders.LocalSnackBarHostState
import com.hoppers.duoclock.component.ClockDashBoard
import com.hoppers.duoclock.component.ClockList
import com.hoppers.duoclock.component.DropdownMenuItemContent
import com.hoppers.duoclock.component.Page
import com.hoppers.duoclock.dashboard.data.DashBoardScreenArgs
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.extenstions.hasSwappableItem
import com.hoppers.duoclock.navigation.AppScreens
import com.hoppers.duoclock.search.Place
import com.hoppers.duoclock.utils.Constants.TAG
import com.jk.mr.duo.clock.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


@Composable
fun DashBoardScreen(args: DashBoardScreenArgs) {
    var isEditActivated: Boolean by rememberSaveable { mutableStateOf(false) }
    val context: Context = LocalContext.current
    val navController: NavController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current
    var showLiveUpdateDialog by rememberSaveable { mutableStateOf(false) }

    val mAppWidgetId = (context as? AppWidgetConfigureActivity)?.intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

    LaunchedEffect(Unit) {
        // Only when opened via widget-add flow
        // Alarm manager is off
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && !alarmManager.canScheduleExactAlarms()) {
            showLiveUpdateDialog = true
        }
    }

    val scope = rememberCoroutineScope()
    val lifCycleOwner = LocalLifecycleOwner.current
    val dataList = args.dataList

    ObservePlaceResult(navController = navController, onPlaceSelected = args.onEvent)
    ObserveStartStopLifecycle(
        onStart = args.onStart,
        lifecycleOwner = lifCycleOwner,
        onStop = args.onStop
    )
    Page(
        menuItems = mutableListOf(
            createMenus(
                dataList = dataList,
                isEditActivated = isEditActivated,
                onRemove = args.onRemove,
                arrange = args.arrange,
                onEditChange = {
                    isEditActivated = it
                    if (!it) args.onDone()
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
                LaunchedEffect(key1 = result.tag) {
                    //  context.toast(it)
                    isEditActivated = false
                    updateWidget(
                        context = context,
                        calDataList = dataList,
                        mAppWidgetId = mAppWidgetId
                    )
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
        if (showLiveUpdateDialog) {
            LiveUpdateInfoDialog(
                onDismiss = {
                    showLiveUpdateDialog = false
                },
                onOpenAppSettings = {
                    navController.navigate(AppScreens.Setting.route)
                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createMenus(
    dataList: List<LocationItem>,
    isEditActivated: Boolean,
    onRemove: () -> Unit,
    arrange: (LocationItem) -> Unit,
    onEditChange: (Boolean) -> Unit
): DropdownMenuItemContent {
    return DropdownMenuItemContent {
        val navController = LocalNavController.current
        val icon = if (isEditActivated) Icons.Default.Done else Icons.Default.Edit
        TooltipBox(
            state = rememberTooltipState(),
            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
            tooltip = { Text("Edit clock") }
        ) {
            IconButton(
                onClick = { onEditChange(!isEditActivated) },
                content = {
                    Icon(
                        contentDescription = "Edit icon",
                        imageVector = icon
                    )
                }
            )
        }
        AnimatedVisibility(visible = !dataList.none { it.isSelected } && isEditActivated) {
            IconButton(
                onClick = { onRemove() },
                content = {
                    Icon(
                        imageVector = Icons.Default.Delete,
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
        IconButton(onClick = {
            navController.navigate(AppScreens.Setting.route)
        }) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Theme Icon"
            )
        }
    }
}

@Composable
private fun ObservePlaceResult(
    navController: NavController,
    onPlaceSelected: (Place) -> Unit
) {
    val savedStateHandle =
        navController.currentBackStackEntry?.savedStateHandle ?: return

    LaunchedEffect(savedStateHandle) {
        savedStateHandle
            .getStateFlow<Place?>("ADDRESS", null)
            .collect { place ->
                place?.let {
                    onPlaceSelected(it)
                    savedStateHandle["ADDRESS"] = null
                }
            }
    }
}

@Composable
private fun ObserveStartStopLifecycle(
    lifecycleOwner: LifecycleOwner,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> onStart()
                Lifecycle.Event.ON_STOP -> onStop()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun updateWidget(context: Context, calDataList: List<LocationItem>, mAppWidgetId: Int) {
    MainScope().launch {

        ClockAlarmReceiver.updateNow(context, calDataList)
        val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(AppWidget::class.java)
        Log.d(TAG, "updateClock: glanceIds : $glanceIds")
        if (glanceIds.isNotEmpty()) {
            val glanceId = glanceIds.last()
            if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) { // opens from widget only
                val resultValue =
                    Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, glanceId.toString())
                (context as? AppWidgetConfigureActivity)?.setResult(Activity.RESULT_OK, resultValue)
            }
        }
    }
}

@Composable
fun LiveUpdateInfoDialog(
    onDismiss: () -> Unit,
    onOpenAppSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Live clock updates")
        },
        text = {
            Text(
                "DuoClock can update the widget every minute for precise time.\n\n" +
                        "You can enable this later from:\n" +
                        "App → Settings → Live updates\n\n" +
                        "(This requires a system permission.)"
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        },
        dismissButton = {
            TextButton(onClick = onOpenAppSettings) {
                Text("Open app settings")
            }
        }
    )
}
