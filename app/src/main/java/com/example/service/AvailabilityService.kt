package com.example.service

import com.example.model.AvailabilityStatus
import com.example.model.Medicine
import com.example.model.Pharmacy
import com.example.utils.DistanceUtils
import com.example.viewmodel.PharmacySearchResult

class AvailabilityService(
    private val medicineService: MedicineService,
    private val pharmacyService: PharmacyService,
    private val inventoryService: InventoryService
) {

    fun searchAvailability(
        query: String,
        userLat: Double = 26.8526,
        userLon: Double = 80.9927,
        category: String = "All",
        facilityType: String = "All",
        maxDistanceKm: Double = 5.0,
        availabilityFilter: String = "All",
        sortBy: String = "Nearest" // "Nearest", "Availability", "Recently Updated"
    ): List<PharmacySearchResult> {
        val q = query.trim().lowercase()
        val matchedMeds = medicineService.getAllMedicines().filter { med ->
            val matchesCat = category == "All" || med.category.equals(category, ignoreCase = true)
            val matchesQ = q.isEmpty() ||
                    med.name.lowercase().contains(q) ||
                    med.genericName.lowercase().contains(q) ||
                    med.category.lowercase().contains(q)
            matchesCat && matchesQ
        }
        val matchedMedIds = matchedMeds.map { it.id }.toSet()

        // Calculate distance from user location for all registered pharmacies
        val allPharmacies = pharmacyService.pharmacies.value
        val pharmaciesWithCalculatedDistance = allPharmacies.map { pharmacy ->
            val calculatedDist = DistanceUtils.calculateDistanceKm(
                userLat, userLon,
                pharmacy.latitude, pharmacy.longitude
            )
            pharmacy.copy(distanceKm = calculatedDist)
        }

        // Filter pharmacies by facility type and distance radius
        val eligiblePharmacies = pharmaciesWithCalculatedDistance.filter { pharmacy ->
            val matchesFacility = facilityType == "All" || pharmacy.facilityType.equals(facilityType, ignoreCase = true)
            val matchesRadius = maxDistanceKm <= 0.0 || maxDistanceKm >= 999.0 || pharmacy.distanceKm <= maxDistanceKm
            matchesFacility && matchesRadius
        }

        val allInventory = inventoryService.inventory.value
        val allMedicines = medicineService.getAllMedicines()

        val results = mutableListOf<PharmacySearchResult>()

        for (pharmacy in eligiblePharmacies) {
            val pharmacyRecords = allInventory.filter { record ->
                record.pharmacyId == pharmacy.id &&
                        (matchedMedIds.contains(record.medicineId) || matchedMedIds.isEmpty())
            }

            if (pharmacyRecords.isNotEmpty()) {
                val bestRecord = pharmacyRecords.first()
                val medicine = allMedicines.find { it.id == bestRecord.medicineId } ?: continue

                val matchesAvailability = when (availabilityFilter) {
                    "Available" -> bestRecord.status == AvailabilityStatus.AVAILABLE
                    "Low Stock" -> bestRecord.status == AvailabilityStatus.LOW_STOCK
                    "Out of Stock" -> bestRecord.status == AvailabilityStatus.OUT_OF_STOCK
                    else -> true
                }

                if (matchesAvailability) {
                    results.add(
                        PharmacySearchResult(
                            pharmacy = pharmacy,
                            inventory = bestRecord,
                            medicine = medicine
                        )
                    )
                }
            }
        }

        // Sorting
        return when (sortBy) {
            "Availability" -> {
                // Priority: AVAILABLE -> LOW_STOCK -> OUT_OF_STOCK, then nearest distance
                results.sortedWith(
                    compareBy<PharmacySearchResult> { res ->
                        when (res.inventory.status) {
                            AvailabilityStatus.AVAILABLE -> 0
                            AvailabilityStatus.LOW_STOCK -> 1
                            AvailabilityStatus.OUT_OF_STOCK -> 2
                        }
                    }.thenBy { res -> res.pharmacy.distanceKm }
                )
            }
            "Recently Updated" -> {
                results.sortedBy { res -> res.pharmacy.lastUpdated }
            }
            else -> { // Default: "Nearest"
                results.sortedBy { res -> res.pharmacy.distanceKm }
            }
        }
    }
}
