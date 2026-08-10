package com.example.ui.pharmacy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Pharmacy
import com.example.ui.theme.MediBluePrimary

@Composable
fun PharmacyProfileScreen(
    pharmacy: Pharmacy,
    onSaveProfileClick: ((String, String, String, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var address by remember { mutableStateOf(pharmacy.address) }
    var phone by remember { mutableStateOf(pharmacy.phone) }
    var timing by remember { mutableStateOf(pharmacy.timing) }
    var facilityType by remember { mutableStateOf(pharmacy.facilityType) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Store Profile",
                                tint = MediBluePrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = pharmacy.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (pharmacy.isVerified) "License #UP-LKO-88214 • Verified Facility" else "License #UP-LKO-PENDING • Unverified Store",
                                    fontSize = 12.sp,
                                    color = if (pharmacy.isVerified) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        if (onSaveProfileClick != null) {
                            IconButton(onClick = { isEditing = !isEditing }) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = "Edit Profile"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditing) {
                        Text(
                            text = "Edit Store Profile Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MediBluePrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Address") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Primary Phone") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = timing,
                            onValueChange = { timing = it },
                            label = { Text("Operating Hours") },
                            leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = facilityType,
                            onValueChange = { facilityType = it },
                            label = { Text("Facility Type") },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                onSaveProfileClick?.invoke(address, phone, timing, facilityType)
                                isEditing = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Store Changes")
                        }
                    } else {
                        ProfileDetailRow(
                            icon = Icons.Default.LocationOn,
                            title = "Address",
                            value = pharmacy.address
                        )

                        ProfileDetailRow(
                            icon = Icons.Default.Phone,
                            title = "Primary Phone",
                            value = pharmacy.phone
                        )

                        ProfileDetailRow(
                            icon = Icons.Default.AccessTime,
                            title = "Operating Hours",
                            value = "${pharmacy.openStatus} (${pharmacy.timing})"
                        )

                        ProfileDetailRow(
                            icon = Icons.Default.LocalHospital,
                            title = "Facility Type",
                            value = pharmacy.facilityType
                        )
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Store Services & Facilities",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = true, onClick = {}, label = { Text("Refrigerated Cold Chain ❄️") })
                        FilterChip(selected = true, onClick = {}, label = { Text("Home Delivery 🚚") })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = true, onClick = {}, label = { Text("Oxygen Cylinder 🩺") })
                        FilterChip(selected = true, onClick = {}, label = { Text("Digital Payment 💳") })
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
