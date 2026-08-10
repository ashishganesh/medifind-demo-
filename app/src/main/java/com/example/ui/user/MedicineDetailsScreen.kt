package com.example.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlternativeWithAvailability
import com.example.model.AvailabilityStatus
import com.example.model.InventoryRecord
import com.example.model.Medicine
import com.example.model.Pharmacy
import com.example.ui.components.PharmacyCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.MediBluePrimary

@Composable
fun MedicineDetailsScreen(
    medicine: Medicine,
    inventoryRecords: List<InventoryRecord>,
    pharmacies: List<Pharmacy>,
    onPharmacyClick: (String) -> Unit,
    onDirectionsClick: (String) -> Unit,
    onViewVerifiedAlternativesClick: ((String) -> Unit)? = null,
    verifiedAlternativesList: List<AlternativeWithAvailability> = emptyList(),
    modifier: Modifier = Modifier
) {
    val availableCount = inventoryRecords.count { it.status == AvailabilityStatus.AVAILABLE }
    val lowStockCount = inventoryRecords.count { it.status == AvailabilityStatus.LOW_STOCK }
    val outOfStockCount = inventoryRecords.count { it.status == AvailabilityStatus.OUT_OF_STOCK }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Medicine Overview Header Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Medication,
                                    contentDescription = medicine.name,
                                    tint = MediBluePrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = medicine.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Generic: ${medicine.genericName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(
                            onClick = {},
                            label = { Text("Form: ${medicine.form}") }
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text("Strength: ${medicine.strength}") }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = medicine.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Dosage Info: ${medicine.dosageInfo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Availability Summary Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Availability Summary Nearby",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "Available: $availableCount",
                                color = Color(0xFF15803D),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "Low Stock: $lowStockCount",
                                color = Color(0xFFB45309),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "Unavailable: $outOfStockCount",
                                color = Color(0xFFB91C1C),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Alternative / Similar Medicines Section
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Alternatives",
                            tint = MediBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Alternative / Similar Brand Formulations",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "Same active ingredient (${medicine.genericName}):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        medicine.genericAlternatives.forEach { alt ->
                            SuggestionChip(
                                onClick = {},
                                label = { Text(alt, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                            )
                        }
                    }

                    if (onViewVerifiedAlternativesClick != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { onViewVerifiedAlternativesClick(medicine.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MediBluePrimary)
                        ) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View Verified Alternatives System", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Prominent Safety Disclaimer
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Safety Disclaimer",
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Medicine alternatives should be confirmed by a qualified doctor or pharmacist. MediFind does not independently prescribe medicines.",
                                fontSize = 11.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Stocked Pharmacies Section
        item {
            Text(
                text = "Nearby Pharmacies Carrying Stock",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        val matchingPharmacies = pharmacies.filter { p ->
            inventoryRecords.any { inv -> inv.pharmacyId == p.id }
        }

        items(matchingPharmacies) { pharmacy ->
            val record = inventoryRecords.find { it.pharmacyId == pharmacy.id }
            if (record != null) {
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = pharmacy.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${pharmacy.distanceKm} km away • ${pharmacy.address}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            StatusBadge(status = record.status, stockCount = record.stockCount)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onPharmacyClick(pharmacy.id) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Pharmacy Profile", fontSize = 12.sp)
                            }
                            Button(
                                onClick = { onDirectionsClick(pharmacy.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary)
                            ) {
                                Text("Directions", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
