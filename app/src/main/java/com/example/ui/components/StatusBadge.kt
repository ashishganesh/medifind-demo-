package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailabilityStatus
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    status: AvailabilityStatus,
    modifier: Modifier = Modifier,
    stockCount: Int? = null
) {
    val (bgColor, textColor, icon) = when (status) {
        AvailabilityStatus.AVAILABLE -> Triple(
            StatusAvailableBg,
            StatusAvailableGreen,
            Icons.Default.CheckCircle
        )
        AvailabilityStatus.LOW_STOCK -> Triple(
            StatusLowStockBg,
            StatusLowStockAmber,
            Icons.Default.Warning
        )
        AvailabilityStatus.OUT_OF_STOCK -> Triple(
            StatusOutStockBg,
            StatusOutStockRed,
            Icons.Default.Error
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.label,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            val text = if (stockCount != null) {
                if (status == AvailabilityStatus.OUT_OF_STOCK) "Out of Stock"
                else "${status.label} ($stockCount)"
            } else {
                status.label
            }
            Text(
                text = text,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
