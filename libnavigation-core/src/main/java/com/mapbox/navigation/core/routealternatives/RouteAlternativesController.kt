package com.mapbox.navigation.core.routealternatives

import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.base.common.logger.model.Message
import com.mapbox.base.common.logger.model.Tag
import com.mapbox.navigation.base.internal.utils.parseNativeDirectionsAlternative
import com.mapbox.navigation.base.route.RouteAlternativesOptions
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.trip.NativeRouteProcessingListener
import com.mapbox.navigation.core.trip.session.TripSession
import com.mapbox.navigation.navigator.internal.MapboxNativeNavigator
import com.mapbox.navigation.utils.internal.logD
import com.mapbox.navigation.utils.internal.logE
import com.mapbox.navigation.utils.internal.logI
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CopyOnWriteArraySet

internal class RouteAlternativesController constructor(
    private val options: RouteAlternativesOptions,
    private val navigator: MapboxNativeNavigator,
    private val tripSession: TripSession
) {

    /**
     * Works around the problem of an endless loop of alternatives updates.
     *
     * We deliver new routes to developers in [RouteAlternativesObserver.onRouteAlternatives] and
     * developers set the selected alternatives back with [MapboxNavigation] which in turn triggers
     * [MapboxNativeNavigator.setRoute] and that calls [com.mapbox.navigator.RouteAlternativesObserver]
     * which would lock us in a loop.
     *
     * To break out of this loop, we ignore the first [com.mapbox.navigator.RouteAlternativesObserver]
     * after setting a route.
     */
    private var inhibitNextUpdate = false

    private val nativeRouteAlternativesController = navigator.createRouteAlternativesController()
        .apply {
            setRouteAlternativesOptions(
                com.mapbox.navigator.RouteAlternativesOptions(
                    options.intervalMillis * SECONDS_PER_MILLIS,
                    MIN_TIME_BEFORE_MANEUVER_SECONDS,
                    LOOK_AHEAD_SECONDS
                )
            )
            enableEmptyAlternativesRefresh(true)
        }

    private val observers = CopyOnWriteArraySet<RouteAlternativesObserver>()

    private val nativeRouteProcessingListener = NativeRouteProcessingListener {
        inhibitNextUpdate = true
    }

    init {
        tripSession.registerNativeRouteProcessingListener(nativeRouteProcessingListener)
    }

    fun register(routeAlternativesObserver: RouteAlternativesObserver) {
        val isStopped = observers.isEmpty()
        observers.add(routeAlternativesObserver)
        if (isStopped) {
            inhibitNextUpdate = false
            nativeRouteAlternativesController.addObserver(nativeObserver)
        }
    }

    fun unregister(routeAlternativesObserver: RouteAlternativesObserver) {
        observers.remove(routeAlternativesObserver)
        if (observers.isEmpty()) {
            nativeRouteAlternativesController.removeObserver(nativeObserver)
        }
    }

    fun triggerAlternativeRequest() {
        nativeRouteAlternativesController.refreshImmediately()
    }

    fun unregisterAll() {
        nativeRouteAlternativesController.removeAllObservers()
        observers.clear()
    }

    private val nativeObserver = object : com.mapbox.navigator.RouteAlternativesObserver {
        override fun onRouteAlternativesChanged(
            routeAlternatives: List<com.mapbox.navigator.RouteAlternative>
        ): List<Int> {
            if (inhibitNextUpdate) {
                logD(TAG, Message("update skipped because alternatives were reset with #setRoutes"))
                inhibitNextUpdate = false
                return emptyList()
            }

            val routeProgress = tripSession.getRouteProgress()
                ?: return emptyList()

            // TODO make async
            val alternatives: List<DirectionsRoute> = runBlocking {
                routeAlternatives.mapIndexedNotNull { index, routeAlternative ->
                    val alternative = parseNativeDirectionsAlternative(
                        routeAlternative.routeResponse,
                        routeProgress.route.routeOptions()
                    )
                    if (alternative == null) {
                        logE(TAG, Message("null alternative at index $index"))
                    }
                    alternative
                }
            }
            logI(TAG, Message("${alternatives.size} alternatives available"))

            // Notify the listeners.
            // TODO https://github.com/mapbox/mapbox-navigation-native/issues/4409
            // There is no way to determine if the route was Onboard or Offboard
            observers.forEach {
                it.onRouteAlternatives(routeProgress, alternatives, RouterOrigin.Onboard)
            }

            // This is supposed to be able to filter alternatives
            // but at this point we're not filtering anything.
            return emptyList()
        }

        override fun onError(message: String) {
            logE(TAG, Message("Error: $message"))
        }
    }

    private companion object {
        private val TAG = Tag("MbxRouteAlternativesController")
        private const val MIN_TIME_BEFORE_MANEUVER_SECONDS = 1.0
        private const val LOOK_AHEAD_SECONDS = 1.0
        private const val SECONDS_PER_MILLIS = 0.001
    }
}
