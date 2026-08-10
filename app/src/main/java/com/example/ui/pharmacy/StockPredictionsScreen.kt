package com.example.ui.pharmacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InventoryRecord
import com.example.model.Pharmacy
import com.example.model.StockPrediction
import com.example.model.StockRiskLevel
import com.example.ui.components.EmptyState
import com.example.ui.components.SearchBar
import com.example.ui.components.StockPredictionCard
import com.example.ui.theme.MediBlueDark
import com.example.ui.theme.MediBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockPredictionsScreen(
    pharmacy: Pharmacy,
    predictions: List<StockPrediction>,
    onBackClick: () -> Unit,
    onEditStockClick: (String) -> Unit, // inventoryId
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val myPredictions = predictions.filter { it.pharmacyId == pharmacy.id }

    val criticalCount = myPredictions.count { it.riskLevel == StockRiskLevel.CRITICAL }
    val highCount = myPredictions.count { it.riskLevel == StockRiskLevel.HIGH }
    val mediumCount = myPredictions.count { it.riskLevel == StockRiskLevel.MEDIUM }
    val lowCount = myPredictions.count { it.riskLevel == StockRiskLevel.LOW }
    val unknownCount = myPredictions.count { it.riskLevel == StockRiskLevel.UNKNOWN }

    val filteredPredictions = myPredictions.filter { pred ->
        val matchesSearch = searchQuery.isBlank() ||
                pred.medicineName.contains(searchQuery, ignoreCase = true) ||
                pred.genericName.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Critical" -> pred.riskLevel == StockRiskLevel.CRITICAL
            "High" -> pred.riskLevel == StockRiskLevel.HIGH
            "Medium" -> pred.riskLevel == StockRiskLevel.MEDIUM
            "Low" -> pred.riskLevel == StockRiskLevel.LOW
            "Insufficient Data" -> pred.riskLevel == StockRiskLevel.UNKNOWN
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Stock-Out Forecasting Engine",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pharmacy.name,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Banner
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
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = null,
                                tint = Color(0xFF60A5FA),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Predictive Inventory Analytics",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Calculated from historical stock decreases to forecast estimated stock-out dates. Does not claim certainty.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Risk Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(color = Color(0xFF991B1B), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "🔴 $criticalCount Critical",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(color = Color(0xFFC2410C), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "🟠 $highCount High",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(color = Color(0xFFB45309), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "🟡 $mediumCount Medium",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(color = Color(0xFF15803D), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "🟢 $lowCount Low",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search medicine forecasts...",
                    onSearch = {}
                )
            }

            // Risk Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        "All" to "All (${myPredictions.size})",
                        "Critical" to "🔴 Critical ($criticalCount)",
                        "High" to "🟠 High ($highCount)",
                        "Medium" to "🟡 Medium ($mediumCount)",
                        "Low" to "🟢 Low ($lowCount)",
                        "Insufficient Data" to "⚪ Insufficient ($unknownCount)"
                    )
                    items(filterOptions) { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Predictions List
            if (filteredPredictions.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Forecasts Found",
                        message = "No medicine predictions match the current search or risk filters.",
                        onReset = {
                            searchQuery = ""
                            selectedFilter = "All"
                        }
                    )
                }
            } else {
                items(filteredPredictions) { pred ->
                    StockPredictionCard(
                        prediction = pred,
                        onEditStockClick = { onEditStockClick(pred.inventoryId) }
                    )
                }
            }
        }
    }
}
