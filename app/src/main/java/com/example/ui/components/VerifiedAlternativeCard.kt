package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Info
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
import com.example.ui.theme.MediBluePrimary
import com.example.utils.DistanceUtils

@Composable
fun VerifiedAlternativeCard(
    alternative: AlternativeWithAvailability,
    onViewMedicineDetails: (String) -> Unit,
    onViewPharmaciesClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val altMed = alternative.alternativeMedicine
    val mapping = alternative.mapping

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Title, Verification Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = altMed.name,
                                tint = MediBluePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = altMed.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Generic: ${altMed.genericName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AlternativeVerificationBadge(status = mapping.verificationStatus)
            }

            // Relationship Chip & Specs
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = mapping.relationshipType.displayName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                AssistChip(
                    onClick = {},
                    label = { Text("${altMed.strength} • ${altMed.form}", fontSize = 11.sp) }
                )
            }

            if (mapping.notes.isNotBlank()) {
                Text(
                    text = "Clinical note: ${mapping.notes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }

            HorizontalDivider()

            // Nearby Availability Status Box
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Availability",
                            tint = when (alternative.overallStatus) {
                                AvailabilityStatus.AVAILABLE -> Color(0xFF15803D)
                                AvailabilityStatus.LOW_STOCK -> Color(0xFFB45309)
                                AvailabilityStatus.OUT_OF_STOCK -> Color(0xFFB91C1C)
                            },
                            modifier = Modifier.size(18.dp)
                        )

                        Text(
                            text = when (alternative.overallStatus) {
                                AvailabilityStatus.AVAILABLE -> "Available Nearby"
                                AvailabilityStatus.LOW_STOCK -> "Low Stock Nearby"
                                AvailabilityStatus.OUT_OF_STOCK -> "Out of Stock Nearby"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = when (alternative.overallStatus) {
                                AvailabilityStatus.AVAILABLE -> Color(0xFF15803D)
                                AvailabilityStatus.LOW_STOCK -> Color(0xFFB45309)
                                AvailabilityStatus.OUT_OF_STOCK -> Color(0xFFB91C1C)
                            }
                        )
                    }

                    if (alternative.bestPharmacy != null && alternative.minDistanceKm != null) {
                        Text(
                            text = "Nearest: ${alternative.bestPharmacy.name} (${DistanceUtils.formatDistance(alternative.minDistanceKm)})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "No stocking pharmacies recorded nearby",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StatusBadge(
                    status = alternative.overallStatus,
                    stockCount = alternative.nearbyStockCount
                )
            }

            // Safety notice banner
            AlternativeSafetyNotice()

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onViewMedicineDetails(altMed.id) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Medicine Details", fontSize = 12.sp)
                }

                Button(
                    onClick = { onViewPharmaciesClick(altMed.id) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("View Pharmacies", fontSize = 12.sp)
                }
            }
        }
    }
}
