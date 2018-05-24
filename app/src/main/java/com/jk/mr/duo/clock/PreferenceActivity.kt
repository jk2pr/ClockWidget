package com.jk.mr.duo.clock

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceActivity
import android.widget.Toast
import com.skydoves.colorpickerpreference.ColorPickerDialog
import javax.inject.Inject


class PreferenceActivity : AppCompatPreferenceActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupActionBar()

    }

    override fun isValidFragment(fragmentName: String?): Boolean {
        return true
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }


    override fun onHeaderClick(header: PreferenceActivity.Header, position: Int) {
        super.onHeaderClick(header, position)
        openDialog(R.id.pref_header_clock_color)


    }


    private fun openDialog(id: Int) {

        val builder = ColorPickerDialog.Builder(this)
        builder.setTitle("ColorPicker Dialog")
        builder.setPreferenceName(id.toString())
        builder.setPositiveButton("Confirm") { colorEnvelope ->
            Toast.makeText(this@PreferenceActivity, colorEnvelope.colorHtml, Toast.LENGTH_SHORT).show()
            builder.colorPickerView.saveData()
        }
        builder.setNegativeButton("Cancel") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.show()
    }


}
