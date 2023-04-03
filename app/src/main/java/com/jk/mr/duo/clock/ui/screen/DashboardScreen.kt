package com.jk.mr.duo.clock.ui.screen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.MrDuoClockApplication
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.common.Loading
import com.jk.mr.duo.clock.common.localproviders.LocalNavController
import com.jk.mr.duo.clock.common.localproviders.LocalScaffold
import com.jk.mr.duo.clock.component.*
import com.jk.mr.duo.clock.data.AddressSearchResult
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.data.colors
import com.jk.mr.duo.clock.extenstions.toast
import com.jk.mr.duo.clock.navigation.AppScreens
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants.TAG
import com.jk.mr.duo.clock.utils.PreferenceHandler
import com.jk.mr.duo.clock.viewmodels.CalDataViewModel
import com.mapbox.search.result.SearchResult
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*

private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

@Composable
fun DashBoardScreen() {

    val context = LocalContext.current as AppWidgetConfigureActivity
    val viewModel: CalDataViewModel by context.viewModels()
    var isEditActivated: Boolean by remember { mutableStateOf(false) }
    val preferenceHandler = viewModel.preferenceHandler

    val dataList = remember { mutableStateListOf<CalData>() }
    val snackBarHostState = LocalScaffold.current.snackbarHostState

    val scope = rememberCoroutineScope()
    val lifCycleOwner = LocalLifecycleOwner.current
    val navController = LocalNavController.current
    var showThemeDialog by remember { mutableStateOf(false) }

    Page(
        menuItems = mutableListOf(
            DropdownMenuItemContent {
                IconButton(onClick = {
                    showThemeDialog = true
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_color_lens_24),
                        contentDescription = "Theme Icon",
                    )
                }
                if (dataList.size > 1)
                    IconButton(onClick = {
                        isEditActivated = !isEditActivated
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_edit_24),
                            contentDescription = "Theme Icon",
                        )
                    }
                if (!dataList.none { it.isSelected })
                    IconButton(onClick = {
                        if (dataList.size > 1 && !dataList.first().isSelected) {
                            dataList.removeAll { it.isSelected }
                            dataList.reset(context = context)
                            isEditActivated = false
                            scope.launch {
                                snackBarHostState.showSnackbar("Deleted successfully")
                            }
                        } else
                            scope.launch {
                                snackBarHostState.showSnackbar("Can't delete secondary clock")
                            }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_outline_24),
                            contentDescription = "Theme Icon",
                        )
                    }
                val filtered = dataList.filter { it.isSelected }
                if (filtered.size == 1 && !dataList.first().isSelected)
                    IconButton(onClick = {
                        Collections.swap(dataList, 0, dataList.indexOf(filtered.first()))
                        dataList.reset(context = context)
                        isEditActivated = false
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_vertical_align_top_24),
                            contentDescription = "Theme Icon",
                        )
                    }
                if (isEditActivated)
                    IconButton(onClick = {
                        isEditActivated = false
                        dataList.reset(context = context)
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.twotone_done_24),
                            contentDescription = "Theme Icon",
                        )
                    }
            }
        ),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(24.dp),
                onClick = { navController.navigate(AppScreens.SearchLocation.route) },
                content = { Icon(imageVector = Icons.Default.Add, "") }
            )
        },
        content = {
            mAppWidgetId = context.mAppWidgetId
            val app = ((context.application) as MrDuoClockApplication)
            if (showThemeDialog) ColorDialog(
                colorList = colors,
                onColorSelected = { newColor ->
                    app.changeColorScheme(newColor)
                    preferenceHandler.saveAppColorScheme(newColor.toJSON())
                    updateWidget(context = context, dataList = dataList)
                },
                currentlySelected = colors.firstOrNull { it == app.defaultScheme } ?: colors[0],
                onDismiss = {
                    showThemeDialog = false
                }
            )
            navController.currentBackStackEntry
                ?.savedStateHandle?.let {
                    it.apply {
                        LaunchedEffect(key1 = it) {
                            getLiveData<AddressSearchResult>("ADDRESS").observe(lifCycleOwner) { result ->
                                Log.d("this.toString()", "GetData called:   -- ${result.name}")
                                viewModel.getData(
                                    searchResult = result,
                                    lat = result.coordinate.latitude().toString(),
                                    long = result.coordinate.longitude().toString(),
                                )
                                remove<SearchResult>("ADDRESS")
                            }
                        }
                    }
                }
            ManageLifeCycle(
                dataList = dataList,
                lifCycleOwner = lifCycleOwner,
                preferenceHandler = preferenceHandler
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                ClockDashBoard()
                ClockList(
                    isEditActivated = isEditActivated,
                    dataList = dataList,
                    onEditActivated = {
                        isEditActivated = it
                    },
                )
            }
            when (val result = viewModel.uiState.collectAsState().value) {
                is UiState.Content ->
                    (result.data as CalData).let {
                        LaunchedEffect(key1 = it) {
                            if (dataList.contains(it)) {
                                context.toast("Already added location, Please try with different location")
                            } else {
                                addItems(dataList = dataList, newItem = it)
                                updateWidget(
                                    context = context,
                                    dataList = dataList
                                )
                            }
                        }
                    }

                is UiState.Error ->
                    LaunchedEffect(key1 = Unit) {
                        scope.launch {
                            snackBarHostState.apply {
                                showSnackbar(
                                    message = result.message,
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                is UiState.Loading -> Loading()
                is UiState.Empty -> {}
            }
        }
    )
}

fun MutableList<CalData>.reset(context: AppWidgetConfigureActivity) {
    forEach { it.isSelected = false }
    updateWidget(context = context, dataList = this)
}

@Composable
private fun ManageLifeCycle(
    dataList: MutableList<CalData>,
    lifCycleOwner: LifecycleOwner,
    preferenceHandler: PreferenceHandler,
) {
    DisposableEffect(key1 = lifCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                val jsonString = preferenceHandler.getDateData()
                jsonString?.let { it ->
                    if (it.isEmpty()) return@LifecycleEventObserver
                    val listType = object : TypeToken<List<CalData>>() {}.type
                    val storedData =
                        Gson().fromJson<List<CalData>>(it, listType)
                    if (storedData.isNotEmpty()) {
                        dataList.clear()
                        addItems(dataList = dataList, newItemAll = storedData) // get from stored
                    }
                }
            }
            if (event == Lifecycle.Event.ON_STOP)
                preferenceHandler.saveDateData(dataList)
        }
        lifCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private fun addItems(
    dataList: MutableList<CalData>,
    newItemAll: List<CalData>? = null,
    newItem: CalData? = null,
) {
    newItemAll?.let { dataList.addAll(it) }
    newItem?.let { dataList.add(0, it) }
}

private fun updateWidget(
    context: AppWidgetConfigureActivity,
    dataList: MutableList<CalData>,
) {
    try {
        MainScope().launch {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceAppWidgetManager.getGlanceIds(AppWidget::class.java)
            Log.d(TAG, "updateClock: glanceIds : $glanceIds")
            if (glanceIds.isEmpty())
                return@launch
            val glanceId = glanceIds.last()
            val application = context.applicationContext as MrDuoClockApplication
            updateAppWidgetState(context = context, glanceId = glanceId) {
                dataList.first().let { data ->
                    it[stringPreferencesKey("calData")] = data.toJSON()
                    it[stringPreferencesKey("theme")] = application.defaultScheme.toJSON()
                }
            }
            val glanceAppWidget: GlanceAppWidget = AppWidget()

            val resultValue =
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, glanceId.toString())
            context.setResult(Activity.RESULT_OK, resultValue)
            glanceAppWidget.updateAll(context)
        }
    } catch (e: IllegalArgumentException) {
        Log.d(TAG, "No GlanceId found for this appWidgetId.")
    }
}
