package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HistoricalStockPoint
import com.example.ui.theme.MediBluePrimary

@Composable
fun StockTrendChart(
    points: List<HistoricalStockPoint>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) return

    val maxVal = points.maxOfOrNull { it.stockLevel }?.coerceAtLeast(10) ?: 100

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊 Historical Stock Level & Trend",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MediBluePrimary))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Actual", fontSize = 10.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFDC2626)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Forecast", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bar Chart Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            points.forEach { point ->
                val barRatio = (point.stockLevel.toFloat() / maxVal.toFloat()).coerceIn(0.05f, 1.0f)
                val barColor = if (point.isForecast) Color(0xFFEF4444) else MediBluePrimary

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${point.stockLevel}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (point.isForecast) Color(0xFFB91C1C) else MediBluePrimary
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .fillMaxHeight(barRatio)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(barColor)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = point.dateLabel,
                        fontSize = 8.sp,
                        maxLines = 1,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
