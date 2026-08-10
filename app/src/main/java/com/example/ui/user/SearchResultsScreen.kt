package com.example.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserLocation
import com.example.ui.components.EmptyState
import com.example.ui.components.LocationSelectorBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.MediBluePrimary
import com.example.utils.DistanceUtils
import com.example.viewmodel.PharmacySearchResult

@Composable
fun SearchResultsScreen(
    query: String,
    results: List<PharmacySearchResult>,
    userLocation: UserLocation,
    maxDistanceKm: Double,
    selectedSortBy: String,
    onChangeLocationClick: () -> Unit,
    onDistanceChange: (Double) -> Unit,
    onSortChange: (String) -> Unit,
    onPharmacyClick: (String) -> Unit,
    onDirectionsClick: (Double, Double, String) -> Unit,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Location Selector Bar
        item {
            LocationSelectorBar(
                userLocation = userLocation,
                onChangeLocationClick = onChangeLocationClick
            )
        }

        // Result Summary Header
        item {
            Column {
                Text(
                    text = "Nearby Medicine Availability",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (query.isBlank()) "All Medicines" else query,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "• ${results.size} pharmacies stocking",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Search Radius Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Search Radius Filter:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val radiusOptions = listOf(
                        1.0 to "1 km",
                        2.0 to "2 km",
                        5.0 to "5 km",
                        10.0 to "10 km",
                        999.0 to "All (Unlimited)"
                    )
                    items(radiusOptions) { (dist, label) ->
                        FilterChip(
                            selected = maxDistanceKm == dist,
                            onClick = { onDistanceChange(dist) },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Map Area Placeholder
        item {
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = "Map View",
                                tint = MediBluePrimary
                            )
                            Text(
                                text = "Proximity Radar View",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Text(
                            text = "📍 ${userLocation.areaName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MediBluePrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Radar Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // User location pulse
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(MediBluePrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Your Location",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Pins
                        Box(
                            modifier = Modifier
                                .offset(x = (-60).dp, y = (-20).dp)
                                .background(Color(0xFF15803D), CircleShape)
                                .size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("1", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .offset(x = (60).dp, y = (25).dp)
                                .background(Color(0xFF15803D), CircleShape)
                                .size(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Haversine Proximity calculated from ${userLocation.areaName}",
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }

        // Sort Options
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text("Sort Results:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Nearest", "Availability", "Recently Updated").forEach { opt ->
                        FilterChip(
                            selected = selectedSortBy == opt,
                            onClick = { onSortChange(opt) },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Result Pharmacy Cards
        if (results.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EmptyState(
                        title = "No Pharmacies Found Within ${maxDistanceKm.toInt()} km",
                        message = "No stocking pharmacies were found within ${if (maxDistanceKm >= 999.0) "all areas" else "${maxDistanceKm.toInt()} km"} of ${userLocation.areaName}.",
                        onReset = onResetFilters
                    )
                    if (maxDistanceKm < 10.0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onDistanceChange(10.0) },
                            colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Expand Search Radius to 10 km")
                        }
                    }
                }
            }
        } else {
            items(results) { result ->
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = MediBluePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = result.pharmacy.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = result.pharmacy.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }

                            StatusBadge(
                                status = result.inventory.status,
                                stockCount = result.inventory.stockCount
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = result.medicine.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MediBluePrimary
                                )
                                Text(
                                    text = "₹${result.inventory.unitPriceRupees} / unit • Stock: ${result.inventory.stockCount}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${DistanceUtils.formatDistance(result.pharmacy.distanceKm)} away",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onPharmacyClick(result.pharmacy.id) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("View Store Inventory", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    onDirectionsClick(
                                        result.pharmacy.latitude,
                                        result.pharmacy.longitude,
                                        result.pharmacy.name
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Directions,
                                    contentDescription = "Directions",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Directions", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
