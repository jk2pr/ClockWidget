package com.jk.mr.duo.clock


import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode.FULLSCREEN
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode.OVERLAY
import com.jk.mr.duo.clock.data.Results
import com.jk.mr.duo.clock.di.components.AppComponent
import com.jk.mr.duo.clock.di.components.DaggerAppComponent
import com.jk.mr.duo.clock.di.modules.NetworkModule
import com.jk.mr.duo.clock.services.IApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_widget_configure.*
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

    // val adapter by lazy { DataAdapter(this@AppWidgetConfigureActivity) }

    public override fun onCreate(icicle: Bundle?) {

        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }
        val theme = getThemePref()
        setTheme(theme)
        super.onCreate(icicle)
        appComponent = DaggerAppComponent.builder()
                .networkModule(NetworkModule())
                .build()
        appComponent.inject(this)

        setContentView(R.layout.app_widget_configure)
        setSupportActionBar(toolbar)
        Places.initialize(applicationContext, getString(R.string.apiKey))

     //   calendar.add(Calendar.HOUR, -2)

        // Initialize the AutocompleteSupportFragment.
      /*  val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as (AutocompleteSupportFragment)

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {

          override  fun onPlaceSelected( place:Place) {
                // TODO: Get info about the selected place.

                Log.i(TAG, "Place: " + place.getName() + ", " + place.id)
             // val p = Autocomplete.getPlaceFromIntent(place)
              Log.i("TAG", "Place: " + place.name + ", " + place.id)
              requestData(place.latLng!!.latitude.toString(), place.latLng!!.longitude.toString())
            }

       override     fun onError( status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        })*/






        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        //  setResult(Activity.RESULT_CANCELED)



        // toolbar fancy stuff
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.action_search)

        // Find the widget id from the intent.


        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }


    // val mAppWidgetText:EditText by lazy { findViewById(R.id.appwidget_text) }
    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@AppWidgetConfigureActivity

        val timezonSelected = it.tag as String
        // When the button is clicked, store the string locally
        //  val widgetText = appwidget_text.text.toString()
        saveTitlePref(context, mAppWidgetId, timezonSelected)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //   super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    Log.i("TAG", "Place: " + place.name + ", " + place.id)
                    requestData(place.latLng!!.latitude.toString(), place.latLng!!.longitude.toString())
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Log.i("TAG", status.statusMessage)
                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        // Associate searchable configuration with the SearchView
        /* val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
         val searchView = menu.findItem(R.id.action_search)
                 .actionView as SearchView
         searchView.setSearchableInfo(searchManager
                 .getSearchableInfo(componentName))
         searchView.maxWidth = Integer.MAX_VALUE*/


        // listening to search query text change
        /* searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
             override fun onQueryTextSubmit(query: String): Boolean {
                 // filter recycler view when query submitted
                 showLoader(true)
                 val searchText = "%$query%"
                 requestData(searchText)

                 return false
                 //  adapter.filter.filter(query)
             }

             override fun onQueryTextChange(query: String): Boolean {
                 // filter recycler view when text is changed
                 adapter.filter.filter(query)
                 return false
             }
         })*/
        return true
    }


    private fun requestData(lat: String, long: String) {
        subscriptions.clear()
        val tsLong = System.currentTimeMillis() / 1000
        val ts = tsLong.toString()
        val location = lat.plus(",").plus(long)
        val subscribeOn = api.getTimeZoneFromLatLong(location, ts, getString(R.string.apiKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ abc ->
                    Log.d(AppWidgetConfigureActivity::class.java.simpleName, abc.timeZoneId)
                    //   print(abc.timeZoneId)
                    sendBackResult(abc)
                }

                )
                { e ->
                    run {
                        Log.d("ConfigureActivity", e.message)
                    }
                }

        subscriptions.add(subscribeOn)

    }


    private fun sendBackResult(abc: Results) {
        saveTitlePref(this, mAppWidgetId, abc.timeZoneId)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(this)
        AppWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_search -> {
                val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

                val intent = Autocomplete.IntentBuilder(
                        FULLSCREEN, fields)
                        .build(this)
                startActivityForResult(intent, 0)

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
        val arrayAdapter = MArrayAdapter(this, ar)

        dialog.setNegativeButton("cancel") { d, _ -> d.dismiss() }

        dialog.setAdapter(arrayAdapter) { _, which ->
            val strName = arrayAdapter.getItem(which)!!
            saveThemePref(this, mAppWidgetId, strName/* (

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


    private fun getThemePref(): Int {
        val sThem = loadBgColorPref(this, mAppWidgetId)
        return when (sThem) {
            THEME_LIGHT -> R.style.AppThemeLight
            THEME_DARK -> R.style.AppThemeDark
            THEME_RED -> R.style.AppThemeRed
            THEME_YELLOW -> R.style.AppThemeYellow
            THEME_GREEN -> R.style.AppThemeGreen
            THEME_BLUE -> R.style.AppThemeBlue
            THEME_INDIGO -> R.style.AppThemeIndigo
            THEME_ORANGE -> R.style.AppThemeOrange
            else -> R.style.AppThemeLight
        }
    }


    companion object {

        private const val PREFS_NAME = "com.jk.mr.dualclock.widget.AppWidget"
        private const val PREF_PREFIX_KEY = "appwidget_"
        private const val PREF_INFIX_KEY = "background_"


        const val THEME_DARK = "THEME_DARK"
        const val THEME_LIGHT = "THEME_LIGHT"
        const val THEME_RED = "THEME_RED"
        const val THEME_ORANGE = "THEME_ORANGE"
        const val THEME_YELLOW = "THEME_YELLOW"
        const val THEME_GREEN = "THEME_GREEN"
        const val THEME_BLUE = "THEME_BLUE"
        const val THEME_INDIGO = "THEME_INDIGO"

        const val TEXT_AM = "am"
        const val TEXT_PM = "pm"


        const val TAG = "AppWidgetConfigure"


        lateinit var appComponent: AppComponent

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, text).apply()
        }


        internal fun saveThemePref(context: Context, appWidgetId: Int, theme: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY.plus(PREF_INFIX_KEY).plus(appWidgetId), theme).apply()
        }

        internal fun loadBgColorPref(context: Context, appWidgetId: Int): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            return prefs.getString(PREF_PREFIX_KEY.plus(PREF_INFIX_KEY).plus(appWidgetId), THEME_LIGHT)
                    ?: THEME_LIGHT
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
            return titleValue ?: context.getString(R.string.appwidget_text)
        }

        internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId).apply()
        }
    }
}

private class MArrayAdapter(val act: AppWidgetConfigureActivity, val objects: Array<String>) : ArrayAdapter<String>(act, android.R.layout.simple_list_item_single_choice, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as CheckedTextView).isChecked = AppWidgetConfigureActivity.loadBgColorPref(context, act.mAppWidgetId) == objects[position]
        return view
    }

}
