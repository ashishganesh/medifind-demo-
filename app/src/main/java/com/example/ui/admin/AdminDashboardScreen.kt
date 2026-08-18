package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.data.MockData
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    onNavigatePharmacies: () -> Unit,
    onNavigateInventory: () -> Unit,
    onNavigateAnalytics: () -> Unit,
    onNavigateAlternatives: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val analytics = MockData.sampleAnalytics

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Portal Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
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
                                text = "STATE HEALTH MONITORING PORTAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MediBluePrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Uttar Pradesh Health Dept",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                            Text(
                                text = "Lucknow District Command Center",
                                fontSize = 12.sp,
                                color = Color(0xFF6B7280)
                            )
                        }

                        Surface(
                            color = MediBluePrimary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "System Admin",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Overview Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Registered Pharmacies",
                        value = analytics.totalPharmaciesTracked.toString(),
                        subtitle = "${analytics.activePharmacies24h} active in 24h",
                        icon = Icons.Default.Storefront,
                        iconBgColor = MediBlueContainer,
                        iconColor = MediBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Medicines Tracked",
                        value = analytics.totalMedicinesCataloged.toString(),
                        subtitle = "Active catalog lines",
                        icon = Icons.Default.Medication,
                        iconBgColor = MediTealContainer,
                        iconColor = MediTeal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Shortage Alerts",
                        value = analytics.activeShortagesCount.toString(),
                        subtitle = "Critical low stock items",
                        icon = Icons.Default.Warning,
                        iconBgColor = StatusOutStockBg,
                        iconColor = StatusOutStockRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "District Index",
                        value = "86.4%",
                        subtitle = "Avg availability score",
                        icon = Icons.Default.TrendingUp,
                        iconBgColor = StatusAvailableBg,
                        iconColor = StatusAvailableGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Navigation Shortcuts
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigatePharmacies,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Pharmacies", fontSize = 12.sp, maxLines = 1, softWrap = false)
                    }
                    OutlinedButton(
                        onClick = onNavigateInventory,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Inventory", fontSize = 12.sp, maxLines = 1, softWrap = false)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onNavigateAlternatives != null) {
                        OutlinedButton(
                            onClick = onNavigateAlternatives,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Alternatives", fontSize = 12.sp, maxLines = 1, softWrap = false)
                        }
                    }
                    Button(
                        onClick = onNavigateAnalytics,
                        colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Analytics", fontSize = 12.sp, maxLines = 1, softWrap = false)
                    }
                }
            }
        }

        // District Availability Progress Overview
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "District Availability Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    analytics.districtData.forEach { district ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(district.districtName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("${district.availabilityPercent}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MediBluePrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { district.availabilityPercent / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = if (district.availabilityPercent > 80) StatusAvailableGreen else StatusLowStockAmber,
                                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
