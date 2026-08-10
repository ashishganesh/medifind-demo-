package com.example.data

import com.example.model.DemoLocationOption

object DemoLocations {
    val options = listOf(
        DemoLocationOption(
            id = "demo_ddugu_university",
            label = "SIH Demo Area — DDU Gorakhpur University",
            areaName = "Civil Lines (DDUGU)",
            cityName = "Gorakhpur",
            latitude = 26.7558,
            longitude = 83.3735
        ),
        DemoLocationOption(
            id = "demo_gorakhpur_golghar",
            label = "Gorakhpur — Golghar Market",
            areaName = "Golghar",
            cityName = "Gorakhpur",
            latitude = 26.7606,
            longitude = 83.3732
        ),
        DemoLocationOption(
            id = "demo_gorakhpur_townhall",
            label = "Gorakhpur — Town Hall & Civil Lines",
            areaName = "Town Hall Road",
            cityName = "Gorakhpur",
            latitude = 26.7630,
            longitude = 83.3690
        ),
        DemoLocationOption(
            id = "demo_gorakhpur_medical",
            label = "Gorakhpur — BRD Medical College",
            areaName = "Medical College Road",
            cityName = "Gorakhpur",
            latitude = 26.7820,
            longitude = 83.3850
        ),
        DemoLocationOption(
            id = "demo_gorakhpur_mohaddipur",
            label = "Gorakhpur — Mohaddipur",
            areaName = "Mohaddipur",
            cityName = "Gorakhpur",
            latitude = 26.7550,
            longitude = 83.3910
        ),
        DemoLocationOption(
            id = "demo_lucknow_hazratganj",
            label = "Lucknow — Hazratganj (City Centre)",
            areaName = "Hazratganj",
            cityName = "Lucknow",
            latitude = 26.8467,
            longitude = 80.9462
        )
    )

    val defaultLocation = options[0] // SIH Demo Area — DDU Gorakhpur University
}

