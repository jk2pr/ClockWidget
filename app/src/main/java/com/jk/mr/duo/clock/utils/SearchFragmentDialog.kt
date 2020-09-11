package com.jk.mr.duo.clock.utils

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import com.jk.mr.duo.clock.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.BuildConfig
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.utils.Constants.TAG
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import android.view.WindowManager


class SearchFragmentDialog : DialogFragment() {

    val holdingActivity:AppWidgetConfigureActivity by lazy {
        activity as AppWidgetConfigureActivity
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.search_layout, container)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val typedValue = TypedValue()
        val theme = holdingActivity.theme
        theme.resolveAttribute(R.attr.selectedRowBackgroundColor, typedValue, true)
        @ColorInt val color = typedValue.data

        val placeOptions = PlaceOptions.builder()
                .toolbarColor(color)
              //  .backgroundColor(ContextCompat.getColor(activity as Context, android.R.color.holo_red_dark))
                .build(PlaceOptions.MODE_CARDS)
        val autocompleteFragment: PlaceAutocompleteFragment = PlaceAutocompleteFragment.newInstance(BuildConfig.PLACE_KEY, placeOptions)


        val h = (resources.displayMetrics.heightPixels * 0.50).toInt()
        val w = (resources.displayMetrics.widthPixels * 0.90).toInt()

        val lp = WindowManager.LayoutParams()
        lp.apply {
            copyFrom(dialog?.window?.attributes)
            width = w
            height = h
        }
        dialog?.let {
            it.window?.attributes = lp
            it.show()
        }
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, autocompleteFragment, TAG)
        transaction.commit()


        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                holdingActivity.getResultFromDialog(carmenFeature)
                dismiss()
            }

            override fun onCancel() = dismiss()

        })
    }
}