package com.example.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object NavigationUtils {

    /**
     * Opens native Google Maps or external browser directions URL for a pharmacy destination.
     */
    fun openDirections(context: Context, latitude: Double, longitude: Double, pharmacyName: String = "") {
        val geoUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(pharmacyName)})")
        val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to web browser map navigation link
            val webUrl = DistanceUtils.getDirectionsUrl(latitude, longitude, pharmacyName)
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            try {
                context.startActivity(webIntent)
            } catch (_: Exception) {
                // Ignore if browser is unavailable in non-GUI runner
            }
        }
    }
}
