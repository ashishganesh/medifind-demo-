package com.example.model

enum class RelationshipType(
    val displayName: String,
    val description: String
) {
    SAME_ACTIVE_INGREDIENT(
        displayName = "Same Active Ingredient",
        description = "Contains identical active pharmaceutical substance in equivalent strength."
    ),
    SAME_STRENGTH_DIFFERENT_BRAND(
        displayName = "Same Strength (Different Brand)",
        description = "Identical active ingredient strength produced by a different manufacturer."
    ),
    SAME_FORM_DIFFERENT_BRAND(
        displayName = "Same Dosage Form (Different Brand)",
        description = "Equivalent dosage form with matching therapeutic category."
    ),
    PHARMACIST_VERIFIED_EQUIVALENT(
        displayName = "Pharmacist Verified Equivalent",
        description = "Clinically recognized alternative evaluated and confirmed by medical authority."
    )
}

enum class VerificationStatus(
    val displayName: String,
    val description: String
) {
    VERIFIED(
        displayName = "Verified",
        description = "Verified by medical authority and approved for patient reference."
    ),
    PENDING(
        displayName = "Pending Verification",
        description = "Awaiting administrator review and clinical verification."
    ),
    REJECTED(
        displayName = "Rejected",
        description = "Rejected due to therapeutic mismatch or safety policy."
    ),
    INACTIVE(
        displayName = "Inactive",
        description = "Deactivated mapping, currently not active in catalog."
    )
}

data class VerifiedAlternative(
    val id: String,
    val sourceMedicineId: String,
    val alternativeMedicineId: String,
    val relationshipType: RelationshipType,
    val verificationStatus: VerificationStatus,
    val verificationSource: String = "SIH Demo Data",
    val verifiedBy: String? = "Dr. Rajesh Verma",
    val verifiedAt: String? = "2026-08-10",
    val notes: String = "",
    val createdAt: String = "2026-08-10",
    val updatedAt: String = "2026-08-10"
)

data class AlternativeWithAvailability(
    val mapping: VerifiedAlternative,
    val alternativeMedicine: Medicine,
    val bestPharmacy: Pharmacy?,
    val bestInventory: InventoryRecord?,
    val nearbyStockCount: Int,
    val nearbyPharmaciesCount: Int,
    val overallStatus: AvailabilityStatus,
    val minDistanceKm: Double?
)
