package com.jk.mr.duo.clock.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.TextClock
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.size.Dimension
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.component.Page
import com.jk.mr.duo.clock.data.UiState
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.ui.screen.ClockScreen
import com.jk.mr.duo.clock.ui.theme.ClockTheme
import com.jk.mr.duo.clock.ui.theme.DarkColorPalette
import com.jk.mr.duo.clock.ui.theme.LightColorPalette
import com.jk.mr.duo.clock.utils.*
import com.jk.mr.duo.clock.utils.Constants.themeArray
import com.jk.mr.duo.clock.viewmodels.CalDataViewModel
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
@AndroidEntryPoint
class AppWidgetConfigureActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceHandler: PreferenceHandler

    @Inject
    lateinit var appWidgetHelper: AppWidgetHelper
    private var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private val viewModel by viewModels<CalDataViewModel>()

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        intent.extras?.let {
            mAppWidgetId = it.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)
        }
        setContent {
            ClockTheme(
                customColor = (if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette)
            ) {
                Page(floatingActionButton = {
                    FloatingActionButton(modifier = Modifier.padding(24.dp),
                        onClick = { openSearchDialog() },
                        content = { Icon(imageVector = Icons.Default.Add, "") }
                    )
                }) {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val dataList = remember { mutableStateListOf<CalData>() }
                        val snackBarHostState = remember { SnackbarHostState() }
                        val context = LocalContext.current
                        val scope = rememberCoroutineScope()
                        val lifCycleOwner = LocalLifecycleOwner.current
                        ManageState(dataList = dataList, context = context)
                        ManageLifeCycle(dataList = dataList, lifCycleOwner = lifCycleOwner)
                        Column(modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            ClockDashBoard()

                            ClockScreen(
                                dataList = dataList,
                                updateClock = { updateClock(it, context) },
                            )
                        }
                        SnackbarHost(
                            hostState = snackBarHostState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(Alignment.Bottom)
                        )

                    }
                }
            }
        }

        // setTheme(getThemePref())
        //setContentView(R.layout.app_widget_configure)


        /* viewModel.mutableState.observe(this) { calendarData ->
             when (calendarData) {
                 is Resource.Success -> updateAdapter(calendarData.calData)
                 is Resource.Error -> showError()
                 else -> Unit
             }
             showLoader(calendarData is Resource.Loading)
         }*/

        // setSupportActionBar(toolbar)
        /* title = null
         recycler_clock.apply {
             adapter = dataAdapter
             layoutManager = LinearLayoutManager(this@AppWidgetConfigureActivity)
             swipeListener = onItemSwipeListener
             addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.dp8)))
         }
         handleDashBoardClock()
         fab.setOnClickListener { openSearchDialog() }
         Looper.myLooper()?.let {
             if (intent.action == ACTION_ADD_CLOCK) Handler(it).postDelayed(
                 { fab.performClick() },
                 100
             )
         }*/
    }

    @Composable
    private fun ManageLifeCycle(dataList: MutableList<CalData>, lifCycleOwner: LifecycleOwner) {
        DisposableEffect(key1 = lifCycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    val jsonString = preferenceHandler.getDateData()
                    jsonString?.let { it ->
                        if (it.isEmpty()) return@LifecycleEventObserver
                        val listType = object : TypeToken<List<CalData>>() {}.type
                        val storedData =
                            Gson().fromJson<List<CalData>>(it, listType)
                        if (storedData.isNotEmpty()) {
                            dataList.clear()
                            dataList.addAll(storedData)// get from stored
                        }
                    }
                }
                if (event == Lifecycle.Event.ON_PAUSE)
                    preferenceHandler.saveDateData(dataList)
            }
            lifCycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifCycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    private fun updateClock(selectedDate: CalData, context: Context) {
        preferenceHandler.saveTimeZonePref(selectedDate.toJSON())
        // Make sure we pass back the original appWidgetId
        val resultValue =
            Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        val manager = AppWidgetManager.getInstance(context)
        val name = ComponentName(context, AppWidget::class.java)
        val appIds = manager.getAppWidgetIds(name)
        for (appWidgetId in appIds) appWidgetHelper.updateAppWidget(
            context,
            manager,
            appWidgetId)
    }

    @Composable
    private fun ManageState(context: Context, dataList: MutableList<CalData>) {
        when (val result = viewModel.uiState.collectAsState().value) {
            is UiState.Content ->
                if (result.data is CalData)
                    dataList.add(result.data).apply { updateClock(result.data, context) }
            is UiState.Error -> {}
            is UiState.Loading ->
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            is UiState.Empty ->
                ShowEmpty(dataList)
        }
    }

    @Composable
    private fun ShowEmpty(dataList: MutableList<CalData>) {
        if (dataList.isEmpty()) Box(contentAlignment = Alignment.Center) { Text(text = "Your additional clock will appear here") }
    }

    @Composable
    private fun ClockDashBoard() {
        val circleShape = CircleShape
        val modifier = Modifier
            .size(200.dp)
            .clip(circleShape)
            .border(
                border = BorderStroke(
                    width = 8.dp,
                    color = colorResource(id = R.color.colorAccent)
                ), shape = circleShape
            )
        Box(modifier = modifier, contentAlignment = Alignment.Center) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally) {
                TextClock()
                TimeZone.getDefault().id.let { currentTimeZone ->
                    var tz = currentTimeZone
                    if (tz.contains("/")) tz = currentTimeZone.split("/")[1].replace("_", " ")
                    Text(text = tz, style = MaterialTheme.typography.body1)
                }
            }
        }
    }

    @Composable
    private fun TextClock(
        modifier: Modifier = Modifier,
        timeZone: String? = null,
    ) {
        AndroidView(
            factory = { context ->
                TextClock(context).apply {
                    setFormat12Hour(Utils.getDashBoard12HoursFormat())
                    setFormat24Hour(Utils.get24HoursFormat())
                    timeZone?.let { this.timeZone = TimeZone.getDefault().id }
                    typeface = UiUtils.getBebasneueRegularTypeFace(context)
                    textSize = 40.0f
                    gravity = Gravity.CENTER
                }
            },
            modifier = modifier
        )
    }
/*private fun showError() {
    val snackBar = Snackbar.make(
        root_coordinate,
        getString(R.string.lostInternet),
        Snackbar.LENGTH_INDEFINITE
    ).apply {
        setAction("Ok") {
            if (isShown) {
                dismiss()
            }
        }
    }
    snackBar.view.apply {
        run {
            findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                .apply {
                    setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_baseline_error_24,
                        0,
                        0,
                        0
                    )
                    compoundDrawablePadding =
                        context.resources.getDimensionPixelOffset(R.dimen.dp8)
                }
        }
        snackBar.show()
    }
}*/

    private val onItemSwipeListener = object : OnItemSwipeListener<CalData> {
        override fun onItemSwiped(
            position: Int,
            direction: OnItemSwipeListener.SwipeDirection,
            item: CalData,
        ): Boolean {
            if (direction == OnItemSwipeListener.SwipeDirection.RIGHT_TO_LEFT || direction == OnItemSwipeListener.SwipeDirection.LEFT_TO_RIGHT) {
                onItemDelete(item, position)
                return true
            }
            return false
        }
    }

    private fun onItemDelete(item: CalData, position: Int) = removeItemFromList(item, position)
    private fun removeItemFromList(item: CalData, position: Int) {
        /* dataAdapter.removeItem(position)
         val itemSwipedSnackBar = Snackbar.make(
             root_coordinate,
             getString(R.string.itemRemovedMessage, item.name),
             Snackbar.LENGTH_SHORT
         )
         itemSwipedSnackBar.setAction(getString(R.string.undoCaps)) {
             dataAdapter.insertItem(position, item)
         }
         itemSwipedSnackBar.show()*/
    }

    private fun handleDashBoardClock() {
        val font = UiUtils.getBebasneueRegularTypeFace(this@AppWidgetConfigureActivity)
        //  dashboard_clock.apply {
        //      format12Hour = Utils.getDashBoard12HoursFormat()
        //        format24Hour = Utils.getDashBoard24HoursFormat()
        //    typeface = font }
        TimeZone.getDefault().id.let { currentTimeZone ->
            var tz = currentTimeZone
            if (tz.contains("/")) tz = currentTimeZone.split("/")[1].replace("_", " ")
            //    dashboard_timezone.apply { text = tz typeface = font }
        }
    }

    fun onRestore() {
        val jsonString = preferenceHandler.getDateData()
        jsonString?.let {
            if (it.isEmpty()) return
            val listType = object : TypeToken<List<CalData>>() {}.type
            val storedData = Gson().fromJson<List<CalData>>(jsonString, listType)
            //  if (storedData.isNotEmpty()) dataList.addAll(storedData)// get from stored
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        //   if (dataAdapter.itemCount > 0) dataAdapter.updateClock(dataAdapter.dataSet[0])
    }

    fun onSave() {
        //    preferenceHandler.saveDateData(dataList)
        //   dashboard_timezone.inputType =
        //        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    }

    fun getResultFromDialog(carmenFeature: CarmenFeature) {
        val address = carmenFeature.placeName()!!
        val country = carmenFeature.placeName()!!.split(",").last()
        val place = carmenFeature.geometry() as Point
        viewModel.getData(
            address,
            country,
            place.latitude().toString().plus(",").plus(place.longitude().toString())
        )
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun updateAdapter(calData: CalData) {
        preferenceHandler.saveTimeZonePref(calData.toJSON())
        //  recycler_clock.layoutManager?.scrollToPosition(0)
        //  dataAdapter.addCal(calData)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                openSearchDialog()
                true
            }
            R.id.action_setting -> {
                showThemePickerDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openSearchDialog() {

        supportFragmentManager.let {
            val searchFragmentDialog = SearchFragmentDialog()
            searchFragmentDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog)
            searchFragmentDialog.apply {
                show(it, tag)
            }
        }
    }

    private fun showThemePickerDialog() {
        val arrayAdapter = MyArrayAdapter(this, themeArray)
        AlertDialog.Builder(this).apply {
            setCancelable(true)
            setNegativeButton("cancel") { d, _ -> d.dismiss() }
            setAdapter(arrayAdapter) { _, which ->
                val strName = arrayAdapter.getItem(which)!!
                preferenceHandler.saveThemePref(strName)
                recreate()
                val manager = AppWidgetManager.getInstance(this@AppWidgetConfigureActivity)
                val name = ComponentName(this@AppWidgetConfigureActivity, AppWidget::class.java)
                val appIds = manager.getAppWidgetIds(name)
                for (appWidgetId in appIds)
                    appWidgetHelper.updateAppWidget(
                        this@AppWidgetConfigureActivity,
                        manager,
                        appWidgetId
                    )
            }
        }.show()
    }

    private inner class MyArrayAdapter(
        act: AppWidgetConfigureActivity,
        val objects: Array<String>,
    ) : ArrayAdapter<String>(act, android.R.layout.simple_list_item_single_choice, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            with(view as CheckedTextView) {
                isChecked = preferenceHandler.getThemePref() == objects[position]
                typeface = UiUtils.getAbelRegularTypeFace(view.context)
            }
            return view
        }
    }

    private inner class MarginItemDecoration(private val spaceSize: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            with(outRect) {
                left = spaceSize
                right = spaceSize
                bottom = spaceSize
            }
        }
    }
}
