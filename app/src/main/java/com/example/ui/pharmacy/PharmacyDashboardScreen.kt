package com.example.ui.pharmacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailabilityStatus
import com.example.model.InventoryRecord
import com.example.model.Medicine
import com.example.model.Pharmacy
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*

@Composable
fun PharmacyDashboardScreen(
    pharmacy: Pharmacy,
    inventoryList: List<InventoryRecord>,
    medicines: List<Medicine>,
    predictions: List<com.example.model.StockPrediction> = emptyList(),
    onOpenInventoryClick: () -> Unit,
    onViewPredictionsClick: () -> Unit = {},
    onEditStockClick: (InventoryRecord) -> Unit,
    modifier: Modifier = Modifier
) {
    val myInventory = inventoryList.filter { it.pharmacyId == pharmacy.id }
    val totalCount = myInventory.size
    val availableCount = myInventory.count { it.status == AvailabilityStatus.AVAILABLE }
    val lowStockCount = myInventory.count { it.status == AvailabilityStatus.LOW_STOCK }
    val outStockCount = myInventory.count { it.status == AvailabilityStatus.OUT_OF_STOCK }

    val myPredictions = predictions.filter { it.pharmacyId == pharmacy.id }
    val criticalPredCount = myPredictions.count { it.riskLevel == com.example.model.StockRiskLevel.CRITICAL }
    val highPredCount = myPredictions.count { it.riskLevel == com.example.model.StockRiskLevel.HIGH }
    val mediumPredCount = myPredictions.count { it.riskLevel == com.example.model.StockRiskLevel.MEDIUM }
    val lowPredCount = myPredictions.count { it.riskLevel == com.example.model.StockRiskLevel.LOW }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Pending Verification Banner if not verified
        if (!pharmacy.isVerified) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PendingActions,
                            contentDescription = "Pending Verification",
                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "Pending Verification",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "Your pharmacy registration has been submitted and is currently under review by the State Health Department before public directory inclusion.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Welcome Header Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MediBlueDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Pharmacy Portal Dashboard",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = pharmacy.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (pharmacy.isVerified) "License Reg: UP-LKO-88214 • Verified Facility" else "License Reg: Pending Approval",
                                fontSize = 11.sp,
                                color = Color(0xFF93C5FD)
                            )
                        }

                        Surface(
                            color = if (pharmacy.isVerified) Color(0xFF15803D) else Color(0xFFD97706),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (pharmacy.isVerified) Icons.Default.Verified else Icons.Default.HourglassTop,
                                    contentDescription = "Status",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (pharmacy.isVerified) "Active Online" else "Pending Verification",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Cataloged Items",
                        value = totalCount.toString(),
                        subtitle = "Tracked in catalog",
                        icon = Icons.Default.Inventory2,
                        iconBgColor = MediBlueContainer,
                        iconColor = MediBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Available Stock",
                        value = availableCount.toString(),
                        subtitle = "Healthy inventory",
                        icon = Icons.Default.CheckCircle,
                        iconBgColor = StatusAvailableBg,
                        iconColor = StatusAvailableGreen,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Low Stock Alert",
                        value = lowStockCount.toString(),
                        subtitle = "< 10 units remaining",
                        icon = Icons.Default.Warning,
                        iconBgColor = StatusLowStockBg,
                        iconColor = StatusLowStockAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Out of Stock",
                        value = outStockCount.toString(),
                        subtitle = "Critical restock needed",
                        icon = Icons.Default.Error,
                        iconBgColor = StatusOutStockBg,
                        iconColor = StatusOutStockRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Stock Intelligence & Forecasting Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = null,
                                tint = MediBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Stock Intelligence & Forecasting",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Engine Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Demand rate calculated from historical stock change logs.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(color = Color(0xFFFEF2F2), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "🔴 $criticalPredCount Critical",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(color = Color(0xFFFFF7ED), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "🟠 $highPredCount High",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(color = Color(0xFFFEFCE8), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "🟡 $mediumPredCount Medium",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(color = Color(0xFFF0FDF4), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "🟢 $lowPredCount Low",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Button(
                        onClick = onViewPredictionsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Forecasting & Stock-Out Engine", fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, softWrap = false)
                    }
                }
            }
        }

        // Inventory Management Overview Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Inventory Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onOpenInventoryClick) {
                    Text("Full Inventory Page")
                }
            }
        }

        items(myInventory.take(6)) { record ->
            val med = medicines.find { it.id == record.medicineId }
            if (med != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = med.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Current Units: ${record.stockCount} • ₹${record.unitPriceRupees}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusBadge(status = record.status, stockCount = record.stockCount)

                            OutlinedButton(
                                onClick = { onEditStockClick(record) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Update", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
