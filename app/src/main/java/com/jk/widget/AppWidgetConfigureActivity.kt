package com.jk.widget


import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.app_widget_configure.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


/**
 * The configuration screen for the [AppWidget] AppWidget.
 */
class AppWidgetConfigureActivity : Activity() {
    internal var mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    // val mAppWidgetText:EditText by lazy { findViewById(R.id.appwidget_text) }
    internal var mOnClickListener: View.OnClickListener = View.OnClickListener {
        val context = this@AppWidgetConfigureActivity

        // When the button is clicked, store the string locally
        //  val widgetText = appwidget_text.text.toString()
        //   saveTitlePref(context, mAppWidgetId, widgetText)

        // It is the responsibility of the configuration activity to update the app widget
        val appWidgetManager = AppWidgetManager.getInstance(context)
        AppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
        setResult(Activity.RESULT_OK, resultValue)
        finish()
    }


    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.app_widget_configure)
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
            adapter = RecyclerViewAda(this@AppWidgetConfigureActivity)
        }

        //appwidget_text.setText(loadTitlePref(this@AppWidgetConfigureActivity, mAppWidgetId))
    }


    class RecyclerViewAda(appWidgetConfigureActivity: AppWidgetConfigureActivity) : RecyclerView.Adapter<RecyclerViewAda.MyViewHolder>() {

        val data = constructTimezoneAdapter()
        val appWidgetConfigureActivity = appWidgetConfigureActivity
        private fun constructTimezoneAdapter(): Array<out String> {
            val TZ = TimeZone.getAvailableIDs()
            val TZ1 = ArrayList<String>()
            for (i in TZ.indices) {
                if (!TZ1.contains(TimeZone.getTimeZone(TZ[i]).displayName)) {

                    TZ1.add(TimeZone.getTimeZone(TZ[i]).displayName)
                }
            }
            return TZ
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAda.MyViewHolder {
            val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_time_zone, parent, false)
            return MyViewHolder(layout)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: RecyclerViewAda.MyViewHolder, position: Int) {
            holder.bind(data[position])
        }

        class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            fun bind(data: String) {
                val timezone = TimeZone.getTimeZone(data)


                val cal = Calendar.getInstance(Locale.ENGLISH)
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
                itemView.findViewById<TextView>(R.id.txt_timezoneId).text = data
                itemView.findViewById<TextView>(R.id.txt_timezoneDelay).text = dateforrow
                //itemView.setOnClickListener(RecyclerViewAda.appWidgetConfigureActivity.mOnClickListener)
            }
        }
    }


    companion object {

        private val PREFS_NAME = "com.jk.widget.AppWidget"
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

