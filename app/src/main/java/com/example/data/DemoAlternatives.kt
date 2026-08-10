package com.example.data

import com.example.model.RelationshipType
import com.example.model.VerificationStatus
import com.example.model.VerifiedAlternative

object DemoAlternatives {

    val demoAlternatives = listOf(
        // 1. VERIFIED: Paracetamol 500mg (med_1) -> Dolo 650mg (med_2)
        VerifiedAlternative(
            id = "alt_1",
            sourceMedicineId = "med_1",
            alternativeMedicineId = "med_2",
            relationshipType = RelationshipType.SAME_ACTIVE_INGREDIENT,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationSource = "SIH Demo Data",
            verifiedBy = "Dr. Rajesh Verma (Chief Medical Officer)",
            verifiedAt = "2026-08-01",
            notes = "Both contain Paracetamol active salt. Dolo is higher strength (650mg vs 500mg).",
            createdAt = "2026-08-01",
            updatedAt = "2026-08-01"
        ),

        // 2. VERIFIED: Pantoprazole 40mg (med_5) -> Omeprazole 20mg (med_14)
        VerifiedAlternative(
            id = "alt_2",
            sourceMedicineId = "med_5",
            alternativeMedicineId = "med_14",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationSource = "SIH Demo Data",
            verifiedBy = "Central Pharmacy Board",
            verifiedAt = "2026-08-02",
            notes = "Proton pump inhibitors for gastric acid regulation.",
            createdAt = "2026-08-02",
            updatedAt = "2026-08-02"
        ),

        // 3. VERIFIED: Cetirizine 10mg (med_6) -> Levocetirizine 5mg (med_18)
        VerifiedAlternative(
            id = "alt_3",
            sourceMedicineId = "med_6",
            alternativeMedicineId = "med_18",
            relationshipType = RelationshipType.SAME_ACTIVE_INGREDIENT,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationSource = "SIH Demo Data",
            verifiedBy = "National Formulary Guidelines",
            verifiedAt = "2026-08-03",
            notes = "Levocetirizine is the active R-enantiomer of Cetirizine.",
            createdAt = "2026-08-03",
            updatedAt = "2026-08-03"
        ),

        // 4. VERIFIED: Amoxicillin 500mg (med_3) -> Azithromycin 500mg (med_8)
        VerifiedAlternative(
            id = "alt_4",
            sourceMedicineId = "med_3",
            alternativeMedicineId = "med_8",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationSource = "SIH Demo Data",
            verifiedBy = "Dr. S. K. Gupta",
            verifiedAt = "2026-08-04",
            notes = "Alternative broad-spectrum antibiotic option for respiratory tract infections.",
            createdAt = "2026-08-04",
            updatedAt = "2026-08-04"
        ),

        // 5. VERIFIED: Ibuprofen 400mg (med_11) -> Paracetamol 500mg (med_1)
        VerifiedAlternative(
            id = "alt_5",
            sourceMedicineId = "med_11",
            alternativeMedicineId = "med_1",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationSource = "SIH Demo Data",
            verifiedBy = "Pharmacists Association",
            verifiedAt = "2026-08-05",
            notes = "Analgesic & antipyretic alternative for mild body aches.",
            createdAt = "2026-08-05",
            updatedAt = "2026-08-05"
        ),

        // 6. VERIFIED: Amlodipine 5mg (med_12) -> Telmisartan 40mg (med_13)
        VerifiedAlternative(
            id = "alt_6",
            sourceMedicineId = "med_12",
            alternativeMedicineId = "med_13",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.VERIFIED,
            verificationSource = "SIH Demo Data",
            verifiedBy = "Cardiology Expert Panel",
            verifiedAt = "2026-08-06",
            notes = "Alternative antihypertensive therapy.",
            createdAt = "2026-08-06",
            updatedAt = "2026-08-06"
        ),

        // 7. PENDING: Metformin 500mg (med_4) -> Telmisartan 40mg (med_13)
        VerifiedAlternative(
            id = "alt_7",
            sourceMedicineId = "med_4",
            alternativeMedicineId = "med_13",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.PENDING,
            verificationSource = "Submitted by Pharmacy",
            verifiedBy = null,
            verifiedAt = null,
            notes = "Submitted by Sharma Medical Store for metabolic co-therapy review.",
            createdAt = "2026-08-07",
            updatedAt = "2026-08-07"
        ),

        // 8. PENDING: Insulin Glargine (med_9) -> Metformin 500mg (med_4)
        VerifiedAlternative(
            id = "alt_8",
            sourceMedicineId = "med_9",
            alternativeMedicineId = "med_4",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.PENDING,
            verificationSource = "Submitted by Retailer",
            verifiedBy = null,
            verifiedAt = null,
            notes = "Pending endocrinologist evaluation for oral vs injectable regimen.",
            createdAt = "2026-08-08",
            updatedAt = "2026-08-08"
        ),

        // 9. REJECTED: Amoxicillin 500mg (med_3) -> Paracetamol 500mg (med_1)
        VerifiedAlternative(
            id = "alt_9",
            sourceMedicineId = "med_3",
            alternativeMedicineId = "med_1",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.REJECTED,
            verificationSource = "SIH Audit",
            verifiedBy = "Dr. Rajesh Verma",
            verifiedAt = "2026-08-09",
            notes = "Rejected: Antibiotic cannot be substituted with an antipyretic painkiller.",
            createdAt = "2026-08-09",
            updatedAt = "2026-08-09"
        ),

        // 10. REJECTED: Anti-Rabies Vaccine (med_10) -> ORS Powder (med_7)
        VerifiedAlternative(
            id = "alt_10",
            sourceMedicineId = "med_10",
            alternativeMedicineId = "med_7",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.REJECTED,
            verificationSource = "SIH Safety Review",
            verifiedBy = "Dr. Rajesh Verma",
            verifiedAt = "2026-08-09",
            notes = "Rejected: Critical post-exposure vaccine cannot be substituted with rehydration salts.",
            createdAt = "2026-08-09",
            updatedAt = "2026-08-09"
        ),

        // 11. INACTIVE: Vitamin C 500mg (med_15) -> Calcium + Vitamin D3 (med_16)
        VerifiedAlternative(
            id = "alt_11",
            sourceMedicineId = "med_15",
            alternativeMedicineId = "med_16",
            relationshipType = RelationshipType.SAME_FORM_DIFFERENT_BRAND,
            verificationStatus = VerificationStatus.INACTIVE,
            verificationSource = "SIH Admin",
            verifiedBy = "Admin User",
            verifiedAt = "2026-08-05",
            notes = "Deactivated mapping after product category revision.",
            createdAt = "2026-08-05",
            updatedAt = "2026-08-09"
        ),

        // 12. INACTIVE: Ciprofloxacin 500mg (med_21) -> Azithromycin 500mg (med_8)
        VerifiedAlternative(
            id = "alt_12",
            sourceMedicineId = "med_21",
            alternativeMedicineId = "med_8",
            relationshipType = RelationshipType.PHARMACIST_VERIFIED_EQUIVALENT,
            verificationStatus = VerificationStatus.INACTIVE,
            verificationSource = "SIH Admin",
            verifiedBy = "Admin User",
            verifiedAt = "2026-08-06",
            notes = "Deactivated due to updated antibiotic stewardship guidelines.",
            createdAt = "2026-08-06",
            updatedAt = "2026-08-09"
        )
    )
}
