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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jk.mr.duo.clock.callbacks.SwipeToDeleteCallback
import com.jk.mr.duo.clock.data.caldata.CalData
import com.jk.mr.duo.clock.di.components.DaggerAppComponent
import com.jk.mr.duo.clock.di.modules.NetworkModule
import com.jk.mr.duo.clock.services.IApi
import com.jk.mr.duo.clock.utils.Constants.ACTION_ADD_CLOCK
import com.jk.mr.duo.clock.utils.Constants.SEPARATOR
import com.jk.mr.duo.clock.utils.Constants.TAG
import com.jk.mr.duo.clock.utils.Constants.appComponent
import com.jk.mr.duo.clock.utils.Constants.deleteAllPref
import com.jk.mr.duo.clock.utils.Constants.getBebasneueRegularTypeFace
import com.jk.mr.duo.clock.utils.Constants.getDateData
import com.jk.mr.duo.clock.utils.Constants.getThemePref
import com.jk.mr.duo.clock.utils.Constants.saveDateData
import com.jk.mr.duo.clock.utils.Constants.saveThemePref
import com.jk.mr.duo.clock.utils.Constants.saveTimeZonePref
import com.jk.mr.duo.clock.utils.Constants.themeArray
import com.jk.mr.duo.clock.utils.DataAdapter
import com.jk.mr.duo.clock.utils.SearchFragmentDialog
import com.jk.mr.duo.clock.utils.Utils
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_widget_configure.*
import kotlinx.android.synthetic.main.content_dash_board.*
import java.util.TimeZone
import javax.inject.Inject

/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
class AppWidgetConfigureActivity : AppCompatActivity() {

    @Inject
    lateinit var api: IApi
    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    var subscriptions = CompositeDisposable()

    private val dataAdapter by lazy {
        DataAdapter(this@AppWidgetConfigureActivity) {
            saveTimeZonePref(this, getStringFromCalData(it))
            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
            setResult(Activity.RESULT_OK, resultValue)
            val manager = AppWidgetManager.getInstance(this)
            val name = ComponentName(this, AppWidget::class.java)
            val appIds = manager.getAppWidgetIds(name)
            for (appWidgetId in appIds) AppWidget.updateAppWidget(this, manager, appWidgetId)
        }
    }

    fun handleDataAdapterChanges() = if (dataAdapter.itemCount == 0) txt_empty.visibility = View.VISIBLE else txt_empty.visibility = View.GONE

    public override fun onCreate(icicle: Bundle?) {
        val theme = getThemePref()
        setTheme(theme)
        super.onCreate(icicle)
        setContentView(R.layout.app_widget_configure)
        val extras = intent.extras
        if (extras != null) mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        /*if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

*/
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = PackageInfoCompat.getLongVersionCode(pInfo)

        if (version < 12) deleteAllPref(this)
        appComponent = DaggerAppComponent.builder().networkModule(NetworkModule()).build()
        appComponent.inject(this)
        setSupportActionBar(toolbar)
        title = null
        recycler_clock.apply {
            adapter = dataAdapter
            layoutManager = LinearLayoutManager(this@AppWidgetConfigureActivity)
        }
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = (recycler_clock.adapter as DataAdapter).removeAt(viewHolder.absoluteAdapterPosition)
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recycler_clock)
        handleDashBoardClock()
        fab.setOnClickListener { openSearchDialog() }
        Looper.myLooper()?.let { if (intent.action == ACTION_ADD_CLOCK) Handler(it).postDelayed({ fab.performClick() }, 100) }
    }

    private fun handleDashBoardClock() {
        dashboard_clock.apply {
            format12Hour = Utils.getDashBoard12HoursFormat()
            format24Hour = Utils.getDashBoard24HoursFormat()
            typeface = getBebasneueRegularTypeFace(this@AppWidgetConfigureActivity)
        }
        TimeZone.getDefault().id.also { currentTimeZone ->
            var tz = currentTimeZone
            if (tz.contains("/")) tz = currentTimeZone.split("/")[1].replace("_", " ")
            dashboard_timezone.text = tz
        }
    }

    private val dataObservable = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            handleDataAdapterChanges()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            handleDataAdapterChanges()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            handleDataAdapterChanges()
        }
    }

    override fun onResume() {
        super.onResume()
        val jsonString = getDateData(this)
        dataAdapter.registerAdapterDataObserver(dataObservable)
        jsonString?.let {
            if (it.isEmpty()) return
            val listType = object : TypeToken<List<CalData>>() {}.type
            val storedData = Gson().fromJson<List<CalData>>(jsonString, listType)
            if (storedData.isNotEmpty()) dataAdapter.addAll(storedData) // get from stored
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (dataAdapter.itemCount > 0) dataAdapter.listener.invoke(dataAdapter.data[0])
        handleDataAdapterChanges()
    }

    override fun onPause() {
        super.onPause()
        saveDateData(this, dataAdapter.data)
        if (dataAdapter.hasObservers()) dataAdapter.unregisterAdapterDataObserver(dataObservable)
        dashboard_timezone.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    }

    fun getResultFromDialog(carmenFeature: CarmenFeature) {
        val address = carmenFeature.placeName()!!
        val country = carmenFeature.placeName()!!.split(",").last()
        val place = carmenFeature.geometry() as Point
        requestData(address, country, place.latitude().toString(), place.longitude().toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    private fun requestData(address: String, country: String, lat: String, long: String) {
        subscriptions.clear()
        // val tsLong = System.currentTimeMillis() / 1000
        //  val ts = tsLong.toString()
        val location = lat.plus(",").plus(long)
        val subscribeOn = api.getTimeZoneFromLatLong(location, /*ts,*/ BuildConfig.MAP_TIMEZONE_KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ abc ->
                val timeZoneId = abc.resourceSets[0].resources[0].timeZone.ianaTimeZoneId
                val abbreviation = abc.resourceSets[0].resources[0].timeZone.windowsTimeZoneId
                print("abbreviation $abbreviation")
                if (timeZoneId == null) sendBackResult(timeZoneId)
                else sendBackResult(address.plus(SEPARATOR).plus(country).plus(SEPARATOR).plus(timeZoneId).plus(SEPARATOR).plus(abbreviation))
            }) {}
        subscriptions.add(subscribeOn)
    }

    private fun sendBackResult(timeZoneId: String?) {
        if (timeZoneId != null) {
            saveTimeZonePref(this, timeZoneId)
            showDataInAdapter(timeZoneId)
        } else Toast.makeText(applicationContext, "Unknown Location found, Please enter exact location.", Toast.LENGTH_SHORT).show()
    }

    private fun showDataInAdapter(data: String) {
        val list = data.split(SEPARATOR)
        val address = list.first().split(",").dropLast(1).joinToString()
        val country = list[1].trim().replace("United States", "United States of America")
            .replace("United Kingdom", "United Kingdom of Great Britain and Northern Ireland")
        val timeZone = list[2]
        val abbreviation = list.last()
        assets.open("data.json").apply {
            val jsonString = readBytes().toString(Charsets.UTF_8)
            val listType = object : TypeToken<List<CalData>>() {}.type
            val calData = Gson().fromJson<List<CalData>>(jsonString, listType).filter { it.name.trim().equals(country.trim(), true) }
            if (calData.isEmpty()) return
            val singleCalData = calData.first().apply {
                this.address = address
                currentCityTimeZone = timeZone
                this.abbreviation = abbreviation
            }
            dataAdapter.addCal(singleCalData)
        }.close()
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
        val searchFragmentDialog = SearchFragmentDialog()
        searchFragmentDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.myDialog)
        searchFragmentDialog.show(supportFragmentManager, TAG)
    }

    private fun showThemePickerDialog() {
        val arrayAdapter = MyArrayAdapter(this, themeArray)
        AlertDialog.Builder(this).apply {
            setCancelable(true)
            setNegativeButton("cancel") { d, _ -> d.dismiss() }
            setAdapter(arrayAdapter) { _, which ->
                val strName = arrayAdapter.getItem(which)!!
                saveThemePref(this@AppWidgetConfigureActivity, strName)
                recreate()
                val manager = AppWidgetManager.getInstance(this@AppWidgetConfigureActivity)
                val name = ComponentName(this@AppWidgetConfigureActivity, AppWidget::class.java)
                val appIds = manager.getAppWidgetIds(name)
                for (appWidgetId in appIds) AppWidget.updateAppWidget(this@AppWidgetConfigureActivity, manager, appWidgetId)
            }
        }.show()
    }

    private inner class MyArrayAdapter(act: AppWidgetConfigureActivity, val objects: Array<String>) : ArrayAdapter<String>(act, android.R.layout.simple_list_item_single_choice, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            (view as CheckedTextView).isChecked = getThemePref(context) == objects[position]
            return view
        }
    }
}
