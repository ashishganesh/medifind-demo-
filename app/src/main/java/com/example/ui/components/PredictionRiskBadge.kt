package com.example.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StockRiskLevel

@Composable
fun PredictionRiskBadge(
    riskLevel: StockRiskLevel,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, labelText) = when (riskLevel) {
        StockRiskLevel.CRITICAL -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "🔴 CRITICAL RISK")
        StockRiskLevel.HIGH -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "🟠 HIGH RISK")
        StockRiskLevel.MEDIUM -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "🟡 MEDIUM RISK")
        StockRiskLevel.LOW -> Triple(Color(0xFFDCFCE7), Color(0xFF15803D), "🟢 LOW RISK")
        StockRiskLevel.UNKNOWN -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "⚪ INSUFFICIENT DATA")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = labelText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}
