package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailabilityStatus
import com.example.model.InventoryRecord
import com.example.model.Medicine
import com.example.ui.components.SearchBar
import com.example.ui.theme.MediBluePrimary

@Composable
fun AdminInventoryScreen(
    medicines: List<Medicine>,
    inventoryList: List<InventoryRecord>,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    val filteredMedicines = remember(medicines, query) {
        medicines.filter {
            query.isBlank() || it.name.contains(query, ignoreCase = true) || it.genericName.contains(query, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Statewide Medicine Stock & Shortage Monitor",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Monitor district-wide stocking levels, shortage triggers, and emergency medicine availability.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Search medicine catalog..."
            )
        }

        items(filteredMedicines) { medicine ->
            val records = inventoryList.filter { it.medicineId == medicine.id }
            val availableStores = records.count { it.status == AvailabilityStatus.AVAILABLE }
            val lowStockStores = records.count { it.status == AvailabilityStatus.LOW_STOCK }
            val outOfStockStores = records.count { it.status == AvailabilityStatus.OUT_OF_STOCK }

            val isShortage = availableStores == 0 || (availableStores + lowStockStores) <= 1

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
                                text = medicine.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Generic: ${medicine.genericName} (${medicine.category})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isShortage) {
                            Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(12.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Shortage",
                                        tint = Color(0xFFB91C1C),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Critical Shortage",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB91C1C)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Available Stores: $availableStores", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                        Text("Low Stock: $lowStockStores", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                        Text("Out of Stock: $outOfStockStores", fontSize = 11.sp, color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
