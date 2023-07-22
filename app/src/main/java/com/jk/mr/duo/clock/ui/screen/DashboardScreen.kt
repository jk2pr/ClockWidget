package com.jk.mr.duo.clock.ui.screen

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.util.Log
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.MrDuoClockApplication
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
import com.jk.mr.duo.clock.extenstions.toast
import com.jk.mr.duo.clock.navigation.AppScreens
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants.TAG
import com.jk.mr.duo.clock.utils.PreferenceHandler
import com.mapbox.search.result.SearchResult
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Collections

private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

@Composable
fun DashBoardScreen(
    state: StateFlow<UiState>,
    preferenceHandler: PreferenceHandler?,
    onEvent: (AddressSearchResult, String, String) -> (Unit),
    context: Context = LocalContext.current,
    appWidgetId: Int
) {
    var isEditActivated: Boolean by remember { mutableStateOf(false) }

    val dataList = remember { mutableStateListOf<CalData>() }
    val navController: NavController = LocalNavController.current
    val snackBarHostState = LocalSnackBarHostState.current

    val scope = rememberCoroutineScope()
    val lifCycleOwner = LocalLifecycleOwner.current

    Page(
        menuItems = mutableListOf(
            DropdownMenuItemContent {
                if (dataList.size > 1) {
                    IconButton(onClick = {
                        isEditActivated = !isEditActivated
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_edit_24),
                            contentDescription = "Theme Icon"
                        )
                    }
                }
                if (!dataList.none { it.isSelected }) {
                    IconButton(onClick = {
                        if (dataList.size > 1 && !dataList.first().isSelected) {
                            dataList.removeAll { it.isSelected }
                            dataList.reset(context = context)
                            isEditActivated = false
                            scope.launch {
                                snackBarHostState.showSnackbar("Deleted successfully")
                            }
                        } else {
                            scope.launch {
                                snackBarHostState.showSnackbar("Can't delete secondary clock")
                            }
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_delete_outline_24),
                            contentDescription = "Theme Icon"
                        )
                    }
                }
                val filtered = dataList.filter { it.isSelected }
                if (filtered.size == 1 && !dataList.first().isSelected) {
                    IconButton(onClick = {
                        Collections.swap(dataList, 0, dataList.indexOf(filtered.first()))
                        dataList.reset(context = context)
                        isEditActivated = false
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_vertical_align_top_24),
                            contentDescription = "Theme Icon"
                        )
                    }
                }
                if (isEditActivated) {
                    IconButton(onClick = {
                        isEditActivated = false
                        dataList.reset(context = context)
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.twotone_done_24),
                            contentDescription = "Theme Icon"
                        )
                    }
                }
            }
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
        mAppWidgetId = appWidgetId
        navController.currentBackStackEntry
            ?.savedStateHandle?.let {
                it.apply {
                    LaunchedEffect(key1 = it) {
                        getLiveData<AddressSearchResult>("ADDRESS").observe(lifCycleOwner) { result ->
                            Log.d("this.toString()", "GetData called:   -- ${result.name}")
                            onEvent(
                                result,
                                result.coordinate.latitude().toString(),
                                result.coordinate.longitude().toString()
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClockDashBoard()
            ClockList(
                isEditActivated = isEditActivated,
                dataList = dataList,
                onEditActivated = {
                    isEditActivated = it
                }
            )
        }
        when (val result = state.collectAsState().value) {
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

fun MutableList<CalData>.reset(context: Context) {
    forEach { it.isSelected = false }
    updateWidget(context = context, dataList = this)
}

@Composable
private fun ManageLifeCycle(
    dataList: MutableList<CalData>,
    lifCycleOwner: LifecycleOwner,
    preferenceHandler: PreferenceHandler?
) {
    DisposableEffect(key1 = lifCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                val jsonString = preferenceHandler?.getDateData()
                jsonString?.let {
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
            if (event == Lifecycle.Event.ON_STOP) {
                preferenceHandler?.saveDateData(dataList)
            }
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
    newItem: CalData? = null
) {
    newItemAll?.let { dataList.addAll(it) }
    newItem?.let { dataList.add(0, it) }
}

private fun updateWidget(
    context: Context,
    dataList: MutableList<CalData>
) {
    try {
        MainScope().launch {
            val glanceAppWidgetManager = GlanceAppWidgetManager(context)
            val glanceIds = glanceAppWidgetManager.getGlanceIds(AppWidget::class.java)
            Log.d(TAG, "updateClock: glanceIds : $glanceIds")
            if (glanceIds.isEmpty()) {
                return@launch
            }
            val glanceId = glanceIds.last()
            val application = context.applicationContext as MrDuoClockApplication
            updateAppWidgetState(context = context, glanceId = glanceId) {
                dataList.first().let { data ->
                    it[stringPreferencesKey("calData")] = data.toJSON()
                }
            }
            val glanceAppWidget: GlanceAppWidget = AppWidget()

            val resultValue =
                Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, glanceId.toString())
            (context as? AppWidgetConfigureActivity)?.setResult(Activity.RESULT_OK, resultValue)
            glanceAppWidget.updateAll(context)
        }
    } catch (e: IllegalArgumentException) {
        Log.d(TAG, "No GlanceId found for this appWidgetId.")
    }
}

@Preview
@Composable
fun DashboardPreview() {
    DashBoardScreen(
        context = LocalContext.current,
        state = MutableStateFlow(UiState.Empty),
        preferenceHandler = null,
        appWidgetId = 0,
        onEvent = { _, _, _ -> }
    )
}
