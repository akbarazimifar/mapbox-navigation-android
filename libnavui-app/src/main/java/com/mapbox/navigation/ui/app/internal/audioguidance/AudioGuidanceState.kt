package com.mapbox.navigation.ui.app.internal.audioguidance

/**
 * Defines the state for audio guidance button.
 * @property isMuted is true if the volume is muted; false otherwise
 */
data class AudioGuidanceState internal constructor(
    val isMuted: Boolean = false
)