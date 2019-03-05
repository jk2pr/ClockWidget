package com.jk.mr.duo.clock


import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.app_widget_configure.*

import java.util.*
import kotlin.collections.ArrayList
import android.app.SearchManager
import android.content.SharedPreferences
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*
import javax.inject.Inject
import android.R.drawable.btn_dialog
import android.app.Dialog
import android.content.ComponentName
import android.support.v4.content.ContextCompat
import android.view.Window.FEATURE_NO_TITLE
import android.widget.*
import com.jk.mr.duo.clock.utils.ViewUtils
import com.rtugeek.android.colorseekbar.ColorSeekBar
import kotlinx.android.synthetic.main.layout_seekbar.*


/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
class AppWidgetConfigureActivity : AppCompatActivity() {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID


    val adapter by lazy { RecyclerViewAda(this@AppWidgetConfigureActivity) }

    // val mAppWidgetText:EditText by lazy { findViewById(R.id.appwidget_text) }
    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@AppWidgetConfigureActivity

        val timezonSelected = it.tag as String
        // When the button is clicked, store the string locally
        //  val widgetText = appwidget_text.text.toString()
        saveTitlePref(context, timezonSelected)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search)
                .actionView as SearchView
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(componentName))
        searchView.maxWidth = Integer.MAX_VALUE

        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                adapter.filter.filter(query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_search -> true
            R.id.action_setting -> {

                showColorPickerSeekbar()
                //startActivity(Intent(this, PreferenceActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    private fun showColorPickerSeekbar() {


        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.layout_seekbar)


        val seekBar = dialog.findViewById(R.id.colorSlider) as ColorSeekBar
        /* seekBar.setOnColorChangeListener(ColorSeekBar.OnColorChangeListener { colorBarPosition, alphaBarPosition, color ->
          Toast.makeText(this,color.toString(),Toast.LENGTH_SHORT).show()
          //   textView.setTextColor(color)
             //colorSeekBar.getAlphaValue();
         })*/


        val btOk = dialog.findViewById(R.id.bt_ok) as Button

        btOk.setOnClickListener {


            val manager = AppWidgetManager.getInstance(this)
            val name = ComponentName(this, AppWidget::class.java)
            val appIds = manager.getAppWidgetIds(name)
            saveBgColorPref(it.context ,seekBar.color)

            for (id in appIds) {

                val v = RemoteViews(packageName, R.layout.app_widget)
                AppWidget.updateAppWidget(this, manager, id)
                //manager.updateAppWidget(id, v)
            }
            dialog.dismiss()
        }

        dialog.show()

    }

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        //  setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.app_widget_configure)


        setSupportActionBar(toolbar)

        // toolbar fancy stuff
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.select_city)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }


        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@AppWidgetConfigureActivity)
            adapter = this@AppWidgetConfigureActivity.adapter
        }

        //appwidget_text.setText(loadTitlePref(this@AppWidgetConfigureActivity, mAppWidgetId))
    }


    inner class RecyclerViewAda( val appWidgetConfigureActivity: AppWidgetConfigureActivity) : RecyclerView.Adapter<RecyclerViewAda.MyViewHolder>(), Filterable {

        val originalData = constructTimezoneAdapter()
        val filteredData = originalData.clone() as ArrayList<String>
        // val appWidgetConfigureActivity = appWidgetConfigureActivity
        private fun constructTimezoneAdapter(): ArrayList<String> {

            val inputStream = ViewUtils.getFileByResourceId(appWidgetConfigureActivity, R.raw.timezone)
            val lineList = mutableListOf<String>()
            inputStream.bufferedReader().useLines { lines ->
                lines.forEach {
                    lineList.add(it)
                }
            }



            val T = TimeZone.getAvailableIDs()
            val TZ = ArrayList<String>()
            val nod=T.distinct()
            TZ.addAll(nod)
          //  TZ.addAll(lineList.distinct())
            TZ.sorted()
            return TZ
        }

        override fun getFilter(): Filter {

            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                    val charString = charSequence.toString()
                    val filteredList = ArrayList<String>()
                    if (charString.isEmpty()) {
                        filteredList.addAll(originalData)
                    } else {
                        for (row in originalData) {
                            val d: String
                            if (row.contains("/"))
                                d = row.split("/")[1]
                            else
                                d = row
                            if (d.contains(charString, true)) if (!filteredList.contains(row))
                                filteredList.add(row)
                        }
                        // filteredData.addAll(filteredList.sorted())
                    }

                    val filterResults = Filter.FilterResults()
                    filterResults.values = filteredList
                    return filterResults
                }

                override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                    filteredData.clear()
                    filteredData.addAll(filterResults.values as ArrayList<String>)
                    notifyDataSetChanged()
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_time_zone, parent, false)
            return MyViewHolder(layout)
        }

        override fun getItemCount(): Int {
            return filteredData.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.bind(filteredData[position])
        }

        inner class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            fun bind(data: String) {
                val timezone = TimeZone.getTimeZone(data)


                val cal = Calendar.getInstance(Locale.getDefault())
                cal.timeZone = timezone
                var hour = String.format("%02d", cal.get(Calendar.HOUR))
                if (hour == "00") hour = "12"
                val minute = String.format("%02d", cal.get(Calendar.MINUTE))
                val ampm = cal.get(Calendar.AM_PM)
                val ap = if (ampm == 0) {
                    "AM"
                } else "PM"
                val dateforrow = hour + ":" + minute + ":" + ap
                /*val zone = DateTimeZone.forID(data)
                val dateTime = DateTime(zone)
                val output = dateTime.toLocalTime().toDateTimeToday()
*/

                /*val d: String
                if (data.toLowerCase().contains("/"))
                    d = data.toLowerCase().split("/")[1]
                else
                    d = data.toLowerCase()*/
                itemView.findViewById<TextView>(R.id.txt_timezoneId).text = data
                itemView.findViewById<TextView>(R.id.txt_timezoneDelay).text = dateforrow
                itemView.tag = data
                itemView.setOnClickListener(mOnClickListener)
            }
        }
    }


    companion object {

        private val PREFS_NAME = "com.jk.mr.dualclock.widget.AppWidget"
        private val PREF_PREFIX_KEY = "appwidget_"
        private val PREF_INFIX_KEY = "background_"


        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveTitlePref(context: Context, text: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY, text).apply()
        }

        internal fun saveBgColorPref(context: Context, color: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putInt(PREF_PREFIX_KEY.plus(PREF_INFIX_KEY), color).apply()
        }

        internal fun loadBgColorPref(context: Context): Int {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val bgColor = prefs.getInt(PREF_PREFIX_KEY.plus(PREF_INFIX_KEY), 0)
            return bgColor
            /*      ?: "#".plus(Integer.toHexString(ContextCompat.getColor(context, R.color.bgcolor))""*/
        }

        // Read the prefix from the SharedPreferences object for this widget.
        // If there is no preference saved, get the default from a resource
        internal fun loadTitlePref(context: Context): String {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0)
            val titleValue = prefs.getString(PREF_PREFIX_KEY , null)
            return titleValue ?: context.getString(R.string.appwidget_text)
        }

        internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.remove(PREF_PREFIX_KEY + appWidgetId).apply()
        }
    }
}

