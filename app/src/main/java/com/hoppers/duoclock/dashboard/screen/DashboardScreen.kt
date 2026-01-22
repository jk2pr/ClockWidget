package com.hoppers.duoclock.dashboard.screen

import android.app.Activity
import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.hoppers.duoclock.AppWidgetConfigureActivity
import com.hoppers.duoclock.common.component.ComposeLocalWrapper
import com.hoppers.duoclock.common.component.Loading
import com.hoppers.duoclock.common.component.Page
import com.hoppers.duoclock.common.localproviders.LocalNavController
import com.hoppers.duoclock.common.localproviders.LocalSnackBarHostState
import com.hoppers.duoclock.dashboard.components.AlarmPermissionRequestDialog
import com.hoppers.duoclock.dashboard.components.ClockList
import com.hoppers.duoclock.dashboard.components.ConfirmDeleteDialog
import com.hoppers.duoclock.dashboard.components.LiveUpdateBanner
import com.hoppers.duoclock.dashboard.components.PinnedClocksRow
import com.hoppers.duoclock.dashboard.components.TextClock
import com.hoppers.duoclock.dashboard.components.skelton.DashBoardSkeleton
import com.hoppers.duoclock.dashboard.data.CitiesUiState
import com.hoppers.duoclock.dashboard.data.DashBoardScreenArgs
import com.hoppers.duoclock.dashboard.data.DeleteDialogState
import com.hoppers.duoclock.dashboard.data.LocationItem
import com.hoppers.duoclock.dashboard.data.UiState
import com.hoppers.duoclock.navigation.AppScreens
import com.hoppers.duoclock.search.Place
import com.hoppers.duoclock.utils.UiUtils.openExactAlarmSystemSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.TimeZone


@Composable
fun DashBoardScreen(args: DashBoardScreenArgs) {
    val uiState by args.cityUiState.collectAsStateWithLifecycle()

    val cities = when (uiState) {
        is CitiesUiState.Ready -> (uiState as CitiesUiState.Ready).cities
        CitiesUiState.Loading -> emptyList()
    }

    DashBoardContent(
        dataList = cities,
        isLoading = uiState is CitiesUiState.Loading,
        args = args
    )

}

@Composable
fun DashBoardContent(
    dataList: List<LocationItem> = emptyList(),
    isLoading: Boolean = false, args: DashBoardScreenArgs
) {
    val context: Context = LocalContext.current
    val navController: NavController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current
    val scope = rememberCoroutineScope()
    val appConfigurationActivity = LocalActivity.current

    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    var showDialog by rememberSaveable { mutableStateOf(false) }
    val shouldShowBanner = !alarmManager.canScheduleExactAlarms()

    val mAppWidgetId =
        appConfigurationActivity?.intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)

    if (isLoading) DashBoardSkeleton()
    else {
        Page(
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text("Add city") },
                    icon = { Icon(Icons.Outlined.AddLocation, null) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        navController.navigate(AppScreens.SearchLocation.route)
                    }
                )
            }
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextClock(textColor = MaterialTheme.colorScheme.primary)
                if (shouldShowBanner)
                    LiveUpdateBanner(visible = true) {
                        showDialog = true
                    }
                if (showDialog) {
                    AlarmPermissionRequestDialog(
                        onContinue = {
                            openExactAlarmSystemSettings(context)
                        },
                        onDismiss = { showDialog = false }

                    )
                }
                HorizontalDivider()
                PinnedClocksRow(
                    pinnedItems = dataList.filter { it.isPinned },
                    onUnpinRequested = { args.onToggle(it) {} })
                ClockList(
                    dataList = dataList,
                    onTogglePin = {
                        args.onToggle(it) { message ->
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    onDeleteItem = args.requestDelete,
                )
            }
            ConfirmDeleteDialog(
                dialogState = args.dialogState.collectAsState().value,
                confirm = args.confirmDelete,
                cancel = args.cancelRemove
            )
            when (val result = args.state.collectAsState().value) {
                is UiState.Content -> {}
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
            LaunchedEffect(mAppWidgetId, dataList) {
                if (mAppWidgetId == null) return@LaunchedEffect
                (context as? AppWidgetConfigureActivity)?.setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        mAppWidgetId
                    )
                )

            }

            ObservePlaceResult(navController = navController, onPlaceSelected = args.onEvent)
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


@Preview
@Composable
private fun DashBoardScreenPreview() {
    ComposeLocalWrapper {
        Page {
            DashBoardContent(
                args = DashBoardScreenArgs(
                    requestDelete = {},
                    onEvent = {},
                    onSelect = {},
                    state = MutableStateFlow(UiState.Empty),
                    cityUiState = MutableStateFlow(CitiesUiState.Loading),
                    dialogState = MutableStateFlow(DeleteDialogState.Hidden),
                    confirmDelete = {},
                    cancelRemove = {},
                    reset = { },
                    onToggle = { _, _ -> }
                ),
                dataList = mutableListOf(
                    LocationItem(
                        name = "Delhi",
                        country = "India",
                        remoteCityTimeZone = TimeZone.getDefault().id,
                        isSelected = false,
                        isPinned = false,
                        pinnedOrder = -1,
                        displayName = "Delhi",
                        flag = null
                    ),
                    LocationItem(
                        name = "Mumbai",
                        country = "India",
                        remoteCityTimeZone = TimeZone.getDefault().id,
                        isSelected = false,
                        isPinned = false,
                        pinnedOrder = -1,
                        displayName = "Delhi",
                        flag = null
                    ),

                    ),

                )
        }
    }
}