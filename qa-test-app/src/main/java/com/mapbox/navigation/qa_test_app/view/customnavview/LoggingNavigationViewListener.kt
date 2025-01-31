package com.mapbox.navigation.qa_test_app.view.customnavview

import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.EdgeInsets
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.dropin.navigationview.NavigationViewListener
import com.mapbox.navigation.ui.speedlimit.model.SpeedInfoValue
import com.mapbox.navigation.utils.internal.logD

class LoggingNavigationViewListener(
    private val tag: String = "LoggingNavigationViewListener"
) : NavigationViewListener() {

    override fun onDestinationChanged(destination: Point?) {
        log("listener onDestinationChanged = $destination")
    }

    override fun onFreeDrive() {
        log("listener onFreeDrive")
    }

    override fun onDestinationPreview() {
        log("listener onDestinationPreview")
    }

    override fun onRoutePreview() {
        log("listener onRoutePreview")
    }

    override fun onActiveNavigation() {
        log("listener onActiveNavigation")
    }

    override fun onArrival() {
        log("listener onArrival")
    }

    override fun onIdleCameraMode() {
        log("listener onIdleCameraMode")
    }

    override fun onOverviewCameraMode() {
        log("listener onOverviewCameraMode")
    }

    override fun onFollowingCameraMode() {
        log("listener onFollowingCameraMode")
    }

    override fun onCameraPaddingChanged(padding: EdgeInsets) {
        log("listener onCameraPaddingChanged = $padding")
    }

    override fun onAudioGuidanceStateChanged(muted: Boolean) {
        log("listener onAudioGuidanceStateChanged muted = $muted")
    }

    override fun onRouteFetching(requestId: Long) {
        log("listener onRouteFetching requestId = $requestId")
    }

    override fun onRouteFetchFailed(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
        log("listener onRouteFetchFailed reasons = $reasons -- routeOptions = $routeOptions")
    }

    override fun onRouteFetchCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
        log(
            "listener onRouteFetchCanceled routeOptions = " +
                "$routeOptions -- routerOrigin = $routerOrigin"
        )
    }

    override fun onRouteFetchSuccessful(routes: List<NavigationRoute>) {
        log("listener onRouteFetchSuccessful routes = $routes")
    }

    override fun onInfoPanelHidden() {
        log("listener onInfoPanelHidden")
    }

    override fun onInfoPanelExpanded() {
        log("listener onInfoPanelExpanded")
    }

    override fun onInfoPanelCollapsed() {
        log("listener onInfoPanelCollapsed")
    }

    override fun onInfoPanelDragging() {
        log("listener onInfoPanelDragging")
    }

    override fun onInfoPanelSettling() {
        log("listener onInfoPanelSettling")
    }

    override fun onManeuverCollapsed() {
        log("listener onManeuverCollapsed")
    }

    override fun onManeuverExpanded() {
        log("listener onManeuverExpanded")
    }

    override fun onMapClicked(point: Point) {
        log("listener onMapClicked point = $point")
    }

    override fun onSpeedInfoClicked(speedInfo: SpeedInfoValue?) {
        log("listener onSpeedInfoClicked speedInfo = $speedInfo")
    }

    private fun log(message: String) {
        logD(message, tag)
    }
}
