// ManualCheckInScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.components.StudentSearchField
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualCheckInScreen(
    navigateBack: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    val students by studentViewModel.students.collectAsState() // Changed from searchResults
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStudentId by remember { mutableStateOf<Int?>(null) }
    var selectedStudentName by remember { mutableStateOf("") }
    var manualArrivalTime by remember { mutableStateOf("") }

    var showSuccessMessage by remember { mutableStateOf(false) }
    var checkInCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            studentViewModel.fetchStudents(searchQuery) // Changed from searchStudents
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manual Check-In") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (checkInCompleted) {
                // Success message
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Check-In Successful!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = selectedStudentName,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (manualArrivalTime.isNotEmpty()) {
                            Text(
                                text = "Arrival Time: $manualArrivalTime",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                // Reset form for next check-in
                                searchQuery = ""
                                selectedStudentId = null
                                selectedStudentName = ""
                                manualArrivalTime = ""
                                checkInCompleted = false
                            }
                        ) {
                            Text("Check In Another Student")
                        }
                    }
                }
            } else {
                // Check-in form
                Text(
                    text = "Student Check-In",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Student Search
                StudentSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    students = students,
                    onStudentSelected = { student ->
                        selectedStudentId = student.id
                        selectedStudentName = student.fullName
                        searchQuery = student.fullName
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Manual Time Entry (Optional)
                OutlinedTextField(
                    value = manualArrivalTime,
                    onValueChange = { manualArrivalTime = it },
                    label = { Text("Arrival Time (Optional - HH:MM)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Leave blank to use current time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = {
                        selectedStudentId?.let { studentId ->
                            // Parse manual time if provided
                            val arrivalTime = if (manualArrivalTime.isNotEmpty()) {
                                try {
                                    // Simple format validation (HH:MM)
                                    val parts = manualArrivalTime.split(":")
                                    if (parts.size == 2) {
                                        val hour = parts[0].toInt()
                                        val minute = parts[1].toInt()
                                        if (hour in 0..23 && minute in 0..59) {
                                            LocalTime.of(hour, minute)
                                        } else null
                                    } else null
                                } catch (e: Exception) {
                                    null
                                }
                            } else null

                            attendanceViewModel.recordCheckIn(
                                studentId = studentId,
                                arrivalTime = arrivalTime
                            )

                            checkInCompleted = true
                        }
                    },
                    enabled = selectedStudentId != null && !loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Record Check-In")
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}