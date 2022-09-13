package com.jk.mr.duo.clock.utils

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.jk.mr.duo.clock.BuildConfig
import com.jk.mr.duo.clock.R
import com.jk.mr.duo.clock.ui.AppWidgetConfigureActivity
import com.jk.mr.duo.clock.utils.Constants.TAG
import com.jk.mr.duo.clock.viewmodels.CalDataViewModel
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener

class SearchFragmentDialog : DialogFragment() {

    val holdingActivity: AppWidgetConfigureActivity by lazy {
        activity as AppWidgetConfigureActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.search_layout, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val placeOptions = PlaceOptions.builder()
            .toolbarColor(ContextCompat.getColor(activity as Context, android.R.color.white))
            //      .backgroundColor(color)
            .build(PlaceOptions.MODE_FULLSCREEN)
        val autocompleteFragment: PlaceAutocompleteFragment =
            PlaceAutocompleteFragment.newInstance(BuildConfig.MAPBOX_ACCESS_TOKEN, placeOptions)
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, autocompleteFragment, TAG)
        transaction.commit()

        autocompleteFragment.setOnPlaceSelectedListener(
            object : PlaceSelectionListener {
                override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                    holdingActivity.getResultFromDialog(carmenFeature)
                    dismiss()
                }

                override fun onCancel() = dismiss()
            }
        )
    }
}
