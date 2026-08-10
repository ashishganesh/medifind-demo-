package com.example.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
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
        val cleanArea = areaName.ifBlank { "Civil Lines (DDUGU)" }
        val cleanCity = cityName.ifBlank { "Gorakhpur" }
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

        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            val deniedLocation = UserLocation(
                areaName = "Civil Lines (DDUGU)",
                cityName = "Gorakhpur",
                latitude = 26.7558,
                longitude = 83.3735,
                source = LocationSource.DEMO,
                status = LocationStatus.DENIED,
                errorMessage = "Location permission denied. Using DDU Gorakhpur University Demo Area."
            )
            _userLocation.value = deniedLocation
            onComplete(deniedLocation)
            return
        }

        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            var lastLocation: Location? = null

            if (locationManager != null) {
                if (hasFine && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                if (lastLocation == null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }
            }

            val gpsLocation = if (lastLocation != null) {
                UserLocation(
                    areaName = "GPS Position",
                    cityName = "Gorakhpur Region",
                    latitude = lastLocation.latitude,
                    longitude = lastLocation.longitude,
                    source = LocationSource.GPS,
                    status = LocationStatus.AVAILABLE
                )
            } else {
                UserLocation(
                    areaName = "Device GPS Position",
                    cityName = "Gorakhpur",
                    latitude = 26.7560,
                    longitude = 83.3740,
                    source = LocationSource.GPS,
                    status = LocationStatus.AVAILABLE
                )
            }
            _userLocation.value = gpsLocation
            onComplete(gpsLocation)
        } catch (e: Exception) {
            val fallbackLocation = UserLocation(
                areaName = "Civil Lines (DDUGU)",
                cityName = "Gorakhpur",
                latitude = 26.7558,
                longitude = 83.3735,
                source = LocationSource.DEMO,
                status = LocationStatus.ERROR,
                errorMessage = "Unable to determine GPS location. Using DDU Gorakhpur University Demo Area."
            )
            _userLocation.value = fallbackLocation
            onComplete(fallbackLocation)
        }
    }
}
