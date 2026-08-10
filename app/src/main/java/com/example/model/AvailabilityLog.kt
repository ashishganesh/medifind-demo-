package com.example.model

data class AvailabilityLog(
    val id: String,
    val inventoryId: String,
    val pharmacyId: String,
    val medicineId: String,
    val previousCount: Int,
    val newCount: Int,
    val previousStatus: AvailabilityStatus,
    val newStatus: AvailabilityStatus,
    val updatedByUserId: String,
    val timestamp: String
)
