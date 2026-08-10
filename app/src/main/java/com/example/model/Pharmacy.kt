package com.example.model

data class Pharmacy(
    val id: String,
    val name: String,
    val address: String,
    val latitude: Double = 26.8467,
    val longitude: Double = 80.9462,
    val distanceKm: Double = 0.0,
    val phone: String,
    val openStatus: String, // e.g. "Open now", "24/7", "Closes at 10 PM"
    val timing: String,
    val isVerified: Boolean = true,
    val facilityType: String, // "Retail Pharmacy", "Jan Aushadhi Kendra", "Govt Hospital Pharmacy"
    val rating: Double = 4.5,
    val lastUpdated: String,
    val availableMedicinesCount: Int = 0,
    val ownerId: String? = "usr_2" // Link to registered pharmacy owner UID
)

