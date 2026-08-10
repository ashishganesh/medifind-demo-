package com.example.model

enum class LocationSource(val label: String) {
    GPS("Browser/Device GPS"),
    MANUAL("Manual Selection"),
    DEMO("SIH Demo Preset")
}

enum class LocationStatus {
    IDLE,
    REQUESTING,
    AVAILABLE,
    DENIED,
    ERROR
}

data class UserLocation(
    val areaName: String = "Civil Lines (DDUGU)",
    val cityName: String = "Gorakhpur",
    val latitude: Double = 26.7558,
    val longitude: Double = 83.3735,
    val source: LocationSource = LocationSource.DEMO,
    val status: LocationStatus = LocationStatus.AVAILABLE,
    val errorMessage: String? = null
) {
    val displayLabel: String
        get() = "$areaName, $cityName"

    val fullStatusLabel: String
        get() = "$areaName, $cityName (${source.label})"
}

data class DemoLocationOption(
    val id: String,
    val label: String,
    val areaName: String,
    val cityName: String,
    val latitude: Double,
    val longitude: Double
)
