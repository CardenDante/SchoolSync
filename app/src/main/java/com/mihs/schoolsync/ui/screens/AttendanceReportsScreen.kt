// AttendanceReportsScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.components.ClassDropdown
import com.mihs.schoolsync.ui.components.CourseDropdown
import com.mihs.schoolsync.ui.components.StudentSearchField
import com.mihs.schoolsync.ui.components.toUiStudents
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel
import com.mihs.schoolsync.ui.viewmodel.ClassViewModel
import com.mihs.schoolsync.ui.viewmodel.CourseViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import com.mihs.schoolsync.utils.toJavaLocalDate
import com.mihs.schoolsync.utils.toThreetenLocalDate
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceReportsScreen(
    navigateBack: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    classViewModel: ClassViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    val classes by classViewModel.classes.collectAsState()
    val courses by courseViewModel.courses.collectAsState() // Changed from searchResults
    val report by attendanceViewModel.attendanceReport.collectAsState()
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()
    val studentDetails by studentViewModel.students.collectAsState()
    val students = studentDetails.toUiStudents()

    val scrollState = rememberScrollState()

    // Report parameters
    var startDate by remember { mutableStateOf(LocalDate.now().minusDays(30)) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var studentId by remember { mutableStateOf<Int?>(null) }
    var studentName by remember { mutableStateOf("") }
    var classSectionId by remember { mutableStateOf<Int?>(null) }
    var courseOfferingId by remember { mutableStateOf<Int?>(null) }
    var includeWeekends by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }

    // Material 3 date picker states
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    // Load data
    LaunchedEffect(Unit) {
        classViewModel.fetchClasses()
        courseViewModel.fetchCourses()
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 3) {
            studentViewModel.fetchStudents(searchQuery) // Changed from searchStudents
        }
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
                title = { Text("Attendance Reports") },
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
                .verticalScroll(scrollState)
        ) {
            // Report Parameters Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Report Parameters",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Range Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Start date selector
                        OutlinedButton(
                            onClick = { showStartDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "to",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // End date selector
                        OutlinedButton(
                            onClick = { showEndDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(endDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Student Search (Optional)
                    Text(
                        text = "Student (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StudentSearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        students = students,
                        onStudentSelected = { student ->
                            studentId = student.id
                            studentName = student.fullName
                            searchQuery = student.fullName
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Class Section Dropdown (Optional)
                    Text(
                        text = "Class Section (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ClassDropdown(
                        classes = classes,
                        selectedClassId = classSectionId,
                        onClassSelected = { classId ->
                            classSectionId = classId
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Course Dropdown (Optional)
                    Text(
                        text = "Course (Optional)",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CourseDropdown(
                        courses = courses,
                        selectedCourseId = courseOfferingId,
                        onCourseSelected = { courseId ->
                            courseOfferingId = courseId
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Include Weekends Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = includeWeekends,
                            onCheckedChange = { includeWeekends = it }
                        )
                        Text(
                            text = "Include Weekends",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Generate Report Button
                    Button(
                        onClick = {
                            attendanceViewModel.generateAttendanceReport(
                                startDate = startDate.toJavaLocalDate(), // Convert to Java time
                                endDate = endDate.toJavaLocalDate(), // Convert to Java time
                                studentId = studentId,
                                classSectionId = classSectionId,
                                courseOfferingId = courseOfferingId,
                                includeWeekends = includeWeekends
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Report")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Report Results
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Use the error value directly instead of trying smart cast
                        val errorMessage = error?.toString() ?: "Unknown error"
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else if (report.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Attendance Report",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Report Summary
                        val summary = report["summary"] as? Map<*, *> ?: emptyMap<String, Any>()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ReportStatCard(
                                title = "Attendance Rate",
                                value = "${(summary["attendance_rate"] as? Number)?.toFloat()?.times(100)?.toInt() ?: 0}%",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            ReportStatCard(
                                title = "Punctuality Rate",
                                value = "${(summary["punctuality_rate"] as? Number)?.toFloat()?.times(100)?.toInt() ?: 0}%",
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Detailed Stats
                        DetailedStats(summary)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Daily breakdown if available
                        val dailyStats = report["daily_stats"] as? List<Map<String, Any>> ?: emptyList()

                        if (dailyStats.isNotEmpty()) {
                            Text(
                                text = "Daily Breakdown",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            dailyStats.forEach { dayStat ->
                                DailyStatItem(dayStat)
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }

                        // Export options
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = { /* Export as PDF */ }
                            ) {
                                Text("Export as PDF")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedButton(
                                onClick = { /* Export as CSV */ }
                            ) {
                                Text("Export as CSV")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
        }
    }
}

@Composable
fun DetailedStats(summary: Map<*, *>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            StatItem("Total Days", "${summary["total_days"] ?: 0}")
            StatItem("Present Days", "${summary["present_count"] ?: 0}")
            StatItem("Absent Days", "${summary["absent_count"] ?: 0}")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            StatItem("Late Days", "${summary["late_count"] ?: 0}")
            StatItem("Excused Days", "${summary["excused_count"] ?: 0}")
            StatItem("Check-Ins", "${summary["check_in_count"] ?: 0}")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DailyStatItem(dayStat: Map<String, Any>) {
    val date = dayStat["date"] as? String ?: ""
    val className = dayStat["class_section_name"] as? String ?: ""
    val attendanceRate = (dayStat["attendance_rate"] as? Number)?.toFloat() ?: 0f
    val presentCount = dayStat["present_count"] as? Int ?: 0
    val totalStudents = dayStat["total_students"] as? Int ?: 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.titleMedium
            )
            if (className.isNotEmpty()) {
                Text(
                    text = className,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${(attendanceRate * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "$presentCount/$totalStudents students",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}