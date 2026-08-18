package com.example.model

enum class UserRole(val title: String, val badge: String, val shortTitle: String) {
    PATIENT("Patient / Citizen", "Public Access", "Public View"),
    PHARMACY("Pharmacy Portal", "Sharma Medical", "Pharmacy View"),
    ADMIN("State Admin", "Health Dept UP", "Admin View")
}
