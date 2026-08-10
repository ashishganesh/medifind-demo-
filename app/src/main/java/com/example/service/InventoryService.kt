package com.example.service

import com.example.data.MockData
import com.example.model.AvailabilityLog
import com.example.model.AvailabilityStatus
import com.example.model.InventoryRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class InventoryService {

    private val _inventory = MutableStateFlow<List<InventoryRecord>>(MockData.initialInventory)
    val inventory: StateFlow<List<InventoryRecord>> = _inventory.asStateFlow()

    private val _availabilityLogs = MutableStateFlow<List<AvailabilityLog>>(MockData.initialAvailabilityLogs)
    val availabilityLogs: StateFlow<List<AvailabilityLog>> = _availabilityLogs.asStateFlow()

    fun resetToBaseline() {
        _inventory.value = MockData.initialInventory
        _availabilityLogs.value = MockData.initialAvailabilityLogs
    }

    fun updateStock(
        recordId: String,
        newCount: Int,
        newUnitPrice: Double,
        updatedByUserId: String = "usr_pharmacy"
    ): InventoryRecord? {
        val sanitizedCount = newCount.coerceAtLeast(0)
        val newStatus = AvailabilityStatus.fromStock(sanitizedCount)

        var updatedRecord: InventoryRecord? = null

        _inventory.update { list ->
            list.map { record ->
                if (record.id == recordId) {
                    val prevCount = record.stockCount
                    val prevStatus = record.status

                    val newRecord = record.copy(
                        stockCount = sanitizedCount,
                        unitPriceRupees = newUnitPrice.coerceAtLeast(0.0),
                        status = newStatus,
                        lastUpdated = "Just now"
                    )
                    updatedRecord = newRecord

                    val log = AvailabilityLog(
                        id = "log_${UUID.randomUUID().toString().take(8)}",
                        inventoryId = record.id,
                        pharmacyId = record.pharmacyId,
                        medicineId = record.medicineId,
                        previousCount = prevCount,
                        newCount = sanitizedCount,
                        previousStatus = prevStatus,
                        newStatus = newStatus,
                        updatedByUserId = updatedByUserId,
                        timestamp = "Just now"
                    )
                    _availabilityLogs.update { logs -> listOf(log) + logs }

                    newRecord
                } else record
            }
        }
        return updatedRecord
    }

    /**
     * Adds a medicine to pharmacy inventory.
     * Prevents duplicate records for the same pharmacyId + medicineId.
     * If combination already exists, updates existing record.
     */
    fun addOrUpdateInventoryItem(
        pharmacyId: String,
        medicineId: String,
        initialStock: Int,
        unitPrice: Double,
        updatedByUserId: String = "usr_pharmacy"
    ): InventoryRecord {
        val sanitizedCount = initialStock.coerceAtLeast(0)
        val sanitizedPrice = unitPrice.coerceAtLeast(0.0)
        val status = AvailabilityStatus.fromStock(sanitizedCount)

        val existing = _inventory.value.find { it.pharmacyId == pharmacyId && it.medicineId == medicineId }
        if (existing != null) {
            return updateStock(existing.id, sanitizedCount, sanitizedPrice, updatedByUserId) ?: existing
        }

        val newRecord = InventoryRecord(
            id = "inv_${UUID.randomUUID().toString().take(8)}",
            pharmacyId = pharmacyId,
            medicineId = medicineId,
            stockCount = sanitizedCount,
            unitPriceRupees = sanitizedPrice,
            status = status,
            lastUpdated = "Just now"
        )

        _inventory.update { list -> listOf(newRecord) + list }

        val log = AvailabilityLog(
            id = "log_${UUID.randomUUID().toString().take(8)}",
            inventoryId = newRecord.id,
            pharmacyId = pharmacyId,
            medicineId = medicineId,
            previousCount = 0,
            newCount = sanitizedCount,
            previousStatus = AvailabilityStatus.OUT_OF_STOCK,
            newStatus = status,
            updatedByUserId = updatedByUserId,
            timestamp = "Just now"
        )
        _availabilityLogs.update { logs -> listOf(log) + logs }

        return newRecord
    }

    fun removeInventoryItem(recordId: String, updatedByUserId: String = "usr_pharmacy"): Boolean {
        val target = _inventory.value.find { it.id == recordId } ?: return false

        _inventory.update { list -> list.filter { it.id != recordId } }

        val log = AvailabilityLog(
            id = "log_${UUID.randomUUID().toString().take(8)}",
            inventoryId = target.id,
            pharmacyId = target.pharmacyId,
            medicineId = target.medicineId,
            previousCount = target.stockCount,
            newCount = 0,
            previousStatus = target.status,
            newStatus = AvailabilityStatus.OUT_OF_STOCK,
            updatedByUserId = updatedByUserId,
            timestamp = "Just now (Removed)"
        )
        _availabilityLogs.update { logs -> listOf(log) + logs }

        return true
    }

    fun getInventoryForPharmacy(pharmacyId: String): List<InventoryRecord> {
        return _inventory.value.filter { it.pharmacyId == pharmacyId }
    }

    fun getLowStockInventory(pharmacyId: String): List<InventoryRecord> {
        return _inventory.value.filter { it.pharmacyId == pharmacyId && it.status == AvailabilityStatus.LOW_STOCK }
    }

    fun getRecordsForMedicine(medicineId: String): List<InventoryRecord> {
        return _inventory.value.filter { it.medicineId == medicineId }
    }

    fun getLogsForInventoryItem(inventoryId: String): List<AvailabilityLog> {
        return _availabilityLogs.value.filter { it.inventoryId == inventoryId }
    }
}
