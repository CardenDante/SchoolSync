package com.mihs.schoolsync.ui.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mihs.schoolsync.data.models.StudentStatus
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

data class StudentFilters(
    val studentId: String? = null,
    val status: StudentStatus? = null,
    val isActive: Boolean? = null,
    val admissionDateStart: String? = null,
    val admissionDateEnd: String? = null,
    val classSectionId: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFilterScreen(
    initialFilters: StudentFilters,
    onNavigateBack: () -> Unit,
    onApplyFilters: (StudentFilters) -> Unit,
    onClearFilters: () -> Unit
) {
    var studentId by remember { mutableStateOf(initialFilters.studentId ?: "") }
    var status by remember { mutableStateOf(initialFilters.status) }
    var isActive by remember { mutableStateOf(initialFilters.isActive) }
    var admissionDateStart by remember { mutableStateOf(initialFilters.admissionDateStart ?: "") }
    var admissionDateEnd by remember { mutableStateOf(initialFilters.admissionDateEnd ?: "") }
    var classSectionId by remember { mutableStateOf(initialFilters.classSectionId?.toString() ?: "") }

    val anyFilterApplied = studentId.isNotBlank() || status != null || isActive != null ||
            admissionDateStart.isNotBlank() || admissionDateEnd.isNotBlank() ||
            classSectionId.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filter Students") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (anyFilterApplied) {
                        IconButton(onClick = {
                            onClearFilters()
                            studentId = ""
                            status = null
                            isActive = null
                            admissionDateStart = ""
                            admissionDateEnd = ""
                            classSectionId = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Filters"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Student ID Filter
            OutlinedTextField(
                value = studentId,
                onValueChange = { studentId = it },
                label = { Text("Student ID") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Status Filter
            Text(
                text = "Status",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            StatusFilterDropdown(
                selectedStatus = status,
                onStatusSelected = { status = it },
                onClearStatus = { status = null }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Active Status Filter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Status",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                // Three-state toggle: true, false, null (no filter)
                FilterTriStateToggle(
                    state = isActive,
                    onStateChange = { isActive = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Admission Date Range
            Text(
                text = "Admission Date Range",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = admissionDateStart,
                onValueChange = { admissionDateStart = it },
                label = { Text("From") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Format: YYYY-MM-DD")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = admissionDateEnd,
                onValueChange = { admissionDateEnd = it },
                label = { Text("To") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Format: YYYY-MM-DD")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Class Section ID Filter
            OutlinedTextField(
                value = classSectionId,
                onValueChange = { classSectionId = it },
                label = { Text("Class Section ID") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Apply Filters Button
            Button(
                onClick = {
                    onApplyFilters(
                        StudentFilters(
                            studentId = studentId.takeIf { it.isNotBlank() },
                            status = status,
                            isActive = isActive,
                            admissionDateStart = admissionDateStart.takeIf { it.isNotBlank() },
                            admissionDateEnd = admissionDateEnd.takeIf { it.isNotBlank() },
                            classSectionId = classSectionId.toIntOrNull()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Apply Filters")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusFilterDropdown(
    selectedStatus: StudentStatus?,
    onStatusSelected: (StudentStatus) -> Unit,
    onClearStatus: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedStatus?.toString() ?: "All Statuses",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Option to clear the filter
            DropdownMenuItem(
                text = { Text("All Statuses") },
                onClick = {
                    onClearStatus()
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterAltOff,
                        contentDescription = "Clear Filter"
                    )
                }
            )

            // Add a divider
            Divider()

            // Options for each status
            StudentStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = status.toString(),
                            color = getStatusColor(status)
                        )
                    },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun FilterTriStateToggle(
    state: Boolean?,
    onStateChange: (Boolean?) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Clear filter option
        IconButton(
            onClick = { onStateChange(null) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FilterAltOff,
                contentDescription = "Clear Filter",
                tint = if (state == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Active/Inactive toggle
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Inactive option
            FilterToggleButton(
                selected = state == false,
                onClick = { onStateChange(false) },
                content = {
                    Text(
                        text = "Inactive",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )

            // Active option
            FilterToggleButton(
                selected = state == true,
                onClick = { onStateChange(true) },
                content = {
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    }
}

@Composable
fun FilterToggleButton(
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides
                    if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            content()
        }
    }
}