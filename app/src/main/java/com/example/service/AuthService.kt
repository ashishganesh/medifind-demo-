package com.example.service

import com.example.data.MockData
import com.example.model.Pharmacy
import com.example.model.User
import com.example.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthService(
    private val pharmacyService: PharmacyService
) {
    private val _users = MutableStateFlow<List<User>>(MockData.sampleUsers)
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(_users.value.first())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(true)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun login(email: String, pass: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isEmpty() || pass.isEmpty()) {
            return AuthResult.Error("Please enter both email address and password.")
        }

        val existingUser = _users.value.find { it.email.lowercase() == trimmedEmail }
        return if (existingUser != null) {
            _currentUser.value = existingUser
            _isAuthenticated.value = true
            _authError.value = null
            AuthResult.Success(existingUser)
        } else {
            // For demo flexibility: if email matches domain pattern or general email, authenticate dynamically
            val role = when {
                trimmedEmail.contains("admin") -> UserRole.ADMIN
                trimmedEmail.contains("pharm") || trimmedEmail.contains("medical") -> UserRole.PHARMACY
                else -> UserRole.PATIENT
            }
            val newUser = User(
                id = "usr_${UUID.randomUUID().toString().take(8)}",
                name = trimmedEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = trimmedEmail,
                role = role,
                pharmacyId = if (role == UserRole.PHARMACY) "pharm_1" else null
            )
            _users.value = _users.value + newUser
            _currentUser.value = newUser
            _isAuthenticated.value = true
            _authError.value = null
            AuthResult.Success(newUser)
        }
    }

    fun registerPatient(name: String, email: String, pass: String, confirmPass: String): AuthResult {
        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
            return AuthResult.Error("All fields are required.")
        }
        if (pass.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters long.")
        }
        if (pass != confirmPass) {
            return AuthResult.Error("Passwords do not match.")
        }
        if (_users.value.any { it.email.lowercase() == email.trim().lowercase() }) {
            return AuthResult.Error("Email address is already registered.")
        }

        val newUser = User(
            id = "usr_${UUID.randomUUID().toString().take(8)}",
            name = name.trim(),
            email = email.trim().lowercase(),
            role = UserRole.PATIENT,
            isVerified = true,
            isActive = true
        )
        _users.value = _users.value + newUser
        _currentUser.value = newUser
        _isAuthenticated.value = true
        _authError.value = null
        return AuthResult.Success(newUser)
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
    ): AuthResult {
        if (ownerName.isBlank() || email.isBlank() || pass.isBlank() || pharmacyName.isBlank() || address.isBlank() || phone.isBlank()) {
            return AuthResult.Error("Please fill in all store and account details.")
        }
        if (pass.length < 6) {
            return AuthResult.Error("Password must be at least 6 characters long.")
        }
        if (pass != confirmPass) {
            return AuthResult.Error("Passwords do not match.")
        }
        if (_users.value.any { it.email.lowercase() == email.trim().lowercase() }) {
            return AuthResult.Error("Email address is already registered.")
        }

        val ownerUid = "usr_${UUID.randomUUID().toString().take(8)}"
        val pharmacyId = "pharm_${UUID.randomUUID().toString().take(8)}"

        // Create new registered pharmacy store in Pending Verification state
        val newPharmacy = Pharmacy(
            id = pharmacyId,
            name = pharmacyName.trim(),
            address = address.trim(),
            distanceKm = 2.5,
            phone = phone.trim(),
            openStatus = "Pending Verification",
            timing = "09:00 AM - 09:00 PM",
            isVerified = false, // Pending verification by Admin
            facilityType = facilityType,
            rating = 4.0,
            lastUpdated = "Just now",
            ownerId = ownerUid
        )
        pharmacyService.addPharmacy(newPharmacy)

        val newUser = User(
            id = ownerUid,
            name = ownerName.trim(),
            email = email.trim().lowercase(),
            role = UserRole.PHARMACY,
            pharmacyId = pharmacyId,
            isVerified = false, // Pending Admin Approval
            isActive = true
        )

        _users.value = _users.value + newUser
        _currentUser.value = newUser
        _isAuthenticated.value = true
        _authError.value = null
        return AuthResult.Success(newUser)
    }

    fun resetPassword(email: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        return AuthResult.Success(
            User(
                id = "temp",
                name = "User",
                email = trimmedEmail,
                role = UserRole.PATIENT
            )
        )
    }

    fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
        _authError.value = null
    }

    fun switchDemoUser(role: UserRole) {
        val userForRole = _users.value.find { it.role == role } ?: when (role) {
            UserRole.PATIENT -> User("usr_demo_p", "Demo Patient", "patient@demo.in", UserRole.PATIENT)
            UserRole.PHARMACY -> User("usr_demo_ph", "Demo Pharmacy Owner", "pharmacy@demo.in", UserRole.PHARMACY, pharmacyId = "pharm_1")
            UserRole.ADMIN -> User("usr_demo_ad", "Demo State Admin", "admin@demo.in", UserRole.ADMIN)
        }
        _currentUser.value = userForRole
        _isAuthenticated.value = true
        _authError.value = null
    }
}
