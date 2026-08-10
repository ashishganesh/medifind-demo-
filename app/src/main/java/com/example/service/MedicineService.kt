package com.example.service

import com.example.data.MockData
import com.example.model.Medicine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MedicineService {

    private val _medicines = MutableStateFlow<List<Medicine>>(MockData.sampleMedicines)
    val medicines: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    fun getAllMedicines(): List<Medicine> = _medicines.value

    fun getMedicineById(id: String): Medicine? {
        return _medicines.value.find { it.id == id }
    }

    fun searchMedicines(query: String, category: String = "All"): List<Medicine> {
        val q = query.trim().lowercase()
        return _medicines.value.filter { med ->
            val matchesCategory = category == "All" || med.category.equals(category, ignoreCase = true)
            val matchesQuery = q.isEmpty() ||
                    med.name.lowercase().contains(q) ||
                    med.genericName.lowercase().contains(q) ||
                    med.category.lowercase().contains(q)
            matchesCategory && matchesQuery
        }
    }

    fun getCategories(): List<String> = listOf("All") + MockData.sampleCategories
}
