package com.example.service

import com.example.data.MockData
import com.example.model.Pharmacy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PharmacyService {

    private val _pharmacies = MutableStateFlow<List<Pharmacy>>(MockData.samplePharmacies)
    val pharmacies: StateFlow<List<Pharmacy>> = _pharmacies.asStateFlow()

    fun getPharmacyById(id: String): Pharmacy? {
        return _pharmacies.value.find { it.id == id }
    }

    fun addPharmacy(pharmacy: Pharmacy) {
        _pharmacies.update { list -> listOf(pharmacy) + list }
    }

    fun updatePharmacyProfile(
        pharmacyId: String,
        address: String,
        phone: String,
        timing: String,
        facilityType: String
    ): Pharmacy? {
        var updatedPharmacy: Pharmacy? = null
        _pharmacies.update { list ->
            list.map { p ->
                if (p.id == pharmacyId) {
                    val updated = p.copy(
                        address = address,
                        phone = phone,
                        timing = timing,
                        facilityType = facilityType,
                        lastUpdated = "Just now"
                    )
                    updatedPharmacy = updated
                    updated
                } else p
            }
        }
        return updatedPharmacy
    }

    fun toggleVerification(pharmacyId: String): Pharmacy? {
        var updatedPharmacy: Pharmacy? = null
        _pharmacies.update { list ->
            list.map { p ->
                if (p.id == pharmacyId) {
                    val newVerified = !p.isVerified
                    val updated = p.copy(
                        isVerified = newVerified,
                        openStatus = if (newVerified) "Open now" else "Pending Verification"
                    )
                    updatedPharmacy = updated
                    updated
                } else p
            }
        }
        return updatedPharmacy
    }

    fun filterPharmacies(
        facilityType: String = "All",
        maxDistanceKm: Double = 10.0,
        onlyVerified: Boolean = false,
        userLat: Double = 26.7558,
        userLon: Double = 83.3735
    ): List<Pharmacy> {
        return _pharmacies.value.map { p ->
            val dist = com.example.utils.DistanceUtils.calculateDistanceKm(userLat, userLon, p.latitude, p.longitude)
            p.copy(distanceKm = dist)
        }.filter { p ->
            val matchesFacility = facilityType == "All" || p.facilityType.equals(facilityType, ignoreCase = true)
            val matchesDistance = maxDistanceKm <= 0.0 || maxDistanceKm >= 999.0 || p.distanceKm <= maxDistanceKm
            val matchesVerified = !onlyVerified || p.isVerified
            matchesFacility && matchesDistance && matchesVerified
        }.sortedBy { it.distanceKm }
    }
}
