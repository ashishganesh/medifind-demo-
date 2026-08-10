package com.example.model

enum class StockRiskLevel(
    val label: String,
    val icon: String,
    val hexColor: Long
) {
    CRITICAL("Critical Risk (0–2 days)", "🔴", 0xFFDC2626),
    HIGH("High Risk (3–5 days)", "🟠", 0xFFEA580C),
    MEDIUM("Medium Risk (6–10 days)", "🟡", 0xFFD97706),
    LOW("Low Risk (>10 days)", "🟢", 0xFF16A34A),
    UNKNOWN("Insufficient History", "⚪", 0xFF64748B)
}

enum class ForecastReliability(
    val label: String
) {
    LOW("Low Reliability (<7d data)"),
    MEDIUM("Moderate Reliability (7–14d)"),
    HIGH("Higher Reliability (14d+)")
}

enum class DemandPattern(
    val label: String
) {
    STABLE("Stable Demand"),
    VARIABLE("Variable Demand"),
    ZERO_DEMAND("Stagnant / No Demand")
}

data class HistoricalStockPoint(
    val dateLabel: String,
    val stockLevel: Int,
    val isForecast: Boolean = false
)

data class StockPrediction(
    val inventoryId: String,
    val pharmacyId: String,
    val medicineId: String,
    val medicineName: String,
    val genericName: String,
    val category: String,
    val currentStock: Int,
    val averageDailyConsumption: Double,
    val estimatedDaysRemaining: Double?,
    val estimatedStockOutDate: String,
    val riskLevel: StockRiskLevel,
    val forecastReliability: ForecastReliability,
    val demandPattern: DemandPattern,
    val observationDays: Int,
    val totalConsumptionUnits: Int,
    val restockEventsCount: Int,
    val calculatedAt: String = "Just now",
    val recommendation: String,
    val variabilityWarning: String? = null,
    val historicalPoints: List<HistoricalStockPoint> = emptyList()
)
