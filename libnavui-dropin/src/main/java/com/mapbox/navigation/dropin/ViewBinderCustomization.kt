package com.mapbox.navigation.dropin

import com.mapbox.navigation.dropin.binder.UIBinder

/**
 * DropInNavigationView customizations.
 */
class ViewBinderCustomization {

    /**
     * Customize the speed limit view by providing your own [UIBinder].
     * Use [UIBinder.USE_DEFAULT] to reset to default.
     */
    var speedLimit: UIBinder? = null

    /**
     * Customize the maneuvers view by providing your own [UIBinder].
     * Use [UIBinder.USE_DEFAULT] to reset to default.
     */
    var maneuver: UIBinder? = null

    /**
     * Customize the road name view by providing your own [UIBinder].
     * Use [UIBinder.USE_DEFAULT] to reset to default.
     */
    var roadName: UIBinder? = null

    /**
     * Customize the Info Panel Trip Progress by providing your own [UIBinder].
     * Use [UIBinder.USE_DEFAULT] to reset to default.
     */
    var infoPanelTripProgressBinder: UIBinder? = null

    /**
     * Customize the Info Panel Header by providing your own [UIBinder].
     * Use [UIBinder.USE_DEFAULT] to reset to default.
     */
    var infoPanelHeaderBinder: UIBinder? = null

    /**
     * Customize the Info Panel Content by providing your own [UIBinder].
     * Use [UIBinder.USE_DEFAULT] to reset to default.
     */
    var infoPanelContentBinder: UIBinder? = null

    /**
     * Customize the Action Buttons by providing your own [UIBinder].
     * Use [UIBinder.USE_DEFAULT] to reset to default.
     */
    var actionButtonsBinder: UIBinder? = null
}