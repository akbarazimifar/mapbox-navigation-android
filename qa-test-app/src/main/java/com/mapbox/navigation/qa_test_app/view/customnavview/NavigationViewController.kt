package com.mapbox.navigation.qa_test_app.view.customnavview

import android.location.Location
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.extensions.applyLanguageAndVoiceUnitOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.internal.extensions.flowNewRawLocation
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.dropin.NavigationView
import com.mapbox.navigation.ui.base.lifecycle.UIComponent
import com.mapbox.navigation.utils.internal.toPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class NavigationViewController(
    lifecycleOwner: LifecycleOwner,
    private val navigationView: NavigationView
) : DefaultLifecycleObserver, UIComponent() {
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    val location = MutableStateFlow<Location?>(null)
    private val mapboxNavigation = MutableStateFlow<MapboxNavigation?>(null)

    override fun onCreate(owner: LifecycleOwner) {
        MapboxNavigationApp.registerObserver(this)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        MapboxNavigationApp.unregisterObserver(this)
    }

    override fun onAttached(mapboxNavigation: MapboxNavigation) {
        super.onAttached(mapboxNavigation)
        this.mapboxNavigation.value = mapboxNavigation
        mapboxNavigation.flowNewRawLocation().observe {
            location.value = it
        }
    }

    override fun onDetached(mapboxNavigation: MapboxNavigation) {
        super.onDetached(mapboxNavigation)
        this.mapboxNavigation.value = null
    }

    suspend fun startActiveGuidance(destination: Point) {
        val routes = fetchRoute(destination)
        navigationView.api.startActiveGuidance(routes)
    }

    suspend fun fetchRoute(destination: Point): List<NavigationRoute> {
        val origin = location.filterNotNull().first().toPoint()
        val mapboxNavigation = this.mapboxNavigation.filterNotNull().first()
        return mapboxNavigation.fetchRoute(origin, destination)
    }

    private suspend fun MapboxNavigation.fetchRoute(
        origin: Point,
        destination: Point
    ): List<NavigationRoute> = suspendCancellableCoroutine { cont ->
        val routeOptions = RouteOptions.builder()
            .applyDefaultNavigationOptions()
            .applyLanguageAndVoiceUnitOptions(navigationOptions.applicationContext)
            .layersList(listOf(getZLevel(), null))
            .coordinatesList(listOf(origin, destination))
            .alternatives(true)
            .build()
        val requestId = requestRoutes(
            routeOptions,
            object : NavigationRouterCallback {
                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    routerOrigin: RouterOrigin
                ) {
                    cont.resume(routes)
                }

                override fun onFailure(
                    reasons: List<RouterFailure>,
                    routeOptions: RouteOptions
                ) {
                    cont.resumeWithException(FetchRouteError(reasons, routeOptions))
                }

                override fun onCanceled(
                    routeOptions: RouteOptions,
                    routerOrigin: RouterOrigin
                ) = Unit
            }
        )
        cont.invokeOnCancellation { cancelRouteRequest(requestId) }
    }

    private class FetchRouteError(
        val reasons: List<RouterFailure>,
        val routeOptions: RouteOptions
    ) : Error()
}