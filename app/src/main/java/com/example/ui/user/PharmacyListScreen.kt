package com.example.ui.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Pharmacy
import com.example.ui.components.PharmacyCard
import com.example.ui.components.SearchBar

@Composable
fun PharmacyListScreen(
    pharmacies: List<Pharmacy>,
    onPharmacyClick: (String) -> Unit,
    onDirectionsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedFacilityType by remember { mutableStateOf("All") }

    val filteredPharmacies = remember(pharmacies, query, selectedFacilityType) {
        pharmacies.filter { p ->
            (query.isBlank() || p.name.contains(query, ignoreCase = true) || p.address.contains(query, ignoreCase = true)) &&
            (selectedFacilityType == "All" || p.facilityType == selectedFacilityType)
        }.sortedBy { it.distanceKm }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nearby Verified Pharmacies & Facilities",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Explore dispensaries, retail chemists, and Jan Aushadhi Kendras in Lucknow.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                placeholder = "Search pharmacy name or location..."
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedFacilityType == "All",
                        onClick = { selectedFacilityType = "All" },
                        label = { Text("All Types", fontSize = 11.sp) }
                    )
                }
                listOf("Retail Pharmacy", "Jan Aushadhi Kendra", "Govt Hospital Pharmacy", "Retail Chain").forEach { type ->
                    item {
                        FilterChip(
                            selected = selectedFacilityType == type,
                            onClick = { selectedFacilityType = type },
                            label = { Text(type, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        items(filteredPharmacies) { pharmacy ->
            PharmacyCard(
                pharmacy = pharmacy,
                onDetailsClick = { onPharmacyClick(pharmacy.id) },
                onDirectionsClick = { onDirectionsClick(pharmacy.id) }
            )
        }
    }
}
