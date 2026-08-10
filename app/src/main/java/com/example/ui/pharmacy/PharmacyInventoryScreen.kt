package com.example.ui.pharmacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailabilityLog
import com.example.model.AvailabilityStatus
import com.example.model.InventoryRecord
import com.example.model.Medicine
import com.example.model.Pharmacy
import com.example.ui.components.AddInventoryDialog
import com.example.ui.components.InventoryHistoryDialog
import com.example.ui.components.SearchBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.MediBluePrimary

@Composable
fun PharmacyInventoryScreen(
    pharmacy: Pharmacy,
    inventoryList: List<InventoryRecord>,
    medicines: List<Medicine>,
    availabilityLogs: List<AvailabilityLog> = emptyList(),
    onEditStockClick: (InventoryRecord) -> Unit,
    onAddMedicineClick: (medicineId: String, stockCount: Int, unitPrice: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    var showAddDialog by remember { mutableStateOf(false) }
    var viewingHistoryRecord by remember { mutableStateOf<InventoryRecord?>(null) }

    val myInventory = remember(inventoryList, pharmacy) {
        inventoryList.filter { it.pharmacyId == pharmacy.id }
    }

    val filteredRecords = remember(myInventory, query, selectedFilter, medicines) {
        myInventory.filter { record ->
            val med = medicines.find { it.id == record.medicineId }
            val matchesQuery = query.isBlank() || (med != null && (
                med.name.contains(query, ignoreCase = true) ||
                med.genericName.contains(query, ignoreCase = true) ||
                med.category.contains(query, ignoreCase = true)
            ))
            val matchesFilter = when (selectedFilter) {
                "Available" -> record.status == AvailabilityStatus.AVAILABLE
                "Low Stock" -> record.status == AvailabilityStatus.LOW_STOCK
                "Out of Stock" -> record.status == AvailabilityStatus.OUT_OF_STOCK
                else -> true
            }
            matchesQuery && matchesFilter
        }
    }

    if (showAddDialog) {
        AddInventoryDialog(
            medicines = medicines,
            onDismiss = { showAddDialog = false },
            onConfirm = { medId, stock, price ->
                onAddMedicineClick(medId, stock, price)
                showAddDialog = false
            }
        )
    }

    viewingHistoryRecord?.let { record ->
        val med = medicines.find { it.id == record.medicineId }
        if (med != null) {
            val itemLogs = availabilityLogs.filter { it.inventoryId == record.id }
            InventoryHistoryDialog(
                medicine = med,
                logs = itemLogs,
                onDismiss = { viewingHistoryRecord = null }
            )
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pharmacy Stock Inventory Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Update medicine quantities in real-time. Changes immediately sync to public patient discovery.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Medicine", fontSize = 12.sp)
                }
            }
        }

        item {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Filter inventory by medicine or generic name..."
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Available", "Low Stock", "Out of Stock").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter, fontSize = 11.sp) }
                    )
                }
            }
        }

        if (filteredRecords.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No medicines found in inventory.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No items match your query or filter criteria. Click 'Add Medicine' above to add items to your store.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(filteredRecords) { record ->
            val med = medicines.find { it.id == record.medicineId }
            if (med != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = med.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "Generic: ${med.genericName} • ${med.strength}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            StatusBadge(status = record.status, stockCount = record.stockCount)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Unit Price: ₹${record.unitPriceRupees} • Updated: ${record.lastUpdated}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { viewingHistoryRecord = record },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "Audit History",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("History", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onEditStockClick(record) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Stock",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Edit Stock", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
