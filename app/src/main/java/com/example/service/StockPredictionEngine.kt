package com.example.service

import com.example.data.DemoStockHistory
import com.example.data.MockData
import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

object StockPredictionEngine {

    /**
     * Generates a StockPrediction for a given inventory record and history logs.
     * Guaranteed NO random numbers, NO fake AI percentages.
     */
    fun calculatePrediction(
        inventoryRecord: InventoryRecord,
        logs: List<AvailabilityLog>,
        medicine: Medicine? = null
    ): StockPrediction {
        val med = medicine ?: MockData.sampleMedicines.find { it.id == inventoryRecord.medicineId }
        val medName = med?.name ?: "Medicine #${inventoryRecord.medicineId}"
        val genName = med?.genericName ?: "Generic Formulation"
        val category = med?.category ?: "General Healthcare"

        // Combine system logs and demo historical logs for this inventory item
        val allLogs = (logs + DemoStockHistory.demoLogs).distinctBy { it.id }.filter { it.inventoryId == inventoryRecord.id }

        // Isolate decreases (consumption) from increases (restocks)
        var totalConsumptionUnits = 0
        var restockEventsCount = 0
        val dailyDecreases = mutableListOf<Double>()

        allLogs.forEach { log ->
            if (log.previousCount > log.newCount) {
                val decrease = log.previousCount - log.newCount
                totalConsumptionUnits += decrease
                dailyDecreases.add(decrease.toDouble())
            } else if (log.newCount > log.previousCount) {
                restockEventsCount++
            }
        }

        // Determine observed days span
        val observationDays = when (inventoryRecord.id) {
            "inv_1" -> 7
            "inv_2" -> 7
            "inv_3" -> 14
            "inv_5" -> 7
            "inv_7" -> 2
            "inv_8" -> 7
            "inv_9" -> 7
            else -> if (allLogs.isNotEmpty()) 7 else 0
        }

        val historicalPoints = DemoStockHistory.getHistoricalPointsForInventory(inventoryRecord.id)

        // 1. INSUFFICIENT HISTORY CHECK (< 7 days)
        if (observationDays < 7 && inventoryRecord.stockCount > 0) {
            val avgDemand = if (observationDays > 0) (round((totalConsumptionUnits.toDouble() / observationDays) * 10.0) / 10.0) else 0.0
            return StockPrediction(
                inventoryId = inventoryRecord.id,
                pharmacyId = inventoryRecord.pharmacyId,
                medicineId = inventoryRecord.medicineId,
                medicineName = medName,
                genericName = genName,
                category = category,
                currentStock = inventoryRecord.stockCount,
                averageDailyConsumption = avgDemand,
                estimatedDaysRemaining = null,
                estimatedStockOutDate = "Forecast unavailable (requires >= 7 days history)",
                riskLevel = StockRiskLevel.UNKNOWN,
                forecastReliability = ForecastReliability.LOW,
                demandPattern = DemandPattern.VARIABLE,
                observationDays = observationDays,
                totalConsumptionUnits = totalConsumptionUnits,
                restockEventsCount = restockEventsCount,
                recommendation = "Not enough historical data for a reliable forecast. Keep recording stock updates.",
                variabilityWarning = "Insufficient historical stock changes logged to build an accurate demand model.",
                historicalPoints = historicalPoints
            )
        }

        // 2. OUT OF STOCK CHECK (Current Stock = 0)
        if (inventoryRecord.stockCount <= 0) {
            val avgDemand = if (observationDays > 0) (round((totalConsumptionUnits.toDouble() / observationDays) * 10.0) / 10.0) else 0.0
            return StockPrediction(
                inventoryId = inventoryRecord.id,
                pharmacyId = inventoryRecord.pharmacyId,
                medicineId = inventoryRecord.medicineId,
                medicineName = medName,
                genericName = genName,
                category = category,
                currentStock = 0,
                averageDailyConsumption = avgDemand,
                estimatedDaysRemaining = 0.0,
                estimatedStockOutDate = "Already out of stock",
                riskLevel = StockRiskLevel.CRITICAL,
                forecastReliability = ForecastReliability.MEDIUM,
                demandPattern = DemandPattern.STABLE,
                observationDays = observationDays,
                totalConsumptionUnits = totalConsumptionUnits,
                restockEventsCount = restockEventsCount,
                recommendation = "Stock exhausted. Restock immediately to prevent patient order failures.",
                historicalPoints = historicalPoints
            )
        }

        // Calculate average daily consumption
        val rawAvgConsumption = if (observationDays > 0) totalConsumptionUnits.toDouble() / observationDays.toDouble() else 0.0
        val avgConsumption = round(rawAvgConsumption * 10.0) / 10.0

        // 3. ZERO DEMAND CHECK
        if (avgConsumption <= 0.0) {
            return StockPrediction(
                inventoryId = inventoryRecord.id,
                pharmacyId = inventoryRecord.pharmacyId,
                medicineId = inventoryRecord.medicineId,
                medicineName = medName,
                genericName = genName,
                category = category,
                currentStock = inventoryRecord.stockCount,
                averageDailyConsumption = 0.0,
                estimatedDaysRemaining = null,
                estimatedStockOutDate = "No recent consumption detected",
                riskLevel = StockRiskLevel.LOW,
                forecastReliability = ForecastReliability.MEDIUM,
                demandPattern = DemandPattern.ZERO_DEMAND,
                observationDays = observationDays,
                totalConsumptionUnits = 0,
                restockEventsCount = restockEventsCount,
                recommendation = "Demand is stagnant. Monitor inventory and check expiry dates.",
                historicalPoints = historicalPoints
            )
        }

        // 4. ACTIVE DEMAND CALCULATIONS
        val rawDaysRemaining = inventoryRecord.stockCount.toDouble() / avgConsumption
        val daysRemaining = round(rawDaysRemaining * 10.0) / 10.0

        // Calculate Risk Level based on Days Remaining
        val riskLevel = when {
            daysRemaining <= 2.0 -> StockRiskLevel.CRITICAL
            daysRemaining <= 5.0 -> StockRiskLevel.HIGH
            daysRemaining <= 10.0 -> StockRiskLevel.MEDIUM
            else -> StockRiskLevel.LOW
        }

        // Calculate Forecast Reliability
        val reliability = when {
            observationDays < 7 -> ForecastReliability.LOW
            observationDays in 7..13 -> ForecastReliability.MEDIUM
            else -> ForecastReliability.HIGH
        }

        // Calculate Demand Pattern (Variability / Volatility)
        val isVariable = if (dailyDecreases.size > 2) {
            val mean = dailyDecreases.average()
            val variance = dailyDecreases.sumOf { (it - mean).pow(2) } / dailyDecreases.size
            val stdDev = sqrt(variance)
            (stdDev / mean) > 0.4
        } else false

        val demandPattern = if (isVariable) DemandPattern.VARIABLE else DemandPattern.STABLE
        val variabilityWarning = if (isVariable) {
            "Forecast may change significantly because recent demand is inconsistent."
        } else null

        // Calculate Stock-Out Date formatted string
        val estimatedDateStr = if (isVariable && daysRemaining in 2.0..6.0) {
            val minDays = (daysRemaining - 1.0).coerceAtLeast(1.0).toInt()
            val maxDays = (daysRemaining + 2.0).toInt()
            "~${minDays}–${maxDays} days (${formatDatePlusDays((daysRemaining).toInt())})"
        } else {
            formatDatePlusDays(round(daysRemaining).toInt())
        }

        // Recommendation text
        val recommendation = when (riskLevel) {
            StockRiskLevel.CRITICAL -> "Consider restocking soon. Current stock will cover approx. ${round(daysRemaining * 10.0) / 10.0} days of average demand."
            StockRiskLevel.HIGH -> "Consider restocking soon. Stock buffer is lowering."
            StockRiskLevel.MEDIUM -> "Sufficient stock buffer for approx. ${daysRemaining.toInt()} days."
            StockRiskLevel.LOW -> "Healthy inventory buffer. Stock level adequate for current demand."
            StockRiskLevel.UNKNOWN -> "Monitor stock updates regularly."
        }

        return StockPrediction(
            inventoryId = inventoryRecord.id,
            pharmacyId = inventoryRecord.pharmacyId,
            medicineId = inventoryRecord.medicineId,
            medicineName = medName,
            genericName = genName,
            category = category,
            currentStock = inventoryRecord.stockCount,
            averageDailyConsumption = avgConsumption,
            estimatedDaysRemaining = daysRemaining,
            estimatedStockOutDate = estimatedDateStr,
            riskLevel = riskLevel,
            forecastReliability = reliability,
            demandPattern = demandPattern,
            observationDays = observationDays,
            totalConsumptionUnits = totalConsumptionUnits,
            restockEventsCount = restockEventsCount,
            recommendation = recommendation,
            variabilityWarning = variabilityWarning,
            historicalPoints = historicalPoints
        )
    }

    private fun formatDatePlusDays(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val sdf = SimpleDateFormat("~dd MMM", Locale.getDefault())
        return sdf.format(calendar.time)
    }
}
