package com.example.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.ui.theme.*
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
import com.example.ui.components.AddAlternativeMappingDialog
import com.example.ui.components.AlternativeVerificationBadge
import com.example.ui.components.EmptyState
import com.example.ui.components.StatCard
import com.example.ui.theme.MediBluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAlternativesScreen(
    alternatives: List<VerifiedAlternative>,
    medicines: List<Medicine>,
    onCreateMapping: (sourceId: String, altId: String, relType: RelationshipType, notes: String, status: VerificationStatus) -> Unit,
    onVerifyMapping: (id: String) -> Unit,
    onRejectMapping: (id: String, reason: String) -> Unit,
    onDeactivateMapping: (id: String) -> Unit,
    onDeleteMapping: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedStatusFilter by remember { mutableStateOf<VerificationStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    var showAddDialog by remember { mutableStateOf(false) }
    var editingMapping by remember { mutableStateOf<VerifiedAlternative?>(null) }

    var rejectDialogMappingId by remember { mutableStateOf<String?>(null) }
    var rejectReason by remember { mutableStateOf("") }

    val totalCount = alternatives.size
    val verifiedCount = alternatives.count { it.verificationStatus == VerificationStatus.VERIFIED }
    val pendingCount = alternatives.count { it.verificationStatus == VerificationStatus.PENDING }
    val rejectedCount = alternatives.count { it.verificationStatus == VerificationStatus.REJECTED }

    val filteredAlternatives = alternatives.filter { alt ->
        val statusMatches = selectedStatusFilter == null || alt.verificationStatus == selectedStatusFilter
        val sourceMed = medicines.find { it.id == alt.sourceMedicineId }?.name ?: ""
        val altMed = medicines.find { it.id == alt.alternativeMedicineId }?.name ?: ""
        val textMatches = searchQuery.isBlank() ||
                sourceMed.contains(searchQuery, ignoreCase = true) ||
                altMed.contains(searchQuery, ignoreCase = true) ||
                alt.notes.contains(searchQuery, ignoreCase = true)

        statusMatches && textMatches
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    editingMapping = null
                    showAddDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Mapping") },
                text = { Text("Add Alternative Mapping") },
                containerColor = MediBluePrimary,
                contentColor = Color.White
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Title
            item {
                Column {
                    Text(
                        text = "Verified Alternative Catalog",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Master controlled database for medical alternative options",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Stat Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Total",
                            value = "$totalCount",
                            subtitle = "Master Mappings",
                            icon = Icons.Default.MedicalServices,
                            iconBgColor = MediBlueContainer,
                            iconColor = MediBluePrimary
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Verified",
                            value = "$verifiedCount",
                            subtitle = "Approved ✓",
                            icon = Icons.Default.CheckCircle,
                            iconBgColor = StatusAvailableBg,
                            iconColor = StatusAvailableGreen
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Pending",
                            value = "$pendingCount",
                            subtitle = "Needs Review",
                            icon = Icons.Default.HourglassTop,
                            iconBgColor = StatusLowStockBg,
                            iconColor = StatusLowStockAmber
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        StatCard(
                            title = "Rejected",
                            value = "$rejectedCount",
                            subtitle = "Disapproved",
                            icon = Icons.Default.Cancel,
                            iconBgColor = StatusOutStockBg,
                            iconColor = StatusOutStockRed
                        )
                    }
                }
            }

            // Search & Filter
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by medicine name or clinical note...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Status Filter Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedStatusFilter == null,
                            onClick = { selectedStatusFilter = null },
                            label = { Text("All ($totalCount)", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedStatusFilter == VerificationStatus.VERIFIED,
                            onClick = { selectedStatusFilter = VerificationStatus.VERIFIED },
                            label = { Text("Verified ($verifiedCount)", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedStatusFilter == VerificationStatus.PENDING,
                            onClick = { selectedStatusFilter = VerificationStatus.PENDING },
                            label = { Text("Pending ($pendingCount)", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedStatusFilter == VerificationStatus.REJECTED,
                            onClick = { selectedStatusFilter = VerificationStatus.REJECTED },
                            label = { Text("Rejected ($rejectedCount)", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Alternative Mappings List
            if (filteredAlternatives.isEmpty()) {
                item {
                    EmptyState(
                        title = "No Alternative Mappings Found",
                        message = "No alternative medicine mappings match your current filter or search criteria."
                    )
                }
            } else {
                items(filteredAlternatives) { alt ->
                    val sourceMed = medicines.find { it.id == alt.sourceMedicineId }
                    val altMed = medicines.find { it.id == alt.alternativeMedicineId }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Top Row: Source -> Alternative Mapping Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${sourceMed?.name ?: alt.sourceMedicineId}  ➔  ${altMed?.name ?: alt.alternativeMedicineId}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Source Generic: ${sourceMed?.genericName ?: "Unknown"} | Alt Generic: ${altMed?.genericName ?: "Unknown"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                AlternativeVerificationBadge(status = alt.verificationStatus)
                            }

                            // Relationship Type & Source
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = alt.relationshipType.displayName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "Source: ${alt.verificationSource}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Audit Details
                            if (alt.verifiedBy != null || alt.notes.isNotBlank()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (alt.verifiedBy != null) {
                                            Text(
                                                text = "Verified/Audited By: ${alt.verifiedBy} (${alt.verifiedAt ?: "N/A"})",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        if (alt.notes.isNotBlank()) {
                                            Text(
                                                text = "Notes: ${alt.notes}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider()

                            // Action Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (alt.verificationStatus != VerificationStatus.VERIFIED) {
                                    Button(
                                        onClick = { onVerifyMapping(alt.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Verify & Approve", fontSize = 11.sp)
                                    }
                                }

                                if (alt.verificationStatus == VerificationStatus.PENDING) {
                                    OutlinedButton(
                                        onClick = {
                                            rejectDialogMappingId = alt.id
                                            rejectReason = ""
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB91C1C)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Reject", fontSize = 11.sp)
                                    }
                                }

                                if (alt.verificationStatus == VerificationStatus.VERIFIED) {
                                    OutlinedButton(
                                        onClick = { onDeactivateMapping(alt.id) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB45309)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Deactivate", fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(
                                    onClick = { onDeleteMapping(alt.id) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFB91C1C))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddDialog) {
        AddAlternativeMappingDialog(
            medicines = medicines,
            existingAlternatives = alternatives,
            editingMapping = editingMapping,
            onDismiss = { showAddDialog = false },
            onSubmit = { sourceId, altId, relType, notes, status ->
                onCreateMapping(sourceId, altId, relType, notes, status)
                showAddDialog = false
            }
        )
    }

    // Reject Dialog
    if (rejectDialogMappingId != null) {
        AlertDialog(
            onDismissRequest = { rejectDialogMappingId = null },
            title = { Text("Reject Alternative Mapping") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Provide a clinical reason for rejecting this alternative mapping:")
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        label = { Text("Rejection Reason") },
                        placeholder = { Text("e.g. Therapeutic mismatch / safety policy Violation.") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val mappingId = rejectDialogMappingId
                        if (mappingId != null && rejectReason.isNotBlank()) {
                            onRejectMapping(mappingId, rejectReason)
                            rejectDialogMappingId = null
                        }
                    },
                    enabled = rejectReason.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB91C1C))
                ) {
                    Text("Confirm Rejection")
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectDialogMappingId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
