package com.mapbox.navigation.ui.app.internal.controller

import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.internal.extensions.flowRoutesUpdated
import com.mapbox.navigation.ui.app.internal.Action
import com.mapbox.navigation.ui.app.internal.State
import com.mapbox.navigation.ui.app.internal.Store
import com.mapbox.navigation.ui.app.internal.routefetch.RoutesAction

class RouteStateController(private val store: Store) : StateController() {
    init {
        store.register(this)
    }

    private var mapboxNavigation: MapboxNavigation? = null

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        super.onAttached(mapboxNavigation)
        this.mapboxNavigation = mapboxNavigation
        mapboxNavigation.flowRoutesUpdated().observe { result ->
            store.dispatch(RoutesAction.SynchronizeRoutes(result.navigationRoutes))
        }
    }

    override fun onDetached(mapboxNavigation: MapboxNavigation) {
        super.onDetached(mapboxNavigation)
        this.mapboxNavigation = null
    }

    override fun process(state: State, action: Action): State {
        if (action is RoutesAction) {
            return this.mapboxNavigation?.let {
                return state.copy(
                    routes = processRoutesAction(it, action)
                )
            } ?: state
        }
        return state
    }

    private fun processRoutesAction(
        mapboxNavigation: MapboxNavigation,
        action: RoutesAction
    ): List<NavigationRoute> {
        return when (action) {
            is RoutesAction.SetRoutes -> {
                mapboxNavigation.setNavigationRoutes(action.routes, action.legIndex)
                action.routes
            }
            is RoutesAction.SynchronizeRoutes -> {
                action.routes
            }
        }
    }
}
