package com.mapbox.navigation.qa_test_app.lifecycle

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.locationcomponent.LocationProvider
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.qa_test_app.R
import com.mapbox.navigation.ui.base.lifecycle.UIComponent

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class DropInLocationPuck(
    private val mapView: MapView,
    private val locationProvider: LocationProvider
) : UIComponent() {

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        super.onAttached(mapboxNavigation)
        mapView.getMapboxMap().getStyle {
            mapView.location.apply {
                setLocationProvider(locationProvider)
                enabled = true
                locationPuck = LocationPuck2D(
                    bearingImage = ContextCompat.getDrawable(
                        mapView.context,
                        R.drawable.mapbox_navigation_puck_icon
                    )
                )
            }
        }
    }
}
