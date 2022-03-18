package com.mapbox.navigation.dropin.component.infopanel

import androidx.core.view.isVisible
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.dropin.DropInNavigationViewContext
import com.mapbox.navigation.dropin.R
import com.mapbox.navigation.dropin.component.destination.DestinationState
import com.mapbox.navigation.dropin.component.navigationstate.NavigationState
import com.mapbox.navigation.dropin.component.routefetch.RoutesAction
import com.mapbox.navigation.dropin.databinding.MapboxInfoPanelHeaderLayoutBinding
import com.mapbox.navigation.dropin.lifecycle.UIComponent
import kotlinx.coroutines.flow.StateFlow

internal class InfoPanelHeaderComponent(
    private val binding: MapboxInfoPanelHeaderLayoutBinding,
    private val context: DropInNavigationViewContext
) : UIComponent() {

    private val navigationState: StateFlow<NavigationState>
        get() = context.navigationState
    private val destinationState: StateFlow<DestinationState>
        get() = context.destinationState
    private val resources get() = binding.root.resources
    private val dispatch = context.dispatch

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        super.onAttached(mapboxNavigation)

        // views visibility
        navigationState.observe {
            binding.poiName.isVisible = it == NavigationState.FreeDrive
            binding.routePreview.isVisible = it == NavigationState.FreeDrive
            binding.startNavigation.isVisible = it == NavigationState.FreeDrive ||
                it == NavigationState.RoutePreview
            binding.endNavigation.isVisible = it == NavigationState.ActiveNavigation ||
                it == NavigationState.Arrival
            binding.tripProgressLayout.isVisible = it == NavigationState.ActiveNavigation ||
                it == NavigationState.RoutePreview
            binding.arrivedText.isVisible = it == NavigationState.Arrival
        }

        destinationState.observe {
            val placeName = it.destination?.features?.firstOrNull()?.placeName()
            binding.poiName.text =
                placeName ?: resources.getString(R.string.mapbox_drop_in_dropped_pin)
        }

        binding.routePreview.setOnClickListener {
            dispatch(RoutesAction.FetchAndSetRoute)
        }

        binding.startNavigation.setOnClickListener {
            dispatch(RoutesAction.StartNavigation)
        }

        binding.endNavigation.setOnClickListener {
            dispatch(RoutesAction.StopNavigation)
        }
    }
}