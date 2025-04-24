package com.mihs.schoolsync.ui.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.StudentDetail
import com.mihs.schoolsync.data.models.StudentUpdateRequest
import com.mihs.schoolsync.ui.components.LoadingIndicator
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEditScreen(
    studentId: Int,
    viewModel: StudentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onEditSuccess: () -> Unit
) {
    val studentDetailState by viewModel.studentDetailState.collectAsState()
    val operationState by viewModel.studentOperationState.collectAsState()

    // Student ID field
    var studentIdField by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    // Load student details when screen is displayed
    LaunchedEffect(studentId) {
        viewModel.getStudent(studentId)
    }

    // Update fields when student details are loaded
    LaunchedEffect(studentDetailState) {
        if (studentDetailState is StudentViewModel.StudentDetailState.Success) {
            val student = (studentDetailState as StudentViewModel.StudentDetailState.Success).student
            studentIdField = student.studentId
            isActive = student.isActive
        }
    }

    // Handle update state changes
    LaunchedEffect(operationState) {
        if (operationState is StudentViewModel.StudentOperationState.Success &&
            (operationState as StudentViewModel.StudentOperationState.Success).operation == StudentViewModel.Operation.UPDATE) {
            onEditSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Student") },
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
            when (val state = studentDetailState) {
                is StudentViewModel.StudentDetailState.Loading -> {
                    LoadingIndicator()
                }
                is StudentViewModel.StudentDetailState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is StudentViewModel.StudentDetailState.Success -> {
                    val student = state.student

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Student ID Field
                        OutlinedTextField(
                            value = studentIdField,
                            onValueChange = { studentIdField = it },
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

                        Spacer(modifier = Modifier.height(32.dp))

                        // Read-only Information
                        ReadOnlyInfoSection(student)

                        Spacer(modifier = Modifier.height(32.dp))

                        // Save Button
                        Button(
                            onClick = {
                                val updateRequest = StudentUpdateRequest(
                                    studentId = studentIdField,
                                    isActive = isActive
                                )
                                viewModel.updateStudent(studentId, updateRequest)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = studentIdField.isNotBlank() &&
                                    (studentIdField != student.studentId || isActive != student.isActive)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Changes")
                        }
                    }
                }
                else -> {} // Idle state, do nothing
            }

            // Show loading overlay during update
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

            // Show error snackbar if update fails
            if (operationState is StudentViewModel.StudentOperationState.Error) {
                val errorMessage = (operationState as StudentViewModel.StudentOperationState.Error).message
                LaunchedEffect(errorMessage) {
                    // In a real app, you would show a Snackbar here
                    // For simplicity, we're not implementing the actual Snackbar logic
                }
            }
        }
    }
}

@Composable
fun ReadOnlyInfoSection(student: StudentDetail) {
    Column {
        Text(
            text = "Read-Only Information",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        ReadOnlyInfoRow(
            label = "Application ID",
            value = student.applicationId.toString(),
            icon = Icons.Default.Description
        )

        ReadOnlyInfoRow(
            label = "Status",
            value = student.status.toString(),
            icon = Icons.Default.Info
        )

        ReadOnlyInfoRow(
            label = "Admission Date",
            value = formatDateString(student.admissionDate),
            icon = Icons.Default.CalendarToday
        )

        student.currentClass?.let {
            ReadOnlyInfoRow(
                label = "Current Class",
                value = it,
                icon = Icons.Default.School
            )
        }

        student.currentAcademicYear?.let {
            ReadOnlyInfoRow(
                label = "Academic Year",
                value = it,
                icon = Icons.Default.DateRange
            )
        }
    }
}

@Composable
fun ReadOnlyInfoRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}