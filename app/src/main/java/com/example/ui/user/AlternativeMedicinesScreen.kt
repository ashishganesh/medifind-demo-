package com.example.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlternativeWithAvailability
import com.example.model.Medicine
import com.example.ui.components.AlternativeSafetyNotice
import com.example.ui.components.EmptyState
import com.example.ui.components.VerifiedAlternativeCard
import com.example.ui.theme.MediBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlternativeMedicinesScreen(
    sourceMedicine: Medicine?,
    alternativesWithAvailability: List<AlternativeWithAvailability>,
    onBackClick: () -> Unit,
    onViewMedicineDetails: (String) -> Unit,
    onViewPharmaciesClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verified Alternatives", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Requested Medicine Header Card
            item {
                if (sourceMedicine != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Requested Medicine:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Medication, contentDescription = null, tint = MediBluePrimary)
                                Text(
                                    text = sourceMedicine.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Text(
                                text = "Active Ingredient: ${sourceMedicine.genericName} • ${sourceMedicine.strength}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Safety Notice
            item {
                AlternativeSafetyNotice()
            }

            // Results Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Verified Options (${alternativesWithAvailability.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Verified Only ✓",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (alternativesWithAvailability.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Verified Alternatives Available",
                        message = "No medically verified alternatives are currently cataloged for '${sourceMedicine?.name ?: "this medicine"}'. Consult a registered pharmacist or physician for assistance."
                    )
                }
            } else {
                items(alternativesWithAvailability) { alt ->
                    VerifiedAlternativeCard(
                        alternative = alt,
                        onViewMedicineDetails = onViewMedicineDetails,
                        onViewPharmaciesClick = onViewPharmaciesClick
                    )
                }
            }
        }
    }
}
