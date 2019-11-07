package com.jk.mr.duo.clock


import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import androidx.core.content.ContextCompat
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
import com.jk.mr.duo.clock.utils.Constants.THEME_BLUE
import com.jk.mr.duo.clock.utils.Constants.THEME_DARK
import com.jk.mr.duo.clock.utils.Constants.THEME_GREEN
import com.jk.mr.duo.clock.utils.Constants.THEME_INDIGO
import com.jk.mr.duo.clock.utils.Constants.THEME_LIGHT
import com.jk.mr.duo.clock.utils.Constants.THEME_ORANGE
import com.jk.mr.duo.clock.utils.Constants.THEME_RED
import com.jk.mr.duo.clock.utils.Constants.THEME_YELLOW
import com.jk.mr.duo.clock.utils.Constants.appComponent
import com.jk.mr.duo.clock.utils.Constants.deleteAllPref
import com.jk.mr.duo.clock.utils.Constants.getBebasneueRegularTypeFace
import com.jk.mr.duo.clock.utils.Constants.getDateData
import com.jk.mr.duo.clock.utils.Constants.getThemePref
import com.jk.mr.duo.clock.utils.Constants.saveDateData
import com.jk.mr.duo.clock.utils.Constants.saveThemePref
import com.jk.mr.duo.clock.utils.Constants.saveTitlePref
import com.jk.mr.duo.clock.utils.DataAdapter
import com.jk.mr.duo.clock.utils.Utils
import com.jk.mr.duo.clock.utils.YourDialogFragment
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_widget_configure.*
import kotlinx.android.synthetic.main.content_dash_board.*
import java.util.*
import javax.inject.Inject


/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
class AppWidgetConfigureActivity : AppCompatActivity() {


    @Inject
    lateinit var api: IApi

    var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    var subscriptions = CompositeDisposable()


    private fun makeString(it: CalData): String {


        return it.address
                .plus(SEPARATOR)
                .plus(it.name)
                .plus(SEPARATOR)
                .plus(it.currentCityTimeZone)
                .plus(SEPARATOR)
                .plus(it.abbreviation)
    }

    private val dataAdapter by lazy {
        val dd = DataAdapter(this@AppWidgetConfigureActivity) {

            val timeZoneId = makeString(it)

            saveTitlePref(this, timeZoneId)
            // recreate()


            // Make sure we pass back the original appWidgetId
            val resultValue = Intent()
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
            setResult(Activity.RESULT_OK, resultValue)


            val manager = AppWidgetManager.getInstance(this)
            val name = ComponentName(this, AppWidget::class.java)
            val appIds = manager.getAppWidgetIds(name)
            for (appWidgetId in appIds) {
                AppWidget.updateAppWidget(this, manager, appWidgetId)
            }


            /*val appWidgetManager = AppWidgetManager.getInstance(this)
            AppWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId)*/
        }




        dd
    }

    fun abc() {
        if (dataAdapter.itemCount==0)
            txt_empty.visibility = View.VISIBLE
        else
            txt_empty.visibility = View.GONE
    }

    public override fun onCreate(icicle: Bundle?) {

        val theme = getThemePref()
        setTheme(theme)
        super.onCreate(icicle)
        setContentView(R.layout.app_widget_configure)
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        /*if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

*/
        val pInfo = packageManager.getPackageInfo(packageName, 0)
        val version = pInfo.versionCode
        if (version < 12)
            deleteAllPref(this)


        appComponent = DaggerAppComponent.builder()
                .networkModule(NetworkModule())
                .build()
        appComponent.inject(this)


        // initToolbarDimension()
        setSupportActionBar(toolbar)
        title = null

        recycler_clock.apply {
            adapter = dataAdapter
            layoutManager = LinearLayoutManager(this@AppWidgetConfigureActivity)

            // addItemDecoration(itemDecorator)
        }
        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = recycler_clock.adapter as DataAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recycler_clock)






        handleDashBoardClock()

        fab.setOnClickListener {

            openSearchDialog()
        }


        if (intent.action == ACTION_ADD_CLOCK)
            Handler().postDelayed({
                fab.performClick()
            }, 100)

        // Find the widget id from the intent.


        // If this activity was started with an intent without an app widget ID, finish with an error.


    }

    private fun handleDashBoardClock() {

        dashboard_clock.format12Hour = Utils.getDashBoard12HoursFormat()
        dashboard_clock.format24Hour = Utils.getDashBoard24HoursFormat()
        var currentTimeZone = TimeZone.getDefault().id
        if (currentTimeZone.contains("/"))
            currentTimeZone = currentTimeZone.split("/")[1].replace("_", " ")
        dashboard_timezone.text = currentTimeZone
        // dashboard_timezone.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        val typeface = getBebasneueRegularTypeFace(this)
        dashboard_clock.typeface = typeface
        // dashboard_timezone.typeface = typeface


    }


    /*private fun initToolbarDimension() {

       *//* window.apply {
            setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
           // addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor=Color.RED
        }*//*

        val params = toolbar.layoutParams as RelativeLayout.LayoutParams
        params.setMargins(0, getStatusBarHeight(), 0, 0)
        toolbar.layoutParams = params
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
*/

    private val dataObservable = object : RecyclerView.AdapterDataObserver() {

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            abc()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            abc()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            abc()
        }


    }


    override fun onResume() {
        super.onResume()
        val jsonString = getDateData(this)
        dataAdapter.registerAdapterDataObserver(dataObservable)

        jsonString?.let {

            if (it.isEmpty())
                return
            val listType = object : TypeToken<List<CalData>>() {}.type
            val storedData = Gson().fromJson<List<CalData>>(jsonString, listType)
            if (storedData.isNotEmpty()) {
                //get from stored
                dataAdapter.addAll(storedData)
            }
        }


    }

    override fun onPostResume() {
        super.onPostResume()
        if (dataAdapter.itemCount > 0)
            dataAdapter.listener.invoke(dataAdapter.data[0])

        abc()

    }

    override fun onPause() {
        super.onPause()
        saveDateData(this, dataAdapter.data)
        if (dataAdapter.hasObservers())
            dataAdapter.unregisterAdapterDataObserver(dataObservable)


        dashboard_timezone.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
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
        //val tsLong = System.currentTimeMillis() / 1000
        //  val ts = tsLong.toString()
        val location = lat.plus(",").plus(long)
        val subscribeOn = api.getTimeZoneFromLatLong(location, /*ts,*/ BuildConfig.MAP_TIMEZONE_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ abc ->
                    val timeZoneId = abc.resourceSets[0].resources[0].timeZone.ianaTimeZoneId

                    val abbreviation =
                            /*   if (abc.resourceSets[0].resources[0].timeZone.abbreviation != null) {
                            abc.resourceSets[0].resources[0].timeZone.abbreviation // Timber.d(AppWidgetConfigureActivity::class.java.simpleName, timeZoneId)
                        } else*/
                            abc.resourceSets[0].resources[0].timeZone.windowsTimeZoneId
                    print("abbreviation $abbreviation")



                    if (timeZoneId == null)
                        sendBackResult(timeZoneId)
                    else
                        sendBackResult(address
                                .plus(SEPARATOR)
                                .plus(country)
                                .plus(SEPARATOR)
                                .plus(timeZoneId)
                                .plus(SEPARATOR)
                                .plus(abbreviation))

                }

                )
                {
                    run {
                        // Timber.d(e)
                    }
                }

        subscriptions.add(subscribeOn)

    }


    private fun sendBackResult(timeZoneId: String?) {
        if (timeZoneId != null) {
            saveTitlePref(this, timeZoneId)
            showDataInAdapter(timeZoneId)

            /*val result = Intent()
            result.putExtra(DashBoardActivity.CAL_DATA, timeZoneId)
            setResult(Activity.RESULT_OK, result)
            finish()*/


            // It is the responsibility of the configuration activity to update the app widget

            /* val appWidgetManager = AppWidgetManager.getInstance(this)
             AppWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId)

             // Make sure we pass back the original appWidgetId
             val resultValue = Intent()
             resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
             setResult(Activity.RESULT_OK, resultValue)*/
            // finish()
        } else {

            Toast.makeText(applicationContext, "Unknown Location found, Please enter exact location.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showDataInAdapter(dd: String) {

        val list = dd.split(SEPARATOR)
        val address = list.first().split(",").dropLast(1).joinToString()
        val country = list[1].trim().replace("United States", "United States of America")
                .replace("United Kingdom", "United Kingdom of Great Britain and Northern Ireland")

        val timeZone = list[2]
        val abbreviation = list.last()
        assets.open("data.json").apply {
            val jsonString = readBytes().toString(Charsets.UTF_8)
            val listType = object : TypeToken<List<CalData>>() {

            }.type
            val calData = Gson().fromJson<List<CalData>>(jsonString, listType).filter {
                it.name.trim().equals(country, true)
            } // Dummy
            if (calData.isEmpty())
                return
            val singleCalData = calData.first()
            singleCalData.address = address
            singleCalData.currentCityTimeZone = timeZone
            singleCalData.abbreviation = abbreviation
            dataAdapter.addCal(singleCalData)


        }.close()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_search -> {
                //    val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

                //  val intent = Autocomplete.IntentBuilder(
                //           FULLSCREEN, fields)
                //           .build(this)

                /*  val intent = PlaceAutocomplete.IntentBuilder()
                          .accessToken(BuildConfig.PLACE_KEY)
                          .build(this)
                  startActivityForResult(intent, 0)
                  */
                openSearchDialog()

                true
            }
            R.id.action_setting -> {

                showThemePickerDialog()
                //startActivity(Intent(this, PreferenceActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun openSearchDialog() {

        val yourDialogFragment = YourDialogFragment()
        yourDialogFragment.show(supportFragmentManager, TAG)


    }

    private fun showThemePickerDialog() {


        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(true)
        val ar = arrayOf(
                THEME_DARK,
                THEME_LIGHT,
                THEME_RED,
                THEME_ORANGE,
                THEME_YELLOW,
                THEME_GREEN,
                THEME_BLUE,
                THEME_INDIGO
        )
        val arrayAdapter = MyArrayAdapter(this, ar)

        dialog.setNegativeButton("cancel") { d, _ -> d.dismiss() }

        dialog.setAdapter(arrayAdapter) { _, which ->
            val strName = arrayAdapter.getItem(which)!!
            saveThemePref(this, strName/* (

                    if (strName == THEME_DARK)
                        0
                    else
                        1
                    )*/)
            recreate()
            val manager = AppWidgetManager.getInstance(this)
            val name = ComponentName(this, AppWidget::class.java)
            val appIds = manager.getAppWidgetIds(name)
            for (appWidgetId in appIds) {
                AppWidget.updateAppWidget(this, manager, appWidgetId)
            }

            /* val builderInner = AlertDialog.Builder(this)
             builderInner.setMessage(strName)
             builderInner.setTitle("Your Selected Item is")
             builderInner.setPositiveButton("Ok") { it, which0 -> dialog.dismiss() }
             builderInner.show()*/
        }

        dialog.show()

    }

    /* fun showLoader(isShowing: Boolean) {
         if (!isShowing) {
             recycler_view?.visibility = View.VISIBLE
             progress?.visibility = View.GONE
         } else {
             recycler_view?.visibility = View.GONE
             progress?.visibility = View.VISIBLE
         }

     }*/


}


private class MyArrayAdapter(act: AppWidgetConfigureActivity, val objects: Array<String>) : ArrayAdapter<String>(act, android.R.layout.simple_list_item_single_choice, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as CheckedTextView).isChecked = getThemePref(context) == objects[position]
        return view
    }

}
