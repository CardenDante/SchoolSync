// AttendanceDashboardScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.components.ClassDropdown
import com.mihs.schoolsync.ui.components.PieChart
import com.mihs.schoolsync.ui.components.SimpleLineChart
import com.mihs.schoolsync.ui.components.PlaceholderChart
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel
import com.mihs.schoolsync.ui.viewmodel.ClassViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceDashboardScreen(
    navigateToClassAttendance: (Int) -> Unit,
    navigateToReports: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel(),
    classViewModel: ClassViewModel = hiltViewModel()
) {
    val classes by classViewModel.classes.collectAsState()
    val dashboard by attendanceViewModel.attendanceDashboard.collectAsState()
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()

    var selectedClassId by remember { mutableStateOf<Int?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        classViewModel.fetchClasses()
        attendanceViewModel.fetchAttendanceDashboard()
    }

    LaunchedEffect(selectedClassId) {
        attendanceViewModel.fetchAttendanceDashboard(selectedClassId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Attendance Dashboard") },
                actions = {
                    IconButton(onClick = { navigateToReports() }) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = "Reports"
                        )
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
            // Class Selection
            if (classes.isNotEmpty()) {
                ClassDropdown(
                    classes = classes,
                    selectedClassId = selectedClassId,
                    onClassSelected = { classId ->
                        selectedClassId = classId
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
            } else if (dashboard.isNotEmpty()) {
                // Attendance Overview Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val weeklyStats = dashboard["weekly_stats"] as? Map<*, *> ?: emptyMap<String, Float>()
                    val monthlyStats = dashboard["monthly_stats"] as? Map<*, *> ?: emptyMap<String, Float>()

                    DashboardCard(
                        title = "Weekly",
                        attendanceRate = (weeklyStats["attendance_rate"] as? Number)?.toFloat() ?: 0f,
                        punctualityRate = (weeklyStats["punctuality_rate"] as? Number)?.toFloat() ?: 0f,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    DashboardCard(
                        title = "Monthly",
                        attendanceRate = (monthlyStats["attendance_rate"] as? Number)?.toFloat() ?: 0f,
                        punctualityRate = (monthlyStats["punctuality_rate"] as? Number)?.toFloat() ?: 0f,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Attendance Trend Chart
                Text(
                    text = "Attendance Trends",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                val trendData = dashboard["trend_data"] as? List<Map<String, Any>> ?: emptyList()

                if (trendData.isNotEmpty()) {
                    val chartData = trendData.map {
                        val date = it["date"] as? String ?: ""
                        val rate = (it["attendance_rate"] as? Number)?.toFloat() ?: 0f
                        Pair(date, rate * 100) // Convert to percentage
                    }

                    SimpleLineChart(
                        data = chartData,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    PlaceholderChart(
                        title = "No trend data available",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Class Comparison (if multiple classes)
                Text(
                    text = "Class Attendance Comparison",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                val classComparison = dashboard["class_comparison"] as? List<Map<String, Any>> ?: emptyList()

                if (classComparison.isNotEmpty()) {
                    val pieData = classComparison.map { classData ->
                        val className = classData["class_name"] as? String ?: "Unknown"
                        val attendanceRate = (classData["attendance_rate"] as? Number)?.toFloat() ?: 0f
                        Pair(className, attendanceRate * 100) // Convert to percentage
                    }

                    PieChart(
                        data = pieData,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    PlaceholderChart(
                        title = "No class comparison data available",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedClassId?.let { navigateToClassAttendance(it) }
                        },
                        enabled = selectedClassId != null,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Class Attendance")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { navigateToReports() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Generate Reports")
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No dashboard data available")
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    attendanceRate: Float,
    punctualityRate: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(attendanceRate * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Attendance Rate",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Punctuality: ${(punctualityRate * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}