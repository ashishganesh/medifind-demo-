package com.example.utils

import java.util.Locale

object DistanceUtils {

    /**
     * Calculates the great-circle distance between two geographic coordinates
     * using the Haversine formula.
     *
     * @return Distance in kilometers. Returns 0.0 if coordinates are invalid.
     */
    fun calculateDistanceKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        // Coordinate validation
        if (lat1 < -90.0 || lat1 > 90.0 || lat2 < -90.0 || lat2 > 90.0 ||
            lon1 < -180.0 || lon1 > 180.0 || lon2 < -180.0 || lon2 > 180.0
        ) {
            return 0.0
        }
        if ((lat1 == 0.0 && lon1 == 0.0) || (lat2 == 0.0 && lon2 == 0.0)) {
            return 0.0
        }

        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a))
        return earthRadiusKm * c
    }

    /**
     * Formats distance in kilometers into human-readable string.
     * - Less than 1 km: formatted in meters (e.g. "650 m")
     * - 1 km or more: formatted in km with 1 decimal place (e.g. "1.4 km")
     */
    fun formatDistance(distanceKm: Double): String {
        if (distanceKm <= 0.0) return "Nearby"
        return if (distanceKm < 1.0) {
            val meters = (distanceKm * 1000).toInt()
            "$meters m"
        } else {
            String.format(Locale.US, "%.1f km", distanceKm)
        }
    }

    /**
     * Generates web Google Maps directions URL for destination coordinates.
     */
    fun getDirectionsUrl(lat: Double, lon: Double, pharmacyName: String = ""): String {
        val nameEncoded = java.net.URLEncoder.encode(pharmacyName, "UTF-8")
        return "https://www.google.com/maps/dir/?api=1&destination=$lat,$lon&destination_place_id=$nameEncoded"
    }
}
