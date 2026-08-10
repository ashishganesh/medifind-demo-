package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.service.*
import kotlinx.coroutines.flow.*

class MediFindViewModel(
    val medicineService: MedicineService = MedicineService(),
    val pharmacyService: PharmacyService = PharmacyService(),
    val inventoryService: InventoryService = InventoryService(),
    val authService: AuthService = AuthService(pharmacyService),
    val locationService: LocationService = LocationService(),
    val verifiedAlternativeService: VerifiedAlternativeService = VerifiedAlternativeService()
) : ViewModel() {

    val availabilityService: AvailabilityService = AvailabilityService(
        medicineService = medicineService,
        pharmacyService = pharmacyService,
        inventoryService = inventoryService
    )

    init {
        FirebaseConfig.initialize()
    }

    val isDemoMode: StateFlow<Boolean> = FirebaseConfig.isDemoMode
    val currentUser: StateFlow<User?> = authService.currentUser
    val isAuthenticated: StateFlow<Boolean> = authService.isAuthenticated
    val authError: StateFlow<String?> = authService.authError

    val currentRole: StateFlow<UserRole> = MutableStateFlow(
        currentUser.value?.role ?: UserRole.PATIENT
    ).apply {
        // Keep currentRole in sync with currentUser.role
    }

    private val _searchQuery = MutableStateFlow("Paracetamol 500mg")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedFacilityType = MutableStateFlow("All")
    val selectedFacilityType: StateFlow<String> = _selectedFacilityType.asStateFlow()

    private val _maxDistanceKm = MutableStateFlow(5.0) // Default 5 km search radius
    val maxDistanceKm: StateFlow<Double> = _maxDistanceKm.asStateFlow()

    private val _availabilityFilter = MutableStateFlow("All") // "All", "Available", "Low Stock"
    val availabilityFilter: StateFlow<String> = _availabilityFilter.asStateFlow()

    private val _sortBy = MutableStateFlow("Nearest") // "Nearest", "Availability", "Recently Updated"
    val sortBy: StateFlow<String> = _sortBy.asStateFlow()

    val inventoryList: StateFlow<List<InventoryRecord>> = inventoryService.inventory
    val pharmacies: StateFlow<List<Pharmacy>> = pharmacyService.pharmacies
    val medicines: StateFlow<List<Medicine>> = medicineService.medicines
    val availabilityLogs: StateFlow<List<AvailabilityLog>> = inventoryService.availabilityLogs
    val verifiedAlternatives: StateFlow<List<VerifiedAlternative>> = verifiedAlternativeService.alternatives

    val stockPredictions: StateFlow<List<StockPrediction>> =
        combine(inventoryList, availabilityLogs, medicines) { invs, logs, meds ->
            invs.map { inv ->
                val med = meds.find { it.id == inv.medicineId }
                StockPredictionEngine.calculatePrediction(inv, logs, med)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUserLocation: StateFlow<UserLocation> = locationService.userLocation

    val userLocation: StateFlow<String> = MutableStateFlow("📍 Gomti Nagar, Lucknow")

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    // Stock Edit Dialog State
    private val _editingRecord = MutableStateFlow<InventoryRecord?>(null)
    val editingRecord: StateFlow<InventoryRecord?> = _editingRecord.asStateFlow()

    fun login(email: String, pass: String): Boolean {
        return when (val result = authService.login(email, pass)) {
            is AuthResult.Success -> {
                _toastMessage.value = "Signed in successfully as ${result.user.name}"
                true
            }
            is AuthResult.Error -> {
                _toastMessage.value = result.message
                false
            }
        }
    }

    fun registerPatient(name: String, email: String, pass: String, confirmPass: String): Boolean {
        return when (val result = authService.registerPatient(name, email, pass, confirmPass)) {
            is AuthResult.Success -> {
                _toastMessage.value = "Patient account created successfully!"
                true
            }
            is AuthResult.Error -> {
                _toastMessage.value = result.message
                false
            }
        }
    }

    fun registerPharmacy(
        ownerName: String,
        email: String,
        pass: String,
        confirmPass: String,
        pharmacyName: String,
        address: String,
        phone: String,
        facilityType: String
    ): Boolean {
        return when (val result = authService.registerPharmacy(
            ownerName, email, pass, confirmPass,
            pharmacyName, address, phone, facilityType
        )) {
            is AuthResult.Success -> {
                _toastMessage.value = "Pharmacy store registered! Account is pending State Health Dept verification."
                true
            }
            is AuthResult.Error -> {
                _toastMessage.value = result.message
                false
            }
        }
    }

    fun resetPassword(email: String) {
        when (val result = authService.resetPassword(email)) {
            is AuthResult.Success -> {
                _toastMessage.value = "Password reset link sent to $email"
            }
            is AuthResult.Error -> {
                _toastMessage.value = result.message
            }
        }
    }

    fun logout() {
        authService.logout()
        _toastMessage.value = "Signed out successfully."
    }

    fun switchDemoRole(role: UserRole) {
        authService.switchDemoUser(role)
        _toastMessage.value = "Switched to ${role.title} Demo Account"
    }

    fun updatePharmacyProfile(address: String, phone: String, timing: String, facilityType: String) {
        val currentPharmacyId = currentUser.value?.pharmacyId ?: "pharm_1"
        pharmacyService.updatePharmacyProfile(currentPharmacyId, address, phone, timing, facilityType)
        _toastMessage.value = "Pharmacy store profile updated successfully!"
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setFacilityType(type: String) {
        _selectedFacilityType.value = type
    }

    fun setMaxDistance(dist: Double) {
        _maxDistanceKm.value = dist
    }

    fun setAvailabilityFilter(filter: String) {
        _availabilityFilter.value = filter
    }

    fun showEditStockDialog(record: InventoryRecord) {
        val user = currentUser.value
        // Verify pharmacy ownership: Pharmacy user can only edit stock for their own pharmacy
        if (user?.role == UserRole.PHARMACY && record.pharmacyId != user.pharmacyId) {
            _toastMessage.value = "Access Denied: You can only update stock for your own pharmacy store."
            return
        }
        _editingRecord.value = record
    }

    fun hideEditStockDialog() {
        _editingRecord.value = null
    }

    fun updateStockQuantity(recordId: String, newStock: Int, newUnitPrice: Double) {
        val user = currentUser.value ?: return
        val record = editingRecord.value ?: return

        // Extra check for security
        if (user.role == UserRole.PHARMACY && record.pharmacyId != user.pharmacyId) {
            _toastMessage.value = "Unauthorized action blocked."
            hideEditStockDialog()
            return
        }

        inventoryService.updateStock(
            recordId = recordId,
            newCount = newStock,
            newUnitPrice = newUnitPrice,
            updatedByUserId = user.id
        )
        _toastMessage.value = "Inventory updated & audit log recorded!"
        hideEditStockDialog()
    }

    fun addNewInventoryItem(pharmacyId: String, medicineId: String, initialStock: Int, unitPrice: Double) {
        val user = currentUser.value
        if (user?.role == UserRole.PHARMACY && pharmacyId != user.pharmacyId) {
            _toastMessage.value = "Access Denied: You can only manage inventory for your own pharmacy."
            return
        }
        inventoryService.addOrUpdateInventoryItem(
            pharmacyId = pharmacyId,
            medicineId = medicineId,
            initialStock = initialStock,
            unitPrice = unitPrice,
            updatedByUserId = user?.id ?: "usr_pharmacy"
        )
        _toastMessage.value = "Medicine added to inventory successfully!"
    }

    fun togglePharmacyVerification(pharmacyId: String) {
        val user = currentUser.value
        if (user?.role != UserRole.ADMIN) {
            _toastMessage.value = "Unauthorized: Only State Health Administrators can verify pharmacies."
            return
        }
        val updated = pharmacyService.toggleVerification(pharmacyId)
        if (updated != null) {
            _toastMessage.value = "Pharmacy '${updated.name}' verification status set to ${if (updated.isVerified) "VERIFIED" else "UNVERIFIED"}."
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
    }

    fun setDemoLocation(option: DemoLocationOption) {
        locationService.setDemoLocation(option)
        _toastMessage.value = "Location set to: ${option.label}"
    }

    fun setManualLocation(areaName: String, cityName: String, lat: Double, lng: Double) {
        locationService.setManualLocation(areaName, cityName, lat, lng)
        _toastMessage.value = "Location updated: $areaName, $cityName"
    }

    fun requestGpsLocation(context: android.content.Context) {
        locationService.requestGpsLocation(context) { loc ->
            _toastMessage.value = "Current location updated via GPS"
        }
    }

    fun getAlternativesWithAvailabilityForMedicine(sourceMedId: String): List<AlternativeWithAvailability> {
        val loc = currentUserLocation.value
        return verifiedAlternativeService.getAlternativesWithAvailability(
            sourceMedicineId = sourceMedId,
            medicines = medicines.value,
            inventoryList = inventoryList.value,
            pharmacies = pharmacies.value,
            userLat = loc.latitude,
            userLng = loc.longitude
        )
    }

    fun createAlternativeMapping(
        sourceId: String,
        altId: String,
        relType: RelationshipType,
        notes: String,
        initialStatus: VerificationStatus = VerificationStatus.VERIFIED
    ) {
        val user = currentUser.value
        val role = user?.role ?: UserRole.PATIENT
        val name = user?.name ?: "Admin User"

        val result = verifiedAlternativeService.createAlternativeMapping(
            sourceMedicineId = sourceId,
            alternativeMedicineId = altId,
            relationshipType = relType,
            notes = notes,
            userRole = role,
            userName = name,
            initialStatus = initialStatus
        )

        result.fold(
            onSuccess = {
                _toastMessage.value = "Verified alternative mapping created successfully!"
            },
            onFailure = { ex ->
                _toastMessage.value = ex.message ?: "Failed to create alternative mapping."
            }
        )
    }

    fun verifyAlternativeMapping(id: String) {
        val user = currentUser.value
        val role = user?.role ?: UserRole.PATIENT
        val name = user?.name ?: "Admin User"

        val result = verifiedAlternativeService.verifyAlternative(id, role, name)
        result.fold(
            onSuccess = {
                _toastMessage.value = "Alternative mapping verified & approved!"
            },
            onFailure = { ex ->
                _toastMessage.value = ex.message ?: "Failed to verify mapping."
            }
        )
    }

    fun rejectAlternativeMapping(id: String, reason: String) {
        val user = currentUser.value
        val role = user?.role ?: UserRole.PATIENT
        val name = user?.name ?: "Admin User"

        val result = verifiedAlternativeService.rejectAlternative(id, role, name, reason)
        result.fold(
            onSuccess = {
                _toastMessage.value = "Alternative mapping rejected."
            },
            onFailure = { ex ->
                _toastMessage.value = ex.message ?: "Failed to reject mapping."
            }
        )
    }

    fun deactivateAlternativeMapping(id: String) {
        val user = currentUser.value
        val role = user?.role ?: UserRole.PATIENT
        val name = user?.name ?: "Admin User"

        val result = verifiedAlternativeService.deactivateAlternative(id, role, name)
        result.fold(
            onSuccess = {
                _toastMessage.value = "Alternative mapping deactivated."
            },
            onFailure = { ex ->
                _toastMessage.value = ex.message ?: "Failed to deactivate mapping."
            }
        )
    }

    fun deleteAlternativeMapping(id: String) {
        val user = currentUser.value
        val role = user?.role ?: UserRole.PATIENT

        val result = verifiedAlternativeService.deleteAlternative(id, role)
        result.fold(
            onSuccess = {
                _toastMessage.value = "Alternative mapping deleted from catalog."
            },
            onFailure = { ex ->
                _toastMessage.value = ex.message ?: "Failed to delete mapping."
            }
        )
    }

    fun getSearchResultForMedicine(query: String): List<PharmacySearchResult> {
        val loc = currentUserLocation.value
        return availabilityService.searchAvailability(
            query = query,
            userLat = loc.latitude,
            userLon = loc.longitude,
            category = _selectedCategory.value,
            facilityType = _selectedFacilityType.value,
            maxDistanceKm = _maxDistanceKm.value,
            availabilityFilter = _availabilityFilter.value,
            sortBy = _sortBy.value
        )
    }
}

data class PharmacySearchResult(
    val pharmacy: Pharmacy,
    val inventory: InventoryRecord,
    val medicine: Medicine
)
