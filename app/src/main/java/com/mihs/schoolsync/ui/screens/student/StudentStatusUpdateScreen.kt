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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.StudentDetail
import com.mihs.schoolsync.data.models.StudentStatus
import com.mihs.schoolsync.data.models.StudentStatusUpdateRequest
import com.mihs.schoolsync.ui.components.LoadingIndicator
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentStatusUpdateScreen(
    studentId: Int,
    viewModel: StudentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onUpdateSuccess: () -> Unit
) {
    val studentDetailState by viewModel.studentDetailState.collectAsState()
    val operationState by viewModel.studentOperationState.collectAsState()

    var selectedStatus by remember { mutableStateOf<StudentStatus?>(null) }
    var reasonText by remember { mutableStateOf("") }

    // Load student details when screen is displayed
    LaunchedEffect(studentId) {
        viewModel.getStudent(studentId)
    }

    // Set initial selected status when student details are loaded
    LaunchedEffect(studentDetailState) {
        if (studentDetailState is StudentViewModel.StudentDetailState.Success) {
            val student = (studentDetailState as StudentViewModel.StudentDetailState.Success).student
            selectedStatus = student.status
        }
    }

    // Handle update state changes
    LaunchedEffect(operationState) {
        if (operationState is StudentViewModel.StudentOperationState.Success &&
            (operationState as StudentViewModel.StudentOperationState.Success).operation == StudentViewModel.Operation.UPDATE_STATUS) {
            onUpdateSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Student Status") },
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
                        // Student Information Summary
                        StudentStatusHeader(student)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Current Status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Current Status:",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Badge(
                                containerColor = getStatusColor(student.status)
                            ) {
                                Text(
                                    text = student.status.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // New Status Selection
                        Text(
                            text = "Select New Status:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Radio Group
                        StatusSelectionGroup(
                            currentStatus = student.status,
                            selectedStatus = selectedStatus,
                            onStatusSelected = { selectedStatus = it }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Reason for status change
                        OutlinedTextField(
                            value = reasonText,
                            onValueChange = { reasonText = it },
                            label = { Text("Reason for status change") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Update Button
                        Button(
                            onClick = {
                                selectedStatus?.let { newStatus ->
                                    val statusUpdate = StudentStatusUpdateRequest(
                                        status = newStatus,
                                        reason = reasonText.takeIf { it.isNotBlank() }
                                    )
                                    viewModel.updateStudentStatus(studentId, statusUpdate)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedStatus != null && selectedStatus != student.status
                        ) {
                            Icon(
                                imageVector = Icons.Default.Update,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Update Status")
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
fun StudentStatusHeader(student: StudentDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = student.studentId,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            student.currentClass?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Class: $it",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            student.currentAcademicYear?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Academic Year: $it",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun StatusSelectionGroup(
    currentStatus: StudentStatus,
    selectedStatus: StudentStatus?,
    onStatusSelected: (StudentStatus) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        StudentStatus.values().forEach { status ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedStatus == status,
                    onClick = { onStatusSelected(status) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = getStatusColor(status)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = status.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (status == currentStatus) {
                            getStatusColor(status)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontWeight = if (status == currentStatus) FontWeight.Bold else FontWeight.Normal
                    )

                    Text(
                        text = getStatusDescription(status),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Helper function to get status descriptions
fun getStatusDescription(status: StudentStatus): String {
    return when (status) {
        StudentStatus.PENDING -> "Student application is under review"
        StudentStatus.ACTIVE -> "Student is currently enrolled and attending"
        StudentStatus.GRADUATED -> "Student has completed their studies"
        StudentStatus.WITHDRAWN -> "Student was withdrawn by guardian/parent"
        StudentStatus.SUSPENDED -> "Student is temporarily suspended"
        StudentStatus.EXPELLED -> "Student has been permanently removed"
        StudentStatus.TRANSFERRED -> "Student has moved to another school"
        StudentStatus.DECEASED -> "Student is deceased"
    }
}