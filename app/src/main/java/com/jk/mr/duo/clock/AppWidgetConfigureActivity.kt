package com.jk.mr.duo.clock

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.data.caldata.Resource
import com.jk.mr.duo.clock.utils.*
import com.jk.mr.duo.clock.utils.Constants.ACTION_ADD_CLOCK
import com.jk.mr.duo.clock.utils.Constants.themeArray
import com.jk.mr.duo.clock.viewmodels.CalDataViewModel
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.app_widget_configure.*
import kotlinx.android.synthetic.main.content_dash_board.*
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

    private val dataAdapter by lazy {
        DataAdapter(this@AppWidgetConfigureActivity) { selectedDate ->
            preferenceHandler.saveTimeZonePref(selectedDate.toJSON())
            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            val manager = AppWidgetManager.getInstance(this)
            val name = ComponentName(this, AppWidget::class.java)
            val appIds = manager.getAppWidgetIds(name)
            for (appWidgetId in appIds) appWidgetHelper.updateAppWidget(this, manager, appWidgetId)
        }
    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setTheme(getThemePref())
        setContentView(R.layout.app_widget_configure)
        intent.extras?.let {
            mAppWidgetId = it.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        viewModel.mutableState.observe(this, { calendarData ->
            calendarData?.let {
                when (it) {
                    is Resource.Success -> {
                        updateAdapter(it.calData)
                        showLoader(false)
                    }
                    is Resource.Loading ->
                        showLoader(true)
                    is Resource.Error ->
                        showError()
                    else -> Unit
                }
            }
        })

        setSupportActionBar(toolbar)
        title = null
        recycler_clock.apply {
            adapter = dataAdapter
            layoutManager = LinearLayoutManager(this@AppWidgetConfigureActivity)
            swipeListener = onItemSwipeListener
        }
        handleDashBoardClock()
        fab.setOnClickListener { openSearchDialog() }
        Looper.myLooper()?.let {
            if (intent.action == ACTION_ADD_CLOCK) Handler(it).postDelayed(
                { fab.performClick() },
                100
            )
        }
    }

    private fun showError() {
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

    }

    private val onItemSwipeListener = object : OnItemSwipeListener<CalData> {
        override fun onItemSwiped(
            position: Int,
            direction: OnItemSwipeListener.SwipeDirection,
            item: CalData
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
        dataAdapter.removeItem(position)
        val itemSwipedSnackBar = Snackbar.make(
            root_coordinate,
            getString(R.string.itemRemovedMessage, item.name),
            Snackbar.LENGTH_SHORT
        )
        itemSwipedSnackBar.setAction(getString(R.string.undoCaps)) {
            dataAdapter.insertItem(position, item)
        }
        itemSwipedSnackBar.show()
    }

    private fun handleDashBoardClock() {
        dashboard_clock.apply {
            format12Hour = Utils.getDashBoard12HoursFormat()
            format24Hour = Utils.getDashBoard24HoursFormat()
            typeface = UiUtils.getBebasneueRegularTypeFace(this@AppWidgetConfigureActivity)
        }
        TimeZone.getDefault().id.let { currentTimeZone ->
            var tz = currentTimeZone
            if (tz.contains("/")) tz = currentTimeZone.split("/")[1].replace("_", " ")
            dashboard_timezone.text = tz
        }
    }

    override fun onResume() {
        super.onResume()
        val jsonString = preferenceHandler.getDateData()
        jsonString?.let {
            if (it.isEmpty()) return
            val listType = object : TypeToken<List<CalData>>() {}.type
            val storedData = Gson().fromJson<List<CalData>>(jsonString, listType)
            if (storedData.isNotEmpty()) dataAdapter.dataSet = storedData // get from stored
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (dataAdapter.itemCount > 0) dataAdapter.updateClock(dataAdapter.dataSet[0])
    }

    override fun onPause() {
        super.onPause()
        preferenceHandler.saveDateData(dataAdapter.dataSet)
        dashboard_timezone.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    }

    fun getResultFromDialog(carmenFeature: CarmenFeature) {
        val address = carmenFeature.placeName()!!
        val country = carmenFeature.placeName()!!.split(",").last()
        val place = carmenFeature.geometry() as Point
        showLoader(isLoading = true)
        viewModel.getData(
            address,
            country,
            place.latitude().toString().plus(",").plus(place.longitude().toString())
        )
    }

    private fun showLoader(isLoading: Boolean) {
        progress_horizontal.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun updateAdapter(calData: CalData) {
        preferenceHandler.saveTimeZonePref(calData.toJSON())
        recycler_clock.layoutManager?.scrollToPosition(0)
        dataAdapter.addCal(calData)
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
                for (appWidgetId in appIds) appWidgetHelper.updateAppWidget(
                    this@AppWidgetConfigureActivity,
                    manager,
                    appWidgetId
                )
            }
        }.show()
    }

    private inner class MyArrayAdapter(
        act: AppWidgetConfigureActivity,
        val objects: Array<String>
    ) : ArrayAdapter<String>(act, android.R.layout.simple_list_item_single_choice, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            (view as CheckedTextView).isChecked =
                preferenceHandler.getThemePref() == objects[position]
            return view
        }
    }
}
