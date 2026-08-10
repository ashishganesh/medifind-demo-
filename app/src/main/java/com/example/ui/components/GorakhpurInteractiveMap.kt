package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvailabilityStatus
import com.example.model.InventoryRecord
import com.example.model.Pharmacy
import com.example.model.UserLocation
import com.example.ui.theme.*
import com.example.utils.DistanceUtils

@Composable
fun GorakhpurInteractiveMap(
    pharmacies: List<Pharmacy>,
    inventory: List<InventoryRecord> = emptyList(),
    selectedMedicineId: String? = null,
    selectedMedicineName: String? = null,
    userLocation: UserLocation,
    selectedPharmacyId: String? = null,
    onPharmacySelect: (String) -> Unit = {},
    onPharmacyDetailsClick: (String) -> Unit = {},
    onDirectionsClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var activeSelectedId by remember(selectedPharmacyId) { mutableStateOf(selectedPharmacyId) }
    var zoomLevel by remember { mutableFloatStateOf(1.0f) }

    // Center coordinates for Deen Dayal Upadhyaya Gorakhpur University (DDUGU), Civil Lines
    val centerLat = 26.7558
    val centerLon = 83.3735

    val selectedPharmacy = remember(pharmacies, activeSelectedId) {
        pharmacies.find { it.id == activeSelectedId }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Map Header Bar
            Surface(
                color = MediBlueContainer,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Map",
                                tint = MediBluePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "DDUGU Campus GIS Map",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MediBluePrimary
                            )
                        }
                        Text(
                            text = if (selectedMedicineName != null) "Stock for $selectedMedicineName near DDUGU" else "🎓 DDU Gorakhpur University (Civil Lines Area)",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }

                    // Map Zoom & Recenter Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            onClick = { zoomLevel = (zoomLevel * 1.25f).coerceAtMost(2.2f) },
                            color = Color.White,
                            shape = CircleShape,
                            shadowElevation = 1.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Zoom In",
                                tint = MediBluePrimary,
                                modifier = Modifier.padding(4.dp).size(16.dp)
                            )
                        }
                        Surface(
                            onClick = { zoomLevel = (zoomLevel / 1.25f).coerceAtLeast(0.7f) },
                            color = Color.White,
                            shape = CircleShape,
                            shadowElevation = 1.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Zoom Out",
                                tint = MediBluePrimary,
                                modifier = Modifier.padding(4.dp).size(16.dp)
                            )
                        }
                        Surface(
                            onClick = {
                                zoomLevel = 1.0f
                                activeSelectedId = null
                            },
                            color = Color.White,
                            shape = CircleShape,
                            shadowElevation = 1.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Reset Center to DDUGU",
                                tint = MediBluePrimary,
                                modifier = Modifier.padding(4.dp).size(16.dp)
                            )
                        }
                    }
                }
            }

            // Map View Canvas Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(Color(0xFFE0F2FE)) // GIS Blue Map Canvas Background
            ) {
                // Vector Roads & DDUGU Campus Boundary Canvas Representation
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val gridColor = Color(0xFFCBD5E1)
                    val strokeWidth = 1.dp.toPx()
                    val dashPath = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

                    for (i in 1..4) {
                        val y = canvasHeight * (i / 5f)
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(canvasWidth, y),
                            strokeWidth = strokeWidth,
                            pathEffect = dashPath
                        )
                    }

                    for (i in 1..4) {
                        val x = canvasWidth * (i / 5f)
                        drawLine(
                            color = gridColor,
                            start = Offset(x, 0f),
                            end = Offset(x, canvasHeight),
                            strokeWidth = strokeWidth,
                            pathEffect = dashPath
                        )
                    }

                    // University Road (Civil Lines - DDUGU Main Corridor)
                    drawLine(
                        color = Color(0xFF94A3B8),
                        start = Offset(canvasWidth * 0.05f, canvasHeight * 0.75f),
                        end = Offset(canvasWidth * 0.95f, canvasHeight * 0.25f),
                        strokeWidth = 5.dp.toPx()
                    )

                    // Town Hall Road / Buxipur Road Crossings
                    drawLine(
                        color = Color(0xFF94A3B8),
                        start = Offset(canvasWidth * 0.25f, canvasHeight * 0.15f),
                        end = Offset(canvasWidth * 0.75f, canvasHeight * 0.85f),
                        strokeWidth = 3.5.dp.toPx()
                    )

                    // DDUGU Campus Grounds Green Area Shader
                    drawCircle(
                        color = Color(0xFF10B981).copy(alpha = 0.15f),
                        center = Offset(canvasWidth * 0.5f, canvasHeight * 0.5f),
                        radius = 60.dp.toPx()
                    )
                }

                // Map Source Attribution & Watermark Tag
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "🗺️ OpenStreetMap DDUGU GIS",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Surface(
                        onClick = { zoomLevel = 1.0f },
                        color = Color.White.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reload Map Tiles",
                                tint = MediBluePrimary,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "Reload Map",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MediBluePrimary
                            )
                        }
                    }
                }

                // Central Reference Landmark Marker: 🎓 Deen Dayal Upadhyaya Gorakhpur University
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-10).dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = Color(0xFF4338CA), // Indigo University Branding Accent
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 5.dp,
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "🎓", fontSize = 11.sp)
                                Text(
                                    text = "DDUGU — Demo Reference Point",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4338CA), CircleShape)
                        )
                    }
                }

                // Interactive Pharmacy Pins placed relative to DDUGU Center
                pharmacies.forEachIndexed { index, pharmacy ->
                    val deltaLat = (pharmacy.latitude - centerLat)
                    val deltaLon = (pharmacy.longitude - centerLon)

                    val baseScale = 28000.0f * zoomLevel
                    val xPx = (deltaLon * baseScale).toFloat().coerceIn(-135f, 135f)
                    val yPx = (-deltaLat * baseScale).toFloat().coerceIn(-95f, 95f)

                    val isSelected = pharmacy.id == activeSelectedId

                    // Determine Medicine Availability Status for Marker Badge if selectedMedicineId present
                    val matchingRecord = if (selectedMedicineId != null) {
                        inventory.find { it.pharmacyId == pharmacy.id && it.medicineId == selectedMedicineId }
                    } else null

                    val markerColor = when {
                        matchingRecord?.status == AvailabilityStatus.AVAILABLE -> StatusAvailableGreen
                        matchingRecord?.status == AvailabilityStatus.LOW_STOCK -> StatusLowStockAmber
                        matchingRecord?.status == AvailabilityStatus.OUT_OF_STOCK -> StatusOutStockRed
                        pharmacy.isVerified -> MediBluePrimary
                        else -> Color(0xFF64748B) // Slate Gray for Map Listed Pharmacies
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(x = xPx.dp, y = yPx.dp)
                            .clickable {
                                activeSelectedId = pharmacy.id
                                onPharmacySelect(pharmacy.id)
                            }
                    ) {
                        Surface(
                            color = if (isSelected) Color(0xFF0F172A) else markerColor,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = if (isSelected) 8.dp else 3.dp,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color.White) else null
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            if (pharmacy.isVerified) StatusAvailableGreen else Color(0xFFCBD5E1),
                                            CircleShape
                                        )
                                )
                                Text(
                                    text = "${index + 1}. ${pharmacy.name.take(13)}${if (pharmacy.name.length > 13) ".." else ""}",
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Selected Pharmacy Callout Popover Card
                if (selectedPharmacy != null) {
                    val pharm = selectedPharmacy
                    val matchingRecord = if (selectedMedicineId != null) {
                        inventory.find { it.pharmacyId == pharm.id && it.medicineId == selectedMedicineId }
                    } else null

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 8.dp,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = pharm.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    if (pharm.isVerified) {
                                        Surface(
                                            color = StatusAvailableBg,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Verified,
                                                    contentDescription = "MediFind Verified",
                                                    tint = StatusAvailableGreen,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = "Verified",
                                                    fontSize = 8.sp,
                                                    color = StatusAvailableGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    } else {
                                        Surface(
                                            color = Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "Map Listed",
                                                fontSize = 8.sp,
                                                color = Color(0xFF64748B),
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = DistanceUtils.formatDistance(pharm.distanceKm),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MediBluePrimary
                                    )
                                    Text(
                                        text = "from DDUGU",
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "• ${pharm.address.take(22)}..",
                                        fontSize = 10.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                // Stock Inventory Verification Display Rule
                                Spacer(modifier = Modifier.height(2.dp))
                                if (matchingRecord != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        StatusBadge(status = matchingRecord.status)
                                        Text(
                                            text = "(${matchingRecord.stockCount} units — ₹${matchingRecord.unitPriceRupees})",
                                            fontSize = 10.sp,
                                            color = Color(0xFF334155),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    if (pharm.isVerified) {
                                        Text(
                                            text = "🟢 MediFind Partner • Active Stock Verification",
                                            fontSize = 9.sp,
                                            color = StatusAvailableGreen,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Text(
                                            text = "⚠️ Inventory unavailable (Map Listed Pharmacy)",
                                            fontSize = 9.sp,
                                            color = Color(0xFFD97706), // Amber warning note for map-listed only
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { onPharmacyDetailsClick(pharm.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("View", fontSize = 10.sp)
                                }
                                OutlinedButton(
                                    onClick = { onDirectionsClick(pharm.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Directions", fontSize = 10.sp)
                                }
                                IconButton(
                                    onClick = { activeSelectedId = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Popover",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
