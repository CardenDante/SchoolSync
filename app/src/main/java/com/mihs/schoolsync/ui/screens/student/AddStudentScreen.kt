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
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.StudentCreateRequest
import com.mihs.schoolsync.data.models.StudentStatus
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(
    viewModel: StudentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onStudentAdded: (Int) -> Unit
) {
    val operationState by viewModel.studentOperationState.collectAsState()

    // Form fields
    var studentId by remember { mutableStateOf("") }
    var applicationId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(StudentStatus.PENDING) }
    var isActive by remember { mutableStateOf(true) }
    var admissionDate by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_DATE)) }

    // Form validation
    val isStudentIdValid = studentId.isNotBlank()
    val isApplicationIdValid = applicationId.isNotBlank() && applicationId.toIntOrNull() != null

    // Handle creation success
    LaunchedEffect(operationState) {
        if (operationState is StudentViewModel.StudentOperationState.Success &&
            (operationState as StudentViewModel.StudentOperationState.Success).operation == StudentViewModel.Operation.CREATE) {
            // Since we don't have direct access to the created student ID,
            // you would typically need to implement a way to get this from your ViewModel
            // For now, let's use a placeholder ID (you'll need to adjust this based on your implementation)
            onStudentAdded(1) // Replace with actual student ID once available
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Student") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Student ID Field
                OutlinedTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    label = { Text("Student ID *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = studentId.isNotBlank() && !isStudentIdValid,
                    supportingText = {
                        if (studentId.isNotBlank() && !isStudentIdValid) {
                            Text("Student ID is required")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Application ID Field
                OutlinedTextField(
                    value = applicationId,
                    onValueChange = { applicationId = it },
                    label = { Text("Application ID *") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = applicationId.isNotBlank() && !isApplicationIdValid,
                    supportingText = {
                        if (applicationId.isNotBlank() && !isApplicationIdValid) {
                            Text("A valid numeric application ID is required")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Status Selection
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                StatusDropdown(
                    selectedStatus = status,
                    onStatusSelected = { status = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active Status
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

                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Admission Date Picker
                OutlinedTextField(
                    value = admissionDate,
                    onValueChange = { admissionDate = it },
                    label = { Text("Admission Date") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    supportingText = {
                        Text("Format: YYYY-MM-DD (Default: Today)")
                    }
                )

                // Note: In a real app, you would implement a proper date picker
                // For simplicity, we're using a text field here

                Spacer(modifier = Modifier.height(32.dp))

                // Required Fields Note
                Text(
                    text = "* Required fields",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Create Button
                Button(
                    onClick = {
                        val createRequest = StudentCreateRequest(
                            studentId = studentId,
                            applicationId = applicationId.toInt(),
                            status = status,
                            isActive = isActive,
                            admissionDate = admissionDate.takeIf { it.isNotBlank() }
                        )
                        viewModel.createStudent(createRequest)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isStudentIdValid && isApplicationIdValid
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Create Student")
                }
            }

            // Show loading overlay during creation
            if (operationState is StudentViewModel.StudentOperationState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Show error dialog if creation fails
            if (operationState is StudentViewModel.StudentOperationState.Error) {
                val errorMessage = (operationState as StudentViewModel.StudentOperationState.Error).message

                AlertDialog(
                    onDismissRequest = { viewModel.resetOperationState() },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetOperationState() }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusDropdown(
    selectedStatus: StudentStatus,
    onStatusSelected: (StudentStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedStatus.toString(),
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