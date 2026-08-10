package com.example.data

import com.example.model.DemoLocationOption

object DemoLocations {
    val options = listOf(
        DemoLocationOption(
            id = "demo_lucknow_gomti",
            label = "Lucknow — Gomti Nagar",
            areaName = "Gomti Nagar",
            cityName = "Lucknow",
            latitude = 26.8526,
            longitude = 80.9927
        ),
        DemoLocationOption(
            id = "demo_lucknow_hazratganj",
            label = "Lucknow — Hazratganj (City Centre)",
            areaName = "Hazratganj",
            cityName = "Lucknow",
            latitude = 26.8467,
            longitude = 80.9462
        ),
        DemoLocationOption(
            id = "demo_lucknow_aliganj",
            label = "Lucknow — Aliganj",
            areaName = "Aliganj",
            cityName = "Lucknow",
            latitude = 26.8920,
            longitude = 80.9442
        ),
        DemoLocationOption(
            id = "demo_lucknow_chowk",
            label = "Lucknow — Chowk Old City",
            areaName = "Chowk",
            cityName = "Lucknow",
            latitude = 26.8682,
            longitude = 80.9129
        ),
        DemoLocationOption(
            id = "demo_gorakhpur_centre",
            label = "Gorakhpur — City Centre",
            areaName = "Golghar",
            cityName = "Gorakhpur",
            latitude = 26.7606,
            longitude = 83.3732
        ),
        DemoLocationOption(
            id = "demo_varanasi_cantt",
            label = "Varanasi — Cantt Railway Station",
            areaName = "Cantt",
            cityName = "Varanasi",
            latitude = 25.3217,
            longitude = 82.9873
        )
    )

    val defaultLocation = options[0] // Gomti Nagar, Lucknow
}
