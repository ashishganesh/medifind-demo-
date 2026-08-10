package com.example.service

import com.example.data.DemoAlternatives
import com.example.model.*
import com.example.utils.DistanceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VerifiedAlternativeService {

    private val _alternatives = MutableStateFlow<List<VerifiedAlternative>>(DemoAlternatives.demoAlternatives)
    val alternatives: StateFlow<List<VerifiedAlternative>> = _alternatives.asStateFlow()

    private fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    /**
     * Patient facing lookup: ONLY returns VERIFIED alternative mappings for a source medicine.
     */
    fun getVerifiedAlternatives(sourceMedicineId: String): List<VerifiedAlternative> {
        return _alternatives.value.filter {
            it.sourceMedicineId == sourceMedicineId && it.verificationStatus == VerificationStatus.VERIFIED
        }
    }

    /**
     * Admin lookup: Returns all mappings regardless of status or filtered by status.
     */
    fun getAllAlternatives(): List<VerifiedAlternative> {
        return _alternatives.value
    }

    fun getAlternativesByStatus(status: VerificationStatus): List<VerifiedAlternative> {
        return _alternatives.value.filter { it.verificationStatus == status }
    }

    fun getAlternativeById(id: String): VerifiedAlternative? {
        return _alternatives.value.find { it.id == id }
    }

    /**
     * Resolves verified alternatives for a source medicine along with nearby pharmacy inventory and distances.
     */
    fun getAlternativesWithAvailability(
        sourceMedicineId: String,
        medicines: List<Medicine>,
        inventoryList: List<InventoryRecord>,
        pharmacies: List<Pharmacy>,
        userLat: Double,
        userLng: Double
    ): List<AlternativeWithAvailability> {
        val verifiedMappings = getVerifiedAlternatives(sourceMedicineId)

        return verifiedMappings.mapNotNull { mapping ->
            val altMed = medicines.find { it.id == mapping.alternativeMedicineId } ?: return@mapNotNull null

            // Find matching inventory records for alternative medicine
            val altInventories = inventoryList.filter { it.medicineId == altMed.id }

            // Join with active pharmacies and calculate distance
            val pharmacyStocks = altInventories.mapNotNull { inv ->
                val pharm = pharmacies.find { it.id == inv.pharmacyId } ?: return@mapNotNull null
                val distance = DistanceUtils.calculateDistanceKm(userLat, userLng, pharm.latitude, pharm.longitude)
                Triple(pharm, inv, distance)
            }.sortedWith(
                compareBy<Triple<Pharmacy, InventoryRecord, Double>> {
                    when (it.second.status) {
                        AvailabilityStatus.AVAILABLE -> 0
                        AvailabilityStatus.LOW_STOCK -> 1
                        AvailabilityStatus.OUT_OF_STOCK -> 2
                    }
                }.thenBy { it.third }
            )

            val bestMatch = pharmacyStocks.firstOrNull()
            val totalAvailableStock = altInventories.sumOf { it.stockCount }
            val stockingPharmaciesCount = pharmacyStocks.count { it.second.status != AvailabilityStatus.OUT_OF_STOCK }

            val overallStatus = when {
                pharmacyStocks.any { it.second.status == AvailabilityStatus.AVAILABLE } -> AvailabilityStatus.AVAILABLE
                pharmacyStocks.any { it.second.status == AvailabilityStatus.LOW_STOCK } -> AvailabilityStatus.LOW_STOCK
                else -> AvailabilityStatus.OUT_OF_STOCK
            }

            AlternativeWithAvailability(
                mapping = mapping,
                alternativeMedicine = altMed,
                bestPharmacy = bestMatch?.first,
                bestInventory = bestMatch?.second,
                nearbyStockCount = totalAvailableStock,
                nearbyPharmaciesCount = stockingPharmaciesCount,
                overallStatus = overallStatus,
                minDistanceKm = bestMatch?.third
            )
        }.sortedWith(
            compareBy<AlternativeWithAvailability> {
                when (it.overallStatus) {
                    AvailabilityStatus.AVAILABLE -> 0
                    AvailabilityStatus.LOW_STOCK -> 1
                    AvailabilityStatus.OUT_OF_STOCK -> 2
                }
            }.thenBy { it.minDistanceKm ?: 99999.0 }
        )
    }

    /**
     * Creates a new alternative mapping with validation rules:
     * - Admin role restriction
     * - Self-reference prevention (sourceMedicineId != alternativeMedicineId)
     * - Duplicate relationship prevention
     */
    fun createAlternativeMapping(
        sourceMedicineId: String,
        alternativeMedicineId: String,
        relationshipType: RelationshipType,
        notes: String,
        userRole: UserRole,
        userName: String,
        initialStatus: VerificationStatus = VerificationStatus.VERIFIED
    ): Result<VerifiedAlternative> {
        // Rule 1: Role check
        if (userRole != UserRole.ADMIN) {
            return Result.failure(SecurityException("Access Denied: Only Administrators can create or manage alternative mappings."))
        }

        // Rule 2: Self-reference check
        if (sourceMedicineId.trim() == alternativeMedicineId.trim()) {
            return Result.failure(IllegalArgumentException("Validation Error: Source medicine and alternative medicine cannot be identical."))
        }

        // Rule 3: Duplicate mapping check
        val existingDuplicate = _alternatives.value.find {
            it.sourceMedicineId == sourceMedicineId &&
                    it.alternativeMedicineId == alternativeMedicineId &&
                    it.relationshipType == relationshipType
        }
        if (existingDuplicate != null) {
            return Result.failure(IllegalStateException("Validation Error: A verified alternative mapping between these medicines with relationship '${relationshipType.displayName}' already exists."))
        }

        val today = getCurrentDateString()
        val newMapping = VerifiedAlternative(
            id = "alt_${System.currentTimeMillis()}",
            sourceMedicineId = sourceMedicineId,
            alternativeMedicineId = alternativeMedicineId,
            relationshipType = relationshipType,
            verificationStatus = initialStatus,
            verificationSource = "SIH Admin Console",
            verifiedBy = userName,
            verifiedAt = if (initialStatus == VerificationStatus.VERIFIED) today else null,
            notes = notes,
            createdAt = today,
            updatedAt = today
        )

        _alternatives.value = _alternatives.value + newMapping
        return Result.success(newMapping)
    }

    /**
     * Admin action to verify/approve a mapping
     */
    fun verifyAlternative(
        mappingId: String,
        userRole: UserRole,
        adminName: String
    ): Result<VerifiedAlternative> {
        if (userRole != UserRole.ADMIN) {
            return Result.failure(SecurityException("Access Denied: Only Administrators can approve or verify alternative mappings."))
        }

        val existing = getAlternativeById(mappingId)
            ?: return Result.failure(NoSuchElementException("Mapping ID '$mappingId' not found."))

        val today = getCurrentDateString()
        val updated = existing.copy(
            verificationStatus = VerificationStatus.VERIFIED,
            verifiedBy = adminName,
            verifiedAt = today,
            updatedAt = today
        )

        _alternatives.value = _alternatives.value.map { if (it.id == mappingId) updated else it }
        return Result.success(updated)
    }

    /**
     * Admin action to reject a mapping
     */
    fun rejectAlternative(
        mappingId: String,
        userRole: UserRole,
        adminName: String,
        reason: String
    ): Result<VerifiedAlternative> {
        if (userRole != UserRole.ADMIN) {
            return Result.failure(SecurityException("Access Denied: Only Administrators can reject alternative mappings."))
        }

        val existing = getAlternativeById(mappingId)
            ?: return Result.failure(NoSuchElementException("Mapping ID '$mappingId' not found."))

        val today = getCurrentDateString()
        val updatedNotes = if (existing.notes.isNotBlank()) "${existing.notes} | Rejected reason: $reason" else "Rejected reason: $reason"
        val updated = existing.copy(
            verificationStatus = VerificationStatus.REJECTED,
            verifiedBy = adminName,
            verifiedAt = today,
            notes = updatedNotes,
            updatedAt = today
        )

        _alternatives.value = _alternatives.value.map { if (it.id == mappingId) updated else it }
        return Result.success(updated)
    }

    /**
     * Admin action to deactivate a mapping
     */
    fun deactivateAlternative(
        mappingId: String,
        userRole: UserRole,
        adminName: String
    ): Result<VerifiedAlternative> {
        if (userRole != UserRole.ADMIN) {
            return Result.failure(SecurityException("Access Denied: Only Administrators can deactivate alternative mappings."))
        }

        val existing = getAlternativeById(mappingId)
            ?: return Result.failure(NoSuchElementException("Mapping ID '$mappingId' not found."))

        val today = getCurrentDateString()
        val updated = existing.copy(
            verificationStatus = VerificationStatus.INACTIVE,
            verifiedBy = adminName,
            updatedAt = today
        )

        _alternatives.value = _alternatives.value.map { if (it.id == mappingId) updated else it }
        return Result.success(updated)
    }

    /**
     * Admin action to delete a mapping
     */
    fun deleteAlternative(
        mappingId: String,
        userRole: UserRole
    ): Result<Unit> {
        if (userRole != UserRole.ADMIN) {
            return Result.failure(SecurityException("Access Denied: Only Administrators can delete alternative mappings."))
        }

        _alternatives.value = _alternatives.value.filterNot { it.id == mappingId }
        return Result.success(Unit)
    }
}
