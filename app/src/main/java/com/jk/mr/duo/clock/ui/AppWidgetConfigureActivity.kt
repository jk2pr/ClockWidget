package com.jk.mr.duo.clock.ui

import Start
import android.appwidget.AppWidgetManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.jk.mr.duo.clock.AppWidget
import com.jk.mr.duo.clock.MrDuoClockApplication
import com.jk.mr.duo.clock.common.localproviders.LocalNavController
import com.jk.mr.duo.clock.common.localproviders.LocalSnackBarHostState
import com.jk.mr.duo.clock.ui.theme.ClockTheme
import com.jk.mr.duo.clock.viewmodels.CalDataViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
@AndroidEntryPoint
class AppWidgetConfigureActivity : ComponentActivity() {

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        intent.extras?.let {
            mAppWidgetId = it.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        setContent {
            CompositionLocalProvider(
                LocalNavController provides rememberNavController(),
                LocalSnackBarHostState provides remember { SnackbarHostState() }
            ) {
                ClockTheme(
                    content = { Start(this) },
                )
            }
        }

        // setTheme(getThemePref())
        // setContentView(R.layout.app_widget_configure)

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
/*
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
        *//* dataAdapter.removeItem(position)
         val itemSwipedSnackBar = Snackbar.make(
             root_coordinate,
             getString(R.string.itemRemovedMessage, item.name),
             Snackbar.LENGTH_SHORT
         )
         itemSwipedSnackBar.setAction(getString(R.string.undoCaps)) {
             dataAdapter.insertItem(position, item)
         }
         itemSwipedSnackBar.show()*//*
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

    *//*fun getResultFromDialog(carmenFeature: CarmenFeature) {
        val address = carmenFeature.placeName()!!
        val country = carmenFeature.placeName()!!.split(",").last()
        val place = carmenFeature.geometry() as Point
        viewModel.getData(
            address,
            country,
            place.latitude().toString().plus(",").plus(place.longitude().toString())
        )
    }*//*


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

       *//* supportFragmentManager.let {
            val searchFragmentDialog = SearchFragmentDialog()
            searchFragmentDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog)
            searchFragmentDialog.apply {
                show(it, tag)
            }
        }*//*
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
}*/
}
