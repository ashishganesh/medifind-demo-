package com.example.model

enum class UserRole(val title: String, val badge: String) {
    PATIENT("Patient / Citizen", "Public Access"),
    PHARMACY("Pharmacy Portal", "Sharma Medical Store"),
    ADMIN("State Admin", "Health Dept UP")
}
