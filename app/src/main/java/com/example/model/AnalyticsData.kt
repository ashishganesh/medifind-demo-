package com.example.model

data class SearchFrequency(
    val query: String,
    val count: Int,
    val trendPercent: Double
)

data class DistrictAvailability(
    val districtName: String,
    val totalPharmacies: Int,
    val availabilityPercent: Int,
    val lowStockAlerts: Int
)

data class AnalyticsData(
    val totalPharmaciesTracked: Int,
    val activePharmacies24h: Int,
    val totalMedicinesCataloged: Int,
    val activeShortagesCount: Int,
    val popularSearches: List<SearchFrequency>,
    val districtData: List<DistrictAvailability>
)
