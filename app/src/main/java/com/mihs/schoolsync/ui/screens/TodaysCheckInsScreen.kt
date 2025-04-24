// TodaysCheckInsScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodaysCheckInsScreen(
    navigateBack: () -> Unit,
    navigateToCheckIn: () -> Unit,
    navigateToQrCheckIn: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel()
) {
    val checkIns by attendanceViewModel.todayCheckIns.collectAsState()
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        attendanceViewModel.fetchTodayCheckIns()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Check-Ins") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navigateToCheckIn() }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Manual Check-In")
                    }
                    IconButton(onClick = { navigateToQrCheckIn() }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "QR Code Check-In")
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
            // Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Check-In Summary",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Total Check-Ins",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${checkIns.size}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // You could add more stats here if needed
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check-Ins List
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
            } else if (checkIns.isNotEmpty()) {
                LazyColumn {
                    items(checkIns) { checkIn ->
                        CheckInListItem(checkIn)
                        Divider()
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "No Check-Ins",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "No check-ins recorded for today",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = { navigateToCheckIn() },
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Manual Check-In")
                            }

                            OutlinedButton(
                                onClick = { navigateToQrCheckIn() }
                            ) {
                                Text("QR Check-In")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CheckInListItem(checkIn: Map<String, Any>) {
    val studentName = checkIn["student_name"] as? String ?: "Unknown"
    val studentId = checkIn["student_number"] as? String ?: "Unknown ID"
    val arrivalTime = checkIn["arrival_time"] as? String
    val checkInTimestamp = checkIn["check_in_timestamp"] as? String
    val className = checkIn["class_name"] as? String

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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = studentName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "ID: $studentId",
                    style = MaterialTheme.typography.bodySmall
                )
                if (className != null) {
                    Text(
                        text = className,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (arrivalTime != null) {
                    Text(
                        text = arrivalTime,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (checkInTimestamp != null) {
                    Text(
                        text = "Recorded at $checkInTimestamp",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}