// DailyAttendanceScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.AttendanceStatus
import com.mihs.schoolsync.ui.components.AttendanceStatusSelector
import com.mihs.schoolsync.ui.components.StudentAttendanceItem
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel
import com.mihs.schoolsync.ui.viewmodel.ClassViewModel
import com.mihs.schoolsync.utils.toJavaLocalDate
import org.threeten.bp.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyAttendanceScreen(
    navigateBack: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    classViewModel: ClassViewModel = hiltViewModel()
) {
    val classes by classViewModel.classes.collectAsState()
    val classAttendance by attendanceViewModel.classAttendance.collectAsState()
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()

    var selectedClassId by remember { mutableStateOf<Int?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var showCalendar by remember { mutableStateOf(false) }

    LaunchedEffect(selectedClassId, selectedDate) {
        selectedClassId?.let { classId ->
            attendanceViewModel.fetchClassAttendance(classId, selectedDate.toJavaLocalDate())
        }
    }

    LaunchedEffect(Unit) {
        classViewModel.fetchClasses()
    }

    // Date picker dialog
    if (showCalendar) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
        )

        DatePickerDialog(
            onDismissRequest = { showCalendar = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDay = millis / (24 * 60 * 60 * 1000)
                            selectedDate = LocalDate.ofEpochDay(selectedDay)
                        }
                        showCalendar = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendar = false }) {
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
                title = { Text("Daily Attendance") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showCalendar = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
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
            // Class Selection Dropdown
            if (classes.isNotEmpty()) {
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { /* Handle expansion */ }
                ) {
                    TextField(
                        value = classes.find { it.id == selectedClassId }?.name ?: "Select Class Section",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = { /* Handle dismissal */ }
                    ) {
                        classes.forEach { clasSection ->
                            DropdownMenuItem(
                                text = { Text(clasSection.name) },
                                onClick = {
                                    selectedClassId = clasSection.id
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Date Display
            Text(
                text = "Date: ${selectedDate}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Attendance Summary if available
            if (classAttendance.isNotEmpty() && classAttendance.containsKey("summary")) {
                val summary = classAttendance["summary"] as Map<*, *>

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DailyAttendanceStat(
                            label = "Present",
                            value = "${summary["present"] ?: 0}",
                            color = MaterialTheme.colorScheme.primary
                        )
                        DailyAttendanceStat(
                            label = "Absent",
                            value = "${summary["absent"] ?: 0}",
                            color = MaterialTheme.colorScheme.error
                        )
                        DailyAttendanceStat(
                            label = "Late",
                            value = "${summary["late"] ?: 0}",
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        DailyAttendanceStat(
                            label = "Excused",
                            value = "${summary["excused"] ?: 0}",
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Students Attendance List
            if (loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error")
                }
            } else if (classAttendance.isNotEmpty() && classAttendance.containsKey("attendance")) {
                val students = classAttendance["attendance"] as List<Map<String, Any>>?

                // Continuing DailyAttendanceScreen.kt
                LazyColumn {
                    items(students ?: emptyList()) { student ->
                        val studentId = student["student_id"] as Int
                        val fullName = student["full_name"] as String
                        val studentNumber = student["student_number"] as String
                        val attendance = student["attendance"] as? Map<String, Any>
                        val checkIn = student["check_in"] as? Map<String, Any>

                        val currentStatus = attendance?.get("status") as? String ?: "not_recorded"
                        val attendanceId = attendance?.get("id") as? Int
                        val arrivalTime = checkIn?.get("arrival_time") as? String

                        var selectedStatus by remember(currentStatus) {
                            mutableStateOf(
                                when(currentStatus) {
                                    "present" -> AttendanceStatus.PRESENT
                                    "absent" -> AttendanceStatus.ABSENT
                                    "late" -> AttendanceStatus.LATE
                                    "excused" -> AttendanceStatus.EXCUSED
                                    else -> null
                                }
                            )
                        }
                        var reason by remember { mutableStateOf(attendance?.get("reason") as? String ?: "") }

                        StudentAttendanceItem(
                            studentName = fullName,
                            studentId = studentNumber,
                            currentStatus = selectedStatus,
                            arrivalTime = arrivalTime,
                            onStatusSelected = { newStatus ->
                                selectedStatus = newStatus

                                // Save immediately when status changes
                                attendanceViewModel.recordDailyAttendance(
                                    studentId = studentId,
                                    date = selectedDate.toJavaLocalDate(),
                                    status = newStatus,
                                    reason = if (reason.isNotEmpty()) reason else null
                                )
                            },
                            onReasonChanged = { newReason ->
                                reason = newReason
                            },
                            onSave = {
                                selectedStatus?.let { status ->
                                    attendanceViewModel.recordDailyAttendance(
                                        studentId = studentId,
                                        date = selectedDate.toJavaLocalDate(),
                                        status = status,
                                        reason = if (reason.isNotEmpty()) reason else null
                                    )
                                }
                            }
                        )
                        Divider()
                    }
                }

                // Bulk Action Button
                Button(
                    onClick = {
                        // Prepare bulk attendance data
                        val records = (students ?: emptyList()).mapNotNull { student ->
                            val studentId = student["student_id"] as Int
                            val attendance = student["attendance"] as? Map<String, Any>
                            val status = attendance?.get("status") as? String

                            if (status != null) {
                                mapOf(
                                    "student_id" to studentId,
                                    "status" to status,
                                    "reason" to (attendance["reason"] as? String ?: "")
                                )
                            } else null
                        }

                        selectedClassId?.let { classId ->
                            attendanceViewModel.recordBulkDailyAttendance(
                                date = selectedDate.toJavaLocalDate(),
                                classSectionId = classId,
                                records = records
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = selectedClassId != null && students?.isNotEmpty() == true
                ) {
                    Text("Save All Attendance")
                }
            } else if (selectedClassId != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No attendance data available for this class on the selected date")
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Please select a class section to view attendance")
                }
            }
        }
    }
}

@Composable
fun DailyAttendanceStat(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}