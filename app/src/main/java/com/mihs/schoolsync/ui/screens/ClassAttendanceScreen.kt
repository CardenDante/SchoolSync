// ClassAttendanceScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.components.ClassAttendanceCalendar
import com.mihs.schoolsync.ui.components.CourseTabs
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel
import com.mihs.schoolsync.ui.viewmodel.ClassViewModel
import com.mihs.schoolsync.ui.viewmodel.CourseViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassAttendanceScreen(
    classSectionId: Int,
    navigateBack: () -> Unit,
    navigateToDaily: (Int, LocalDate) -> Unit,
    navigateToCourse: (Int, Int, LocalDate) -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    classViewModel: ClassViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel()
) {
    val classSection by classViewModel.classSection.collectAsState()
    val courses by courseViewModel.classCourses.collectAsState()
    val rawAttendanceData by attendanceViewModel.classAttendance.collectAsState()
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()

    val classAttendanceData = remember(rawAttendanceData) {
        val result = mutableMapOf<LocalDate, Map<String, Any>>()
        rawAttendanceData.forEach { (key, value) ->
            try {
                val parts = key.toString().split("-")
                if (parts.size == 3) {
                    val year = parts[0].toInt()
                    val month = parts[1].toInt()
                    val day = parts[2].toInt()
                    val date = LocalDate.of(year, month, day)
                    result[date] = value as Map<String, Any>
                }
            } catch (e: Exception) {
                // Handle parsing error
            }
        }
        result
    }

    var selectedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedViewType by remember { mutableStateOf("calendar") }
    var selectedCourseId by remember { mutableStateOf<Int?>(null) }

    var showMonthPicker by remember { mutableStateOf(false) }

    LaunchedEffect(classSectionId) {
        classViewModel.fetchClassSection(classSectionId)
        courseViewModel.fetchClassCourses(classSectionId)
    }

    LaunchedEffect(classSectionId, selectedMonth) {
        attendanceViewModel.fetchClassAttendance(
            classSectionId = classSectionId,
            date = selectedDate
        )
    }

    if (showMonthPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
        )

        DatePickerDialog(
            onDismissRequest = { showMonthPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDay = millis / (24 * 60 * 60 * 1000)
                            val newDate = LocalDate.ofEpochDay(selectedDay)
                            selectedMonth = YearMonth.of(newDate.year, newDate.month)
                            selectedDate = newDate
                        }
                        showMonthPicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMonthPicker = false }) {
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
                        Text("Class Attendance")
                        if (classSection != null) {
                            Text(
                                text = classSection?.name ?: "",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showMonthPicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Select Month")
                    }
                    IconButton(
                        onClick = {
                            selectedViewType = if (selectedViewType == "calendar") "list" else "calendar"
                        }
                    ) {
                        if (selectedViewType == "calendar") {
                            Icon(Icons.Default.List, contentDescription = "List View")
                        } else {
                            Icon(Icons.Default.CalendarViewMonth, contentDescription = "Calendar View")
                        }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${selectedMonth.month} ${selectedMonth.year}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (courses.isNotEmpty()) {
                CourseTabs(
                    courses = courses,
                    selectedCourseId = selectedCourseId,
                    onCourseSelected = { courseId ->
                        selectedCourseId = courseId
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
            } else if (selectedViewType == "calendar") {
                ClassAttendanceCalendar(
                    yearMonth = selectedMonth,
                    attendanceData = classAttendanceData,
                    onDateSelected = { date ->
                        selectedDate = date
                        if (selectedCourseId != null) {
                            navigateToCourse(classSectionId, selectedCourseId!!, date)
                        } else {
                            navigateToDaily(classSectionId, date)
                        }
                    }
                )
            } else {
                AttendanceListView(
                    attendanceData = classAttendanceData,
                    onDateSelected = { date ->
                        selectedDate = date
                        if (selectedCourseId != null) {
                            navigateToCourse(classSectionId, selectedCourseId!!, date)
                        } else {
                            navigateToDaily(classSectionId, date)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun AttendanceListView(
    attendanceData: Map<LocalDate, Map<String, Any>>,
    onDateSelected: (LocalDate) -> Unit
) {
    val sortedDates = attendanceData.keys.sortedDescending()

    LazyColumn {
        items(sortedDates) { date ->
            val dayData = attendanceData[date] ?: emptyMap<String, Any>()
            val summary = dayData["summary"] as? Map<*, *> ?: emptyMap<String, Any>()

            val totalStudents = summary["total"] as? Int ?: 0
            val presentCount = summary["present"] as? Int ?: 0
            val absentCount = summary["absent"] as? Int ?: 0
            val lateCount = summary["late"] as? Int ?: 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onDateSelected(date) },
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEE, MMM dd")),
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (totalStudents > 0) {
                            Text(
                                text = "${(presentCount.toFloat() / totalStudents * 100).toInt()}%",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AttendanceCountChip(
                            label = "Present",
                            count = presentCount,
                            color = MaterialTheme.colorScheme.primary
                        )

                        AttendanceCountChip(
                            label = "Absent",
                            count = absentCount,
                            color = MaterialTheme.colorScheme.error
                        )

                        AttendanceCountChip(
                            label = "Late",
                            count = lateCount,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AttendanceCountChip(
    label: String,
    count: Int,
    color: Color
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
