package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VerificationStatus

@Composable
fun AlternativeVerificationBadge(
    status: VerificationStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, icon, label) = when (status) {
        VerificationStatus.VERIFIED -> Quadruple(
            Color(0xFFDCFCE7),
            Color(0xFF15803D),
            Icons.Default.CheckCircle,
            "Verified Alternative ✓"
        )
        VerificationStatus.PENDING -> Quadruple(
            Color(0xFFFEF3C7),
            Color(0xFFB45309),
            Icons.Default.HourglassEmpty,
            "Pending Review"
        )
        VerificationStatus.REJECTED -> Quadruple(
            Color(0xFFFEE2E2),
            Color(0xFFB91C1C),
            Icons.Default.Cancel,
            "Rejected"
        )
        VerificationStatus.INACTIVE -> Quadruple(
            Color(0xFFF1F5F9),
            Color(0xFF64748B),
            Icons.Default.Block,
            "Inactive"
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
