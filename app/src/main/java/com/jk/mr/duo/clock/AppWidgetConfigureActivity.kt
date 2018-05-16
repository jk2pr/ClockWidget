package com.jk.mr.duo.clock


import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import kotlinx.android.synthetic.main.app_widget_configure.*

import java.util.*
import kotlin.collections.ArrayList
import android.support.v4.view.MenuItemCompat.getActionView
import android.app.SearchManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*


/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
class AppWidgetConfigureActivity : AppCompatActivity() {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    val adapter by lazy { RecyclerViewAda(this@AppWidgetConfigureActivity) }

    // val mAppWidgetText:EditText by lazy { findViewById(R.id.appwidget_text) }
    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@AppWidgetConfigureActivity

        val timazonSelected = it.tag as TimeZone
        // When the button is clicked, store the string locally
        //  val widgetText = appwidget_text.text.toString()
        saveTitlePref(context, mAppWidgetId, timazonSelected.id)

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
                .getActionView() as SearchView
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(componentName))
        searchView.setMaxWidth(Integer.MAX_VALUE)

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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()


        return if (id == R.id.action_search) {
            true
        } else super.onOptionsItemSelected(item)

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


    inner class RecyclerViewAda(appWidgetConfigureActivity: AppWidgetConfigureActivity) : RecyclerView.Adapter<RecyclerViewAda.MyViewHolder>(), Filterable {

        val oriGinaldata = constructTimezoneAdapter()
        val filteredData = oriGinaldata.clone() as ArrayList<String>
        // val appWidgetConfigureActivity = appWidgetConfigureActivity
        private fun constructTimezoneAdapter(): ArrayList<String> {
            val T = TimeZone.getAvailableIDs()
            val TZ = ArrayList<String>()
            TZ.addAll(T.asList())
            TZ.sorted()
            return TZ
        }

        override fun getFilter(): Filter {

            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                    val charString = charSequence.toString()
                    val filteredList = ArrayList<String>()
                    if (charString.isEmpty()) {
                        filteredList.addAll(oriGinaldata)
                    } else {
                        for (row in oriGinaldata) {
                            val d: String
                            if (row.contains("/"))
                                d = row.split("/")[1]
                            else
                                d = row
                            if (d.contains(charString, true)) {
                               if(!filteredList.contains(row))
                                  filteredList.add(row)
                            }
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
                /*val form = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                val dateforrow = form.format(cal.time)*/

                var hour = String.format("%02d", cal.get(Calendar.HOUR))
                if (hour == "00") hour = "12"
                val minute = String.format("%02d", cal.get(Calendar.MINUTE))
                val am_pm = cal.get(Calendar.AM_PM)
                val ap: String
                if (am_pm == 0) {
                    ap = "AM"
                } else ap = "PM"
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
                itemView.tag = timezone
                itemView.setOnClickListener(mOnClickListener)
            }
        }
    }


    companion object {

        private val PREFS_NAME = "com.jk.mr.dualclock.widget.AppWidget"
        private val PREF_PREFIX_KEY = "appwidget_"

        // Write the prefix to the SharedPreferences object for this widget
        internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
            val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
            prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
            prefs.apply()
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
            prefs.remove(PREF_PREFIX_KEY + appWidgetId)
            prefs.apply()
        }
    }
}

