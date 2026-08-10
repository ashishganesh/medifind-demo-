package com.example.ui.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockData
import com.example.model.Medicine
import com.example.model.UserLocation
import com.example.ui.components.LocationSelectorBar
import com.example.ui.components.MedicineCard
import com.example.ui.components.SearchBar
import com.example.ui.theme.MediBluePrimary

@Composable
fun SearchMedicineScreen(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    maxDistanceKm: Double,
    onDistanceChange: (Double) -> Unit,
    availabilityFilter: String,
    onAvailabilityFilterChange: (String) -> Unit,
    userLocation: UserLocation,
    onChangeLocationClick: () -> Unit,
    onSearchSubmit: () -> Unit,
    onMedicineClick: (Medicine) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location Bar
        item {
            LocationSelectorBar(
                userLocation = userLocation,
                onChangeLocationClick = onChangeLocationClick
            )
        }

        item {
            Text(
                text = "Search Medicine Availability",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Enter brand or generic name to find stocking pharmacies nearby.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = onQueryChange,
                placeholder = "e.g. Paracetamol, Amoxicillin, Insulin...",
                onSearch = onSearchSubmit
            )
        }

        // Search Action Button
        item {
            Button(
                onClick = onSearchSubmit,
                colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search Stock in Nearby Pharmacies", fontWeight = FontWeight.Bold)
            }
        }

        // Distance & Availability Filters
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Search Radius & Filters",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Distance Radius Filter
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Max Distance Radius:", fontSize = 13.sp)
                            Text(
                                text = if (maxDistanceKm >= 999.0) "All (Unlimited)" else "${maxDistanceKm.toInt()} km",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MediBluePrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val radiusOpts = listOf(
                                1.0 to "1 km",
                                2.0 to "2 km",
                                5.0 to "5 km",
                                10.0 to "10 km",
                                999.0 to "All Areas"
                            )
                            items(radiusOpts) { (dist, label) ->
                                FilterChip(
                                    selected = maxDistanceKm == dist,
                                    onClick = { onDistanceChange(dist) },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Availability Status Filter
                    Column {
                        Text("Availability Status:", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("All", "Available", "Low Stock").forEach { filter ->
                                FilterChip(
                                    selected = availabilityFilter == filter,
                                    onClick = { onAvailabilityFilterChange(filter) },
                                    label = { Text(filter, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    // Medicine Category Filter
                    Column {
                        Text("Category:", fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                FilterChip(
                                    selected = selectedCategory == "All",
                                    onClick = { onCategoryChange("All") },
                                    label = { Text("All Categories", fontSize = 11.sp) }
                                )
                            }
                            items(MockData.sampleCategories) { category ->
                                FilterChip(
                                    selected = selectedCategory == category,
                                    onClick = { onCategoryChange(category) },
                                    label = { Text(category, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Popular Medicines Quick Search Section
        item {
            Text(
                text = "Popular Medicines in ${userLocation.cityName}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(MockData.sampleMedicines.take(4)) { medicine ->
            MedicineCard(
                medicine = medicine,
                onClick = { onMedicineClick(medicine) }
            )
        }
    }
}
