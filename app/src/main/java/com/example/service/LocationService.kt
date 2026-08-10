package com.example.service

import android.content.Context
import com.example.data.DemoLocations
import com.example.model.DemoLocationOption
import com.example.model.LocationSource
import com.example.model.LocationStatus
import com.example.model.UserLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationService {

    private val _userLocation = MutableStateFlow(
        UserLocation(
            areaName = DemoLocations.defaultLocation.areaName,
            cityName = DemoLocations.defaultLocation.cityName,
            latitude = DemoLocations.defaultLocation.latitude,
            longitude = DemoLocations.defaultLocation.longitude,
            source = LocationSource.DEMO,
            status = LocationStatus.AVAILABLE
        )
    )
    val userLocation: StateFlow<UserLocation> = _userLocation.asStateFlow()

    fun setDemoLocation(option: DemoLocationOption) {
        _userLocation.value = UserLocation(
            areaName = option.areaName,
            cityName = option.cityName,
            latitude = option.latitude,
            longitude = option.longitude,
            source = LocationSource.DEMO,
            status = LocationStatus.AVAILABLE
        )
    }

    fun setManualLocation(areaName: String, cityName: String, latitude: Double, longitude: Double) {
        val cleanArea = areaName.ifBlank { "Central" }
        val cleanCity = cityName.ifBlank { "Lucknow" }
        _userLocation.value = UserLocation(
            areaName = cleanArea,
            cityName = cleanCity,
            latitude = latitude,
            longitude = longitude,
            source = LocationSource.MANUAL,
            status = LocationStatus.AVAILABLE
        )
    }

    fun requestGpsLocation(context: Context, onComplete: (UserLocation) -> Unit = {}) {
        _userLocation.value = _userLocation.value.copy(
            status = LocationStatus.REQUESTING
        )

        try {
            // For browser/emulator environment fallback or simulated device GPS fix
            val simulatedGpsLocation = UserLocation(
                areaName = "Current Device Location",
                cityName = "Lucknow",
                latitude = 26.8500,
                longitude = 80.9500,
                source = LocationSource.GPS,
                status = LocationStatus.AVAILABLE
            )
            _userLocation.value = simulatedGpsLocation
            onComplete(simulatedGpsLocation)
        } catch (e: Exception) {
            val fallbackLocation = _userLocation.value.copy(
                status = LocationStatus.DENIED,
                errorMessage = "GPS access unavailable. Falling back to selected location."
            )
            _userLocation.value = fallbackLocation
            onComplete(fallbackLocation)
        }
    }
}
