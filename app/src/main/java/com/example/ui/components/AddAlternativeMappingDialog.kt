package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Medicine
import com.example.model.RelationshipType
import com.example.model.VerificationStatus
import com.example.model.VerifiedAlternative

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlternativeMappingDialog(
    medicines: List<Medicine>,
    existingAlternatives: List<VerifiedAlternative>,
    editingMapping: VerifiedAlternative? = null,
    onDismiss: () -> Unit,
    onSubmit: (sourceId: String, altId: String, relType: RelationshipType, notes: String, status: VerificationStatus) -> Unit
) {
    var selectedSourceId by remember { mutableStateOf(editingMapping?.sourceMedicineId ?: (medicines.firstOrNull()?.id ?: "")) }
    var selectedAltId by remember { mutableStateOf(editingMapping?.alternativeMedicineId ?: (medicines.getOrNull(1)?.id ?: "")) }
    var selectedRelType by remember { mutableStateOf(editingMapping?.relationshipType ?: RelationshipType.SAME_ACTIVE_INGREDIENT) }
    var selectedStatus by remember { mutableStateOf(editingMapping?.verificationStatus ?: VerificationStatus.VERIFIED) }
    var notes by remember { mutableStateOf(editingMapping?.notes ?: "") }

    var sourceExpanded by remember { mutableStateOf(false) }
    var altExpanded by remember { mutableStateOf(false) }
    var relExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    val sourceMed = medicines.find { it.id == selectedSourceId }
    val altMed = medicines.find { it.id == selectedAltId }

    val isSelfReferencing = selectedSourceId.isNotBlank() && selectedSourceId == selectedAltId
    val isDuplicate = existingAlternatives.any {
        it.id != editingMapping?.id &&
                it.sourceMedicineId == selectedSourceId &&
                it.alternativeMedicineId == selectedAltId &&
                it.relationshipType == selectedRelType
    }

    val isValid = !isSelfReferencing && !isDuplicate && selectedSourceId.isNotBlank() && selectedAltId.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (editingMapping != null) "Edit Alternative Mapping" else "Create Verified Alternative Mapping",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Warning if self referencing
                if (isSelfReferencing) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB91C1C), modifier = Modifier.size(18.dp))
                            Text(
                                text = "Validation Error: Source and Alternative medicine cannot be identical.",
                                fontSize = 11.sp,
                                color = Color(0xFF991B1B),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Warning if duplicate
                if (isDuplicate) {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                            Text(
                                text = "Duplicate Warning: A mapping with relationship '${selectedRelType.displayName}' already exists.",
                                fontSize = 11.sp,
                                color = Color(0xFF92400E),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 1. Source Medicine Selector
                ExposedDropdownMenuBox(
                    expanded = sourceExpanded,
                    onExpandedChange = { sourceExpanded = it }
                ) {
                    OutlinedTextField(
                        value = sourceMed?.let { "${it.name} (${it.strength})" } ?: "Select Source Medicine",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Source Medicine (Requested)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = sourceExpanded,
                        onDismissRequest = { sourceExpanded = false }
                    ) {
                        medicines.forEach { med ->
                            DropdownMenuItem(
                                text = { Text("${med.name} (${med.strength}) — ${med.genericName}") },
                                onClick = {
                                    selectedSourceId = med.id
                                    sourceExpanded = false
                                }
                            )
                        }
                    }
                }

                // 2. Alternative Medicine Selector
                ExposedDropdownMenuBox(
                    expanded = altExpanded,
                    onExpandedChange = { altExpanded = it }
                ) {
                    OutlinedTextField(
                        value = altMed?.let { "${it.name} (${it.strength})" } ?: "Select Alternative Medicine",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Verified Alternative Medicine") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = altExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = altExpanded,
                        onDismissRequest = { altExpanded = false }
                    ) {
                        medicines.filter { it.id != selectedSourceId }.forEach { med ->
                            DropdownMenuItem(
                                text = { Text("${med.name} (${med.strength}) — ${med.genericName}") },
                                onClick = {
                                    selectedAltId = med.id
                                    altExpanded = false
                                }
                            )
                        }
                    }
                }

                // 3. Relationship Type Selector
                ExposedDropdownMenuBox(
                    expanded = relExpanded,
                    onExpandedChange = { relExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedRelType.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Relationship Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = relExpanded,
                        onDismissRequest = { relExpanded = false }
                    ) {
                        RelationshipType.values().forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(type.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(type.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    selectedRelType = type
                                    relExpanded = false
                                }
                            )
                        }
                    }
                }

                // 4. Verification Status Selector
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedStatus.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Initial Verification Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        VerificationStatus.values().forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st.displayName, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    selectedStatus = st
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                // 5. Clinical Reference Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Clinical Reference & Evaluation Notes") },
                    placeholder = { Text("e.g. Approved by Pharmacist Board based on bio-equivalence guidelines.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onSubmit(selectedSourceId, selectedAltId, selectedRelType, notes, selectedStatus)
                    }
                },
                enabled = isValid
            ) {
                Text(if (editingMapping != null) "Update Mapping" else "Create & Verify Mapping")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
