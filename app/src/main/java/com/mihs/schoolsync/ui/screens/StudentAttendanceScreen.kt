// StudentAttendanceScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.AttendanceStatus
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import com.mihs.schoolsync.utils.toJavaLocalDate
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAttendanceScreen(
    studentId: Int,
    navigateBack: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    // Get student detail from StudentViewModel's state
    val studentDetailState by studentViewModel.studentDetailState.collectAsState()
    val studentDetail = when (studentDetailState) {
        is StudentViewModel.StudentDetailState.Success ->
            (studentDetailState as StudentViewModel.StudentDetailState.Success).student
        else -> null
    }

    val attendance by attendanceViewModel.studentsAttendance.collectAsState()
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()

    // Get loading state from both ViewModels
    val isLoading = loading || studentDetailState is StudentViewModel.StudentDetailState.Loading

    // Get error from either ViewModel
    val errorMessage = when {
        error != null -> error
        studentDetailState is StudentViewModel.StudentDetailState.Error ->
            (studentDetailState as StudentViewModel.StudentDetailState.Error).message
        else -> null
    }

    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Load student details
    LaunchedEffect(studentId) {
        studentViewModel.getStudent(studentId)
    }

    // Load attendance data
    LaunchedEffect(studentId, startDate, endDate) {
        attendanceViewModel.fetchStudentAttendance(
            studentId = studentId,
            startDate = startDate.toJavaLocalDate(),
            endDate = endDate.toJavaLocalDate()
        )
    }

    // Start date picker dialog
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate.toEpochDay() * 24 * 60 * 60 * 1000
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDay = millis / (24 * 60 * 60 * 1000)
                            startDate = LocalDate.ofEpochDay(selectedDay)
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

// End date picker dialog
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate.toEpochDay() * 24 * 60 * 60 * 1000
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDay = millis / (24 * 60 * 60 * 1000)
                            endDate = LocalDate.ofEpochDay(selectedDay)
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Student Attendance")
                        if (studentDetail != null) {
                            Text(
                                text = studentDetail.studentId,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
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
                .padding(16.dp)
        ) {
            // Date Range Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Date Range",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "From",
                                style = MaterialTheme.typography.bodySmall
                            )
                            TextButton(onClick = { showStartDatePicker = true }) {
                                Text(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            }
                        }

                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "To",
                            modifier = Modifier.size(24.dp)
                        )

                        Column {
                            Text(
                                text = "To",
                                style = MaterialTheme.typography.bodySmall
                            )
                            TextButton(onClick = { showEndDatePicker = true }) {
                                Text(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attendance Summary
            if (attendance.isNotEmpty()) {
                AttendanceSummaryCard(attendance)

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Attendance Records List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $errorMessage")
                }
            } else if (attendance.isNotEmpty()) {
                Text(
                    text = "Attendance Records",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn {
                    items(attendance) { record ->
                        StudentAttendanceRecordItem(record)
                        Divider()
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No attendance records found for the selected date range")
                }
            }
        }
    }
}

@Composable
fun AttendanceSummaryCard(attendance: List<Map<String, Any>>) {
    // Calculate summary statistics
    val totalDays = attendance.size
    val presentDays = attendance.count {
        (it["status"] as? String)?.equals("present", ignoreCase = true) == true
    }
    val absentDays = attendance.count {
        (it["status"] as? String)?.equals("absent", ignoreCase = true) == true
    }
    val lateDays = attendance.count {
        (it["status"] as? String)?.equals("late", ignoreCase = true) == true
    }
    val excusedDays = attendance.count {
        (it["status"] as? String)?.equals("excused", ignoreCase = true) == true
    }

    val attendanceRate = if (totalDays > 0) {
        (presentDays + lateDays).toFloat() / totalDays
    } else 0f

    val punctualityRate = if (presentDays + lateDays > 0) {
        presentDays.toFloat() / (presentDays + lateDays)
    } else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Summary",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${(attendanceRate * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Attendance Rate",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${(punctualityRate * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Punctuality Rate",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AttendanceStatusCount("Present", presentDays, MaterialTheme.colorScheme.primary)
                AttendanceStatusCount("Absent", absentDays, MaterialTheme.colorScheme.error)
                AttendanceStatusCount("Late", lateDays, MaterialTheme.colorScheme.tertiary)
                AttendanceStatusCount("Excused", excusedDays, MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun AttendanceStatusCount(
    label: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun StudentAttendanceRecordItem(record: Map<String, Any>) {
    val date = record["date"] as? String ?: ""
    val status = record["status"] as? String ?: ""
    val reason = record["reason"] as? String
    val checkIn = record["check_in"] as? Map<*, *>
    val arrivalTime = checkIn?.get("arrival_time") as? String

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date column
            Column(
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium
                )
                if (arrivalTime != null) {
                    Text(
                        text = arrivalTime,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Status indicator
            val statusColor = when(status.lowercase()) {
                "present" -> MaterialTheme.colorScheme.primary
                "absent" -> MaterialTheme.colorScheme.error
                "late" -> MaterialTheme.colorScheme.tertiary
                "excused" -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.outline
            }

            Surface(
                shape = MaterialTheme.shapes.small,
                color = statusColor,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .height(24.dp)
                    .width(80.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }

            // Reason text
            if (reason != null && reason.isNotEmpty()) {
                Text(
                    text = reason,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}