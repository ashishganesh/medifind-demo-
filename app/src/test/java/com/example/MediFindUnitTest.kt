package com.example

import com.example.model.AvailabilityStatus
import com.example.model.UserRole
import com.example.service.AuthResult
import com.example.service.AuthService
import com.example.service.InventoryService
import com.example.service.PharmacyService
import com.example.viewmodel.MediFindViewModel
import org.junit.Assert.*
import org.junit.Test

class MediFindUnitTest {

    @Test
    fun `patient registration creates active patient account`() {
        val pharmacyService = PharmacyService()
        val authService = AuthService(pharmacyService)

        val result = authService.registerPatient(
            name = "Test Citizen",
            email = "citizen@test.in",
            pass = "password123",
            confirmPass = "password123"
        )

        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).user
        assertEquals(UserRole.PATIENT, user.role)
        assertTrue(user.isVerified)
        assertTrue(user.isActive)
    }

    @Test
    fun `pharmacy registration creates pending verification account`() {
        val pharmacyService = PharmacyService()
        val authService = AuthService(pharmacyService)

        val result = authService.registerPharmacy(
            ownerName = "Test Pharmacy Owner",
            email = "pharmacy@test.in",
            pass = "password123",
            confirmPass = "password123",
            pharmacyName = "LifeCare Chemists",
            address = "Gomti Nagar, Lucknow",
            phone = "9876543210",
            facilityType = "Retail Pharmacy"
        )

        assertTrue(result is AuthResult.Success)
        val user = (result as AuthResult.Success).user
        assertEquals(UserRole.PHARMACY, user.role)
        assertFalse(user.isVerified) // Must be pending verification

        val pharmacy = pharmacyService.getPharmacyById(user.pharmacyId!!)
        assertNotNull(pharmacy)
        assertFalse(pharmacy!!.isVerified) // Pending verification
        assertEquals("LifeCare Chemists", pharmacy.name)
    }

    @Test
    fun `registration rejects password mismatch`() {
        val pharmacyService = PharmacyService()
        val authService = AuthService(pharmacyService)

        val result = authService.registerPatient(
            name = "Test Citizen",
            email = "citizen2@test.in",
            pass = "password123",
            confirmPass = "differentPassword"
        )

        assertTrue(result is AuthResult.Error)
        assertEquals("Passwords do not match.", (result as AuthResult.Error).message)
    }

    @Test
    fun `demo switcher changes active user role`() {
        val pharmacyService = PharmacyService()
        val authService = AuthService(pharmacyService)

        authService.switchDemoUser(UserRole.ADMIN)
        assertEquals(UserRole.ADMIN, authService.currentUser.value?.role)

        authService.switchDemoUser(UserRole.PHARMACY)
        assertEquals(UserRole.PHARMACY, authService.currentUser.value?.role)
    }

    // ==========================================
    // PHASE 3 INVENTORY MANAGEMENT UNIT TESTS
    // ==========================================

    @Test
    fun `add medicine to pharmacy inventory creates inventory record`() {
        val inventoryService = InventoryService()
        val record = inventoryService.addOrUpdateInventoryItem(
            pharmacyId = "pharm_test_1",
            medicineId = "med_11",
            initialStock = 42,
            unitPrice = 12.50,
            updatedByUserId = "owner_1"
        )

        assertNotNull(record)
        assertEquals("pharm_test_1", record.pharmacyId)
        assertEquals("med_11", record.medicineId)
        assertEquals(42, record.stockCount)
        assertEquals(AvailabilityStatus.AVAILABLE, record.status)
    }

    @Test
    fun `add same medicine again updates existing record without duplicate`() {
        val inventoryService = InventoryService()
        
        inventoryService.addOrUpdateInventoryItem(
            pharmacyId = "pharm_dup_1",
            medicineId = "med_12",
            initialStock = 15,
            unitPrice = 20.0
        )

        val initialCount = inventoryService.inventory.value.count {
            it.pharmacyId == "pharm_dup_1" && it.medicineId == "med_12"
        }
        assertEquals(1, initialCount)

        val record2 = inventoryService.addOrUpdateInventoryItem(
            pharmacyId = "pharm_dup_1",
            medicineId = "med_12",
            initialStock = 50,
            unitPrice = 22.0
        )

        val finalCount = inventoryService.inventory.value.count {
            it.pharmacyId == "pharm_dup_1" && it.medicineId == "med_12"
        }
        assertEquals(1, finalCount) // Duplicate prevented!
        assertEquals(50, record2.stockCount)
    }

    @Test
    fun `availability status threshold logic evaluates correctly`() {
        assertEquals(AvailabilityStatus.AVAILABLE, AvailabilityStatus.fromStock(42))
        assertEquals(AvailabilityStatus.AVAILABLE, AvailabilityStatus.fromStock(11))
        assertEquals(AvailabilityStatus.LOW_STOCK, AvailabilityStatus.fromStock(10))
        assertEquals(AvailabilityStatus.LOW_STOCK, AvailabilityStatus.fromStock(1))
        assertEquals(AvailabilityStatus.OUT_OF_STOCK, AvailabilityStatus.fromStock(0))
        assertEquals(AvailabilityStatus.OUT_OF_STOCK, AvailabilityStatus.fromStock(-5))
    }

    @Test
    fun `negative stock is rejected and coerced to zero`() {
        val inventoryService = InventoryService()
        val record = inventoryService.addOrUpdateInventoryItem(
            pharmacyId = "pharm_neg",
            medicineId = "med_14",
            initialStock = -10,
            unitPrice = -5.0
        )

        assertEquals(0, record.stockCount)
        assertEquals(0.0, record.unitPriceRupees, 0.01)
        assertEquals(AvailabilityStatus.OUT_OF_STOCK, record.status)
    }

    @Test
    fun `stock update records audit log history`() {
        val inventoryService = InventoryService()
        val record = inventoryService.addOrUpdateInventoryItem(
            pharmacyId = "pharm_audit",
            medicineId = "med_15",
            initialStock = 42,
            unitPrice = 10.0,
            updatedByUserId = "usr_1"
        )

        val updated = inventoryService.updateStock(
            recordId = record.id,
            newCount = 8,
            newUnitPrice = 10.0,
            updatedByUserId = "usr_1"
        )

        assertNotNull(updated)
        assertEquals(8, updated!!.stockCount)
        assertEquals(AvailabilityStatus.LOW_STOCK, updated.status)

        val logs = inventoryService.getLogsForInventoryItem(record.id)
        assertTrue(logs.isNotEmpty())
        val latestLog = logs.first()
        assertEquals(42, latestLog.previousCount)
        assertEquals(8, latestLog.newCount)
        assertEquals(AvailabilityStatus.AVAILABLE, latestLog.previousStatus)
        assertEquals(AvailabilityStatus.LOW_STOCK, latestLog.newStatus)
    }

    @Test
    fun `patient medicine search returns only matching pharmacy inventory`() {
        val viewModel = MediFindViewModel()
        viewModel.updateSearchQuery("Paracetamol")

        val results = viewModel.getSearchResultForMedicine("Paracetamol")
        assertTrue(results.isNotEmpty())
        results.forEach { res ->
            assertTrue(res.medicine.name.contains("Paracetamol", ignoreCase = true) ||
                    res.medicine.genericName.contains("Paracetamol", ignoreCase = true))
        }
    }

    @Test
    fun `unauthorized inventory edit attempt blocked by ownership check`() {
        val viewModel = MediFindViewModel()
        viewModel.switchDemoRole(UserRole.PHARMACY) // Logged in as pharm_1

        val targetOtherPharmacyRecord = viewModel.inventoryList.value.first { it.pharmacyId == "pharm_2" }
        viewModel.showEditStockDialog(targetOtherPharmacyRecord)

        assertNull(viewModel.editingRecord.value)
        assertEquals("Access Denied: You can only update stock for your own pharmacy store.", viewModel.toastMessage.value)
    }

    // ==========================================
    // PHASE 5 STOCK PREDICTION & FORECASTING TESTS
    // ==========================================

    @Test
    fun `test 2 - stock 42 with demand 6 per day calculates exactly 7 days remaining`() {
        val invRecord = com.example.model.InventoryRecord(
            id = "inv_1",
            pharmacyId = "pharm_1",
            medicineId = "med_1",
            stockCount = 42,
            unitPriceRupees = 10.0,
            status = com.example.model.AvailabilityStatus.AVAILABLE,
            lastUpdated = "Today"
        )

        val prediction = com.example.service.StockPredictionEngine.calculatePrediction(
            inventoryRecord = invRecord,
            logs = com.example.data.DemoStockHistory.demoLogs
        )

        assertEquals(42, prediction.currentStock)
        assertEquals(6.0, prediction.averageDailyConsumption, 0.1)
        assertEquals(7.0, prediction.estimatedDaysRemaining!!, 0.1)
        assertEquals(com.example.model.StockRiskLevel.MEDIUM, prediction.riskLevel)
        assertEquals(com.example.model.DemandPattern.STABLE, prediction.demandPattern)
    }

    @Test
    fun `test 3 - stock 8 with demand 4_3 per day yields critical risk under 2 days`() {
        val invRecord = com.example.model.InventoryRecord(
            id = "inv_5",
            pharmacyId = "pharm_1",
            medicineId = "med_5",
            stockCount = 8,
            unitPriceRupees = 15.0,
            status = com.example.model.AvailabilityStatus.LOW_STOCK,
            lastUpdated = "Today"
        )

        val prediction = com.example.service.StockPredictionEngine.calculatePrediction(
            inventoryRecord = invRecord,
            logs = com.example.data.DemoStockHistory.demoLogs
        )

        assertEquals(8, prediction.currentStock)
        assertTrue(prediction.estimatedDaysRemaining!! <= 2.0)
        assertEquals(com.example.model.StockRiskLevel.CRITICAL, prediction.riskLevel)
        assertTrue(prediction.recommendation.contains("Consider restocking soon"))
    }

    @Test
    fun `test 4 - zero current stock returns out of stock status without division by zero`() {
        val invRecord = com.example.model.InventoryRecord(
            id = "inv_8",
            pharmacyId = "pharm_1",
            medicineId = "med_8",
            stockCount = 0,
            unitPriceRupees = 25.0,
            status = com.example.model.AvailabilityStatus.OUT_OF_STOCK,
            lastUpdated = "Today"
        )

        val prediction = com.example.service.StockPredictionEngine.calculatePrediction(
            inventoryRecord = invRecord,
            logs = com.example.data.DemoStockHistory.demoLogs
        )

        assertEquals(0, prediction.currentStock)
        assertEquals(0.0, prediction.estimatedDaysRemaining!!, 0.01)
        assertEquals("Already out of stock", prediction.estimatedStockOutDate)
        assertEquals(com.example.model.StockRiskLevel.CRITICAL, prediction.riskLevel)
    }

    @Test
    fun `test 6 - insufficient data under 7 days returns unknown risk and forecast unavailable`() {
        val invRecord = com.example.model.InventoryRecord(
            id = "inv_7",
            pharmacyId = "pharm_1",
            medicineId = "med_7",
            stockCount = 25,
            unitPriceRupees = 45.0,
            status = com.example.model.AvailabilityStatus.AVAILABLE,
            lastUpdated = "Today"
        )

        val prediction = com.example.service.StockPredictionEngine.calculatePrediction(
            inventoryRecord = invRecord,
            logs = com.example.data.DemoStockHistory.demoLogs
        )

        assertEquals(com.example.model.StockRiskLevel.UNKNOWN, prediction.riskLevel)
        assertEquals(com.example.model.ForecastReliability.LOW, prediction.forecastReliability)
        assertNull(prediction.estimatedDaysRemaining)
        assertTrue(prediction.estimatedStockOutDate.contains("Forecast unavailable"))
    }

    @Test
    fun `test 7 - restock event is isolated and does not count as negative consumption`() {
        // Insulin Glargine history has restock from 88 to 150
        val invRecord = com.example.model.InventoryRecord(
            id = "inv_3",
            pharmacyId = "pharm_1",
            medicineId = "med_3",
            stockCount = 138,
            unitPriceRupees = 450.0,
            status = com.example.model.AvailabilityStatus.AVAILABLE,
            lastUpdated = "Today"
        )

        val prediction = com.example.service.StockPredictionEngine.calculatePrediction(
            inventoryRecord = invRecord,
            logs = com.example.data.DemoStockHistory.demoLogs
        )

        assertEquals(1, prediction.restockEventsCount)
        assertEquals(24, prediction.totalConsumptionUnits) // 4+4+4+4+4+4 = 24 total decreases
        assertTrue(prediction.averageDailyConsumption > 0)
        assertEquals(com.example.model.StockRiskLevel.LOW, prediction.riskLevel) // >10 days remaining
        assertEquals(com.example.model.ForecastReliability.HIGH, prediction.forecastReliability) // 14 days history
    }

    @Test
    fun `test 8 - variable demand pattern triggers variability warning`() {
        val invRecord = com.example.model.InventoryRecord(
            id = "inv_2",
            pharmacyId = "pharm_1",
            medicineId = "med_2",
            stockCount = 15,
            unitPriceRupees = 30.0,
            status = com.example.model.AvailabilityStatus.AVAILABLE,
            lastUpdated = "Today"
        )

        val prediction = com.example.service.StockPredictionEngine.calculatePrediction(
            inventoryRecord = invRecord,
            logs = com.example.data.DemoStockHistory.demoLogs
        )

        assertEquals(com.example.model.DemandPattern.VARIABLE, prediction.demandPattern)
        assertNotNull(prediction.variabilityWarning)
        assertTrue(prediction.variabilityWarning!!.contains("Forecast may change significantly"))
    }

    @Test
    fun `test 13 - predictions are deterministic and reproducible without random values`() {
        val invRecord = com.example.model.InventoryRecord(
            id = "inv_1",
            pharmacyId = "pharm_1",
            medicineId = "med_1",
            stockCount = 42,
            unitPriceRupees = 10.0,
            status = com.example.model.AvailabilityStatus.AVAILABLE,
            lastUpdated = "Today"
        )

        val pred1 = com.example.service.StockPredictionEngine.calculatePrediction(invRecord, com.example.data.DemoStockHistory.demoLogs)
        val pred2 = com.example.service.StockPredictionEngine.calculatePrediction(invRecord, com.example.data.DemoStockHistory.demoLogs)

        assertEquals(pred1.averageDailyConsumption, pred2.averageDailyConsumption, 0.001)
        assertEquals(pred1.estimatedDaysRemaining, pred2.estimatedDaysRemaining)
        assertEquals(pred1.riskLevel, pred2.riskLevel)
        assertEquals(pred1.estimatedStockOutDate, pred2.estimatedStockOutDate)
    }

    // ==========================================
    // PHASE 6 VERIFIED ALTERNATIVE MEDICINE SYSTEM TESTS
    // ==========================================

    @Test
    fun `patient lookup returns only verified alternative mappings`() {
        val alternativeService = com.example.service.VerifiedAlternativeService()
        val verifiedForMed1 = alternativeService.getVerifiedAlternatives("med_1")

        assertTrue(verifiedForMed1.isNotEmpty())
        verifiedForMed1.forEach { alt ->
            assertEquals(com.example.model.VerificationStatus.VERIFIED, alt.verificationStatus)
            assertEquals("med_1", alt.sourceMedicineId)
        }

        // Verify pending, rejected, and inactive mappings are excluded
        val pendingCount = verifiedForMed1.count { it.verificationStatus == com.example.model.VerificationStatus.PENDING }
        val rejectedCount = verifiedForMed1.count { it.verificationStatus == com.example.model.VerificationStatus.REJECTED }
        val inactiveCount = verifiedForMed1.count { it.verificationStatus == com.example.model.VerificationStatus.INACTIVE }

        assertEquals(0, pendingCount)
        assertEquals(0, rejectedCount)
        assertEquals(0, inactiveCount)
    }

    @Test
    fun `creating self referencing alternative mapping fails validation`() {
        val alternativeService = com.example.service.VerifiedAlternativeService()
        val result = alternativeService.createAlternativeMapping(
            sourceMedicineId = "med_1",
            alternativeMedicineId = "med_1",
            relationshipType = com.example.model.RelationshipType.SAME_ACTIVE_INGREDIENT,
            notes = "Self reference test",
            userRole = UserRole.ADMIN,
            userName = "Dr. Admin"
        )

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains("cannot be identical"))
    }

    @Test
    fun `creating duplicate alternative mapping fails validation`() {
        val alternativeService = com.example.service.VerifiedAlternativeService()
        val result = alternativeService.createAlternativeMapping(
            sourceMedicineId = "med_1",
            alternativeMedicineId = "med_2",
            relationshipType = com.example.model.RelationshipType.SAME_ACTIVE_INGREDIENT,
            notes = "Duplicate attempt",
            userRole = UserRole.ADMIN,
            userName = "Dr. Admin"
        )

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains("already exists"))
    }

    @Test
    fun `non admin attempt to create or verify mapping is rejected by security policy`() {
        val alternativeService = com.example.service.VerifiedAlternativeService()

        // Patient create attempt
        val createResult = alternativeService.createAlternativeMapping(
            sourceMedicineId = "med_1",
            alternativeMedicineId = "med_4",
            relationshipType = com.example.model.RelationshipType.SAME_ACTIVE_INGREDIENT,
            notes = "Unauthorized create",
            userRole = UserRole.PATIENT,
            userName = "Unauthorized Citizen"
        )
        assertTrue(createResult.isFailure)
        assertTrue(createResult.exceptionOrNull()!!.message!!.contains("Access Denied"))

        // Pharmacy verify attempt
        val verifyResult = alternativeService.verifyAlternative(
            mappingId = "alt_7",
            userRole = UserRole.PHARMACY,
            adminName = "Unauthorized Pharmacist"
        )
        assertTrue(verifyResult.isFailure)
        assertTrue(verifyResult.exceptionOrNull()!!.message!!.contains("Access Denied"))
    }

    @Test
    fun `admin approve mapping sets status to VERIFIED with audit info`() {
        val alternativeService = com.example.service.VerifiedAlternativeService()

        // alt_7 is initially PENDING
        val approveResult = alternativeService.verifyAlternative(
            mappingId = "alt_7",
            userRole = UserRole.ADMIN,
            adminName = "Dr. Chief Officer"
        )

        assertTrue(approveResult.isSuccess)
        val approved = approveResult.getOrNull()!!
        assertEquals(com.example.model.VerificationStatus.VERIFIED, approved.verificationStatus)
        assertEquals("Dr. Chief Officer", approved.verifiedBy)
        assertNotNull(approved.verifiedAt)
    }

    @Test
    fun `admin reject mapping sets status to REJECTED with reason`() {
        val alternativeService = com.example.service.VerifiedAlternativeService()

        val rejectResult = alternativeService.rejectAlternative(
            mappingId = "alt_8",
            userRole = UserRole.ADMIN,
            adminName = "Dr. Inspector",
            reason = "Therapeutic safety concern"
        )

        assertTrue(rejectResult.isSuccess)
        val rejected = rejectResult.getOrNull()!!
        assertEquals(com.example.model.VerificationStatus.REJECTED, rejected.verificationStatus)
        assertTrue(rejected.notes.contains("Therapeutic safety concern"))
    }

    @Test
    fun `directional mapping A to B does not automatically create reverse B to A`() {
        val alternativeService = com.example.service.VerifiedAlternativeService()

        // med_1 -> med_2 is verified
        val alternativesForMed1 = alternativeService.getVerifiedAlternatives("med_1")
        assertTrue(alternativesForMed1.any { it.alternativeMedicineId == "med_2" })

        // Check med_2 -> med_1 is NOT automatically assumed
        val alternativesForMed2 = alternativeService.getVerifiedAlternatives("med_2")
        assertFalse(alternativesForMed2.any { it.alternativeMedicineId == "med_1" })
    }

    @Test
    fun `demo alternatives contains deterministic status breakdown`() {
        val alternatives = com.example.data.DemoAlternatives.demoAlternatives
        assertTrue(alternatives.size >= 12)

        val verified = alternatives.count { it.verificationStatus == com.example.model.VerificationStatus.VERIFIED }
        val pending = alternatives.count { it.verificationStatus == com.example.model.VerificationStatus.PENDING }
        val rejected = alternatives.count { it.verificationStatus == com.example.model.VerificationStatus.REJECTED }
        val inactive = alternatives.count { it.verificationStatus == com.example.model.VerificationStatus.INACTIVE }

        assertTrue(verified >= 5)
        assertTrue(pending >= 2)
        assertTrue(rejected >= 2)
        assertTrue(inactive >= 2)
    }
}
