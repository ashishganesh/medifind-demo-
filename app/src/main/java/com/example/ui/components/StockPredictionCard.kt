package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DemandPattern
import com.example.model.StockPrediction
import com.example.model.StockRiskLevel
import com.example.ui.theme.MediBluePrimary

@Composable
fun StockPredictionCard(
    prediction: StockPrediction,
    onEditStockClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showChart by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Medicine Name + Risk Badge
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
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MediBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = prediction.medicineName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${prediction.genericName} • ${prediction.category}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                PredictionRiskBadge(riskLevel = prediction.riskLevel)
            }

            HorizontalDivider(color = Color(0xFFE2E8F0))

            // Key Metrics Grid (4 Column Tiles)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Current Stock
                Column {
                    Text("Current Stock", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${prediction.currentStock} units",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (prediction.currentStock == 0) Color(0xFFDC2626) else MediBluePrimary
                    )
                }

                // Metric 2: Daily Demand
                Column {
                    Text("Daily Demand", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = if (prediction.averageDailyConsumption > 0) "${prediction.averageDailyConsumption}/day" else "None",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Metric 3: Est. Days Remaining
                Column {
                    Text("Days Left", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = prediction.estimatedDaysRemaining?.let { "~${it} days" } ?: "N/A",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (prediction.riskLevel) {
                            StockRiskLevel.CRITICAL -> Color(0xFFDC2626)
                            StockRiskLevel.HIGH -> Color(0xFFEA580C)
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }

                // Metric 4: Est. Stock-Out Date
                Column(horizontalAlignment = Alignment.End) {
                    Text("Est. Stock-Out", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = prediction.estimatedStockOutDate,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MediBluePrimary
                    )
                }
            }

            // Status Indicators Row (Reliability & Demand Pattern)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Data: ${prediction.forecastReliability.label}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = if (prediction.demandPattern == DemandPattern.VARIABLE) Color(0xFFFFEDD5) else Color(0xFFE0F2FE),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = prediction.demandPattern.label,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (prediction.demandPattern == DemandPattern.VARIABLE) Color(0xFFC2410C) else Color(0xFF0369A1),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Recommendation Banner
            Surface(
                color = when (prediction.riskLevel) {
                    StockRiskLevel.CRITICAL -> Color(0xFFFEF2F2)
                    StockRiskLevel.HIGH -> Color(0xFFFFF7ED)
                    StockRiskLevel.MEDIUM -> Color(0xFFFEFCE8)
                    else -> Color(0xFFF0FDF4)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Recommendation",
                        tint = when (prediction.riskLevel) {
                            StockRiskLevel.CRITICAL -> Color(0xFFDC2626)
                            StockRiskLevel.HIGH -> Color(0xFFEA580C)
                            StockRiskLevel.MEDIUM -> Color(0xFFD97706)
                            else -> Color(0xFF16A34A)
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = prediction.recommendation,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Interactive Buttons: Chart Toggle & Stock Update
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showChart = !showChart },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = "Chart",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showChart) "Hide Stock Chart" else "Show Historical Chart", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onEditStockClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("Update Stock", fontSize = 11.sp)
                }
            }

            // Embedded Chart if toggled
            AnimatedVisibility(visible = showChart) {
                StockTrendChart(points = prediction.historicalPoints)
            }

            // Expandable Explainability Drawer
            ForecastExplanation(prediction = prediction)
        }
    }
}
