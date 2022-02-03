package com.mapbox.navigation.qa_test_app.lifecycle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapbox.maps.CameraOptions
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.qa_test_app.lifecycle.DropInCameraMode

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class DropInNavigationCameraViewModel : ViewModel() {
    val cameraMode = MutableLiveData(defaultCameraMode)
    val cameraOptions = MutableLiveData(defaultCameraOptions)
    var triggerIdleCameraOnMoveListener: Boolean = true

    fun cameraMode() = cameraMode.value ?: defaultCameraMode

    fun isDefaultCameraMode(): Boolean = cameraMode() == defaultCameraMode

    companion object {
        val defaultCameraMode: DropInCameraMode = DropInCameraMode.FOLLOWING

        private const val DEFAULT_INITIAL_ZOOM = 15.0
        private val defaultCameraOptions = CameraOptions.Builder()
            .zoom(DEFAULT_INITIAL_ZOOM)
    }
}
