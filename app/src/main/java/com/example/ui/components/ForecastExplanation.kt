package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StockPrediction
import com.example.ui.theme.MediBluePrimary

@Composable
fun ForecastExplanation(
    prediction: StockPrediction,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Explainability",
                        tint = MediBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "How was this stock-out forecast calculated?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MediBluePrimary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MediBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFCBD5E1))

                    Text(
                        text = "1. Observed Inventory History",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "• Observed Period: ${prediction.observationDays} days of audit logs\n" +
                                "• Total Stock Consumption: ${prediction.totalConsumptionUnits} units (decrease events only)\n" +
                                "• Restock Events Detected: ${prediction.restockEventsCount} (isolated from demand calculation)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "2. Average Daily Demand Formula",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${prediction.totalConsumptionUnits} units consumed ÷ ${prediction.observationDays} days = ${prediction.averageDailyConsumption} units/day",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MediBluePrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Text(
                        text = "3. Days Remaining Formula",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val remainingText = if (prediction.estimatedDaysRemaining != null) {
                            "Current Stock (${prediction.currentStock}) ÷ Daily Demand (${prediction.averageDailyConsumption}) = ~${prediction.estimatedDaysRemaining} days remaining"
                        } else {
                            "Current Stock: ${prediction.currentStock} • Daily Demand: ${prediction.averageDailyConsumption} units/day"
                        }
                        Text(
                            text = remainingText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MediBluePrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    if (prediction.variabilityWarning != null) {
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "⚠️ ${prediction.variabilityWarning}",
                                fontSize = 11.sp,
                                color = Color(0xFFC2410C),
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Text(
                        text = "Note: This decision-support forecast is estimated from historical stock changes. It does not guarantee exact stock exhaustion dates.",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
