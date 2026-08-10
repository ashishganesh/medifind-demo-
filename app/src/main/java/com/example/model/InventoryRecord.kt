package com.example.model

data class InventoryRecord(
    val id: String,
    val pharmacyId: String,
    val medicineId: String,
    val stockCount: Int,
    val unitPriceRupees: Double,
    val status: AvailabilityStatus,
    val lastUpdated: String
)
