package com.example.data

import com.example.model.AvailabilityLog
import com.example.model.AvailabilityStatus
import com.example.model.HistoricalStockPoint

object DemoStockHistory {

    /**
     * Deterministic, realistic historical log events for demo pharmacies.
     * Engineered to demonstrate:
     * 1. Critical Risk (~1.8 days remaining) - ORS Electrolyte
     * 2. High Risk (~2.1 days remaining, Variable Demand) - Amoxicillin 500mg
     * 3. Medium Risk (~7 days remaining) - Paracetamol 500mg
     * 4. Low Risk (~80 days remaining, with restock event) - Insulin Glargine
     * 5. Insufficient Data (< 7 days history) - Azithromycin 500mg
     * 6. Zero Stock (0 units remaining) - Dolo 650mg
     * 7. Stagnant Demand (0 consumption) - Cetirizine 10mg
     */
    val demoLogs: List<AvailabilityLog> = listOf(
        // --- 1. ORS Electrolyte (inv_5, pharm_1, med_5) -> CRITICAL RISK (~1.8 days) ---
        AvailabilityLog("log_ors_1", "inv_5", "pharm_1", "med_5", 38, 33, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "7 days ago"),
        AvailabilityLog("log_ors_2", "inv_5", "pharm_1", "med_5", 33, 28, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "6 days ago"),
        AvailabilityLog("log_ors_3", "inv_5", "pharm_1", "med_5", 28, 23, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "5 days ago"),
        AvailabilityLog("log_ors_4", "inv_5", "pharm_1", "med_5", 23, 18, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "4 days ago"),
        AvailabilityLog("log_ors_5", "inv_5", "pharm_1", "med_5", 18, 13, AvailabilityStatus.AVAILABLE, AvailabilityStatus.LOW_STOCK, "usr_2", "3 days ago"),
        AvailabilityLog("log_ors_6", "inv_5", "pharm_1", "med_5", 13, 8, AvailabilityStatus.LOW_STOCK, AvailabilityStatus.LOW_STOCK, "usr_2", "1 day ago"),

        // --- 2. Amoxicillin 500mg (inv_2, pharm_1, med_2) -> HIGH RISK & VARIABLE DEMAND ---
        AvailabilityLog("log_amx_1", "inv_2", "pharm_1", "med_2", 65, 63, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "7 days ago"),
        AvailabilityLog("log_amx_2", "inv_2", "pharm_1", "med_2", 63, 43, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "6 days ago"),
        AvailabilityLog("log_amx_3", "inv_2", "pharm_1", "med_2", 43, 40, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "5 days ago"),
        AvailabilityLog("log_amx_4", "inv_2", "pharm_1", "med_2", 40, 22, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "4 days ago"),
        AvailabilityLog("log_amx_5", "inv_2", "pharm_1", "med_2", 22, 21, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "3 days ago"),
        AvailabilityLog("log_amx_6", "inv_2", "pharm_1", "med_2", 21, 15, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "1 day ago"),

        // --- 3. Paracetamol 500mg (inv_1, pharm_1, med_1) -> MEDIUM RISK (~7 days) ---
        AvailabilityLog("log_pcm_1", "inv_1", "pharm_1", "med_1", 84, 77, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "7 days ago"),
        AvailabilityLog("log_pcm_2", "inv_1", "pharm_1", "med_1", 77, 70, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "6 days ago"),
        AvailabilityLog("log_pcm_3", "inv_1", "pharm_1", "med_1", 70, 63, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "5 days ago"),
        AvailabilityLog("log_pcm_4", "inv_1", "pharm_1", "med_1", 63, 56, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "4 days ago"),
        AvailabilityLog("log_pcm_5", "inv_1", "pharm_1", "med_1", 56, 49, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "3 days ago"),
        AvailabilityLog("log_pcm_6", "inv_1", "pharm_1", "med_1", 49, 42, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "1 day ago"),

        // --- 4. Insulin Glargine (inv_3, pharm_1, med_3) -> LOW RISK + RESTOCK EVENT ---
        AvailabilityLog("log_ins_1", "inv_3", "pharm_1", "med_3", 100, 96, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "14 days ago"),
        AvailabilityLog("log_ins_2", "inv_3", "pharm_1", "med_3", 96, 92, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "12 days ago"),
        AvailabilityLog("log_ins_3", "inv_3", "pharm_1", "med_3", 92, 88, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "10 days ago"),
        AvailabilityLog("log_ins_4", "inv_3", "pharm_1", "med_3", 88, 150, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "8 days ago"), // RESTOCK EVENT (+62)
        AvailabilityLog("log_ins_5", "inv_3", "pharm_1", "med_3", 150, 146, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "6 days ago"),
        AvailabilityLog("log_ins_6", "inv_3", "pharm_1", "med_3", 146, 142, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "4 days ago"),
        AvailabilityLog("log_ins_7", "inv_3", "pharm_1", "med_3", 142, 138, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "2 days ago"),

        // --- 5. Azithromycin 500mg (inv_7, pharm_1, med_7) -> INSUFFICIENT DATA (<7 days) ---
        AvailabilityLog("log_azi_1", "inv_7", "pharm_1", "med_7", 28, 25, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "2 days ago"),

        // --- 6. Dolo 650mg (inv_8, pharm_1, med_8) -> OUT OF STOCK (0 units) ---
        AvailabilityLog("log_dolo_1", "inv_8", "pharm_1", "med_8", 12, 0, AvailabilityStatus.LOW_STOCK, AvailabilityStatus.OUT_OF_STOCK, "usr_2", "1 day ago"),

        // --- 7. Cetirizine 10mg (inv_9, pharm_1, med_9) -> ZERO DEMAND (50 units) ---
        AvailabilityLog("log_cet_1", "inv_9", "pharm_1", "med_9", 50, 50, AvailabilityStatus.AVAILABLE, AvailabilityStatus.AVAILABLE, "usr_2", "7 days ago")
    )

    /**
     * Map of inventoryId to historical trend points for visual chart rendering.
     */
    fun getHistoricalPointsForInventory(inventoryId: String): List<HistoricalStockPoint> {
        return when (inventoryId) {
            "inv_1" -> listOf(
                HistoricalStockPoint("Aug 03", 84),
                HistoricalStockPoint("Aug 04", 77),
                HistoricalStockPoint("Aug 05", 70),
                HistoricalStockPoint("Aug 06", 63),
                HistoricalStockPoint("Aug 07", 56),
                HistoricalStockPoint("Aug 08", 49),
                HistoricalStockPoint("Aug 09", 42),
                HistoricalStockPoint("Aug 16 (Est)", 0, isForecast = true)
            )
            "inv_2" -> listOf(
                HistoricalStockPoint("Aug 03", 65),
                HistoricalStockPoint("Aug 04", 63),
                HistoricalStockPoint("Aug 05", 43),
                HistoricalStockPoint("Aug 06", 40),
                HistoricalStockPoint("Aug 07", 22),
                HistoricalStockPoint("Aug 08", 21),
                HistoricalStockPoint("Aug 09", 15),
                HistoricalStockPoint("Aug 11 (Est)", 0, isForecast = true)
            )
            "inv_3" -> listOf(
                HistoricalStockPoint("Jul 27", 100),
                HistoricalStockPoint("Jul 29", 96),
                HistoricalStockPoint("Jul 31", 92),
                HistoricalStockPoint("Aug 02", 88),
                HistoricalStockPoint("Aug 02 (Restock)", 150),
                HistoricalStockPoint("Aug 04", 146),
                HistoricalStockPoint("Aug 06", 142),
                HistoricalStockPoint("Aug 08", 138),
                HistoricalStockPoint("Oct 28 (Est)", 0, isForecast = true)
            )
            "inv_5" -> listOf(
                HistoricalStockPoint("Aug 03", 38),
                HistoricalStockPoint("Aug 04", 33),
                HistoricalStockPoint("Aug 05", 28),
                HistoricalStockPoint("Aug 06", 23),
                HistoricalStockPoint("Aug 07", 18),
                HistoricalStockPoint("Aug 08", 13),
                HistoricalStockPoint("Aug 09", 8),
                HistoricalStockPoint("Aug 11 (Est)", 0, isForecast = true)
            )
            "inv_7" -> listOf(
                HistoricalStockPoint("Aug 07", 28),
                HistoricalStockPoint("Aug 09", 25)
            )
            "inv_8" -> listOf(
                HistoricalStockPoint("Aug 03", 25),
                HistoricalStockPoint("Aug 06", 12),
                HistoricalStockPoint("Aug 09", 0)
            )
            "inv_9" -> listOf(
                HistoricalStockPoint("Aug 03", 50),
                HistoricalStockPoint("Aug 06", 50),
                HistoricalStockPoint("Aug 09", 50)
            )
            else -> listOf(
                HistoricalStockPoint("7d ago", 50),
                HistoricalStockPoint("Today", 40)
            )
        }
    }
}
