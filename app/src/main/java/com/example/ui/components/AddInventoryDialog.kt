package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Medicine
import com.example.ui.theme.MediBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInventoryDialog(
    medicines: List<Medicine>,
    onDismiss: () -> Unit,
    onConfirm: (medicineId: String, stockCount: Int, unitPrice: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMedicine by remember { mutableStateOf<Medicine?>(medicines.firstOrNull()) }
    var expandedDropdown by remember { mutableStateOf(false) }

    var stockInput by remember { mutableStateOf("25") }
    var priceInput by remember { mutableStateOf("15.00") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
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
                        imageVector = Icons.Default.AddBusiness,
                        contentDescription = "Add Medicine to Store",
                        tint = MediBluePrimary
                    )
                    Text(
                        text = "Add Medicine to Inventory",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Select a medicine from the master catalogue and enter your current store stock level.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Dropdown for medicine selection
                ExposedDropdownMenuBox(
                    expanded = expandedDropdown,
                    onExpandedChange = { expandedDropdown = !expandedDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedMedicine?.let { "${it.name} (${it.strength})" } ?: "Select Medicine",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Medicine") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false }
                    ) {
                        medicines.forEach { med ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = med.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(
                                            text = "${med.genericName} • ${med.form} (${med.strength})",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedMedicine = med
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                // Initial stock input
                OutlinedTextField(
                    value = stockInput,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) stockInput = input
                    },
                    label = { Text("Initial Stock Quantity (Units)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Unit price input
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Unit Retail Price (₹ Rupees)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val med = selectedMedicine
                    if (med == null) {
                        errorMessage = "Please select a medicine."
                        return@Button
                    }
                    val stock = stockInput.toIntOrNull()
                    if (stock == null || stock < 0) {
                        errorMessage = "Stock quantity must be a non-negative number."
                        return@Button
                    }
                    val price = priceInput.toDoubleOrNull()
                    if (price == null || price < 0.0) {
                        errorMessage = "Unit price must be a non-negative number."
                        return@Button
                    }

                    onConfirm(med.id, stock, price)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MediBluePrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Add to Store Inventory")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
