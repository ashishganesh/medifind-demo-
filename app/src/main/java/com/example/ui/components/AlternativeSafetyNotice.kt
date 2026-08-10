package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlternativeSafetyNotice(
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFFEF3C7),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Medical Safety Notice",
                tint = Color(0xFFB45309),
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = "Medical Safety Notice",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF92400E)
                )
                Text(
                    text = "Verified alternative information is shown for reference. Confirm suitability with a qualified pharmacist or doctor before use.",
                    fontSize = 11.sp,
                    color = Color(0xFF78350F),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
