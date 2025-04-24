// StudentAttendanceItem.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mihs.schoolsync.data.models.AttendanceStatus

@Composable
fun StudentAttendanceItem(
    studentName: String,
    studentId: String,
    currentStatus: AttendanceStatus?,
    arrivalTime: String? = null,
    onStatusSelected: (AttendanceStatus) -> Unit,
    onReasonChanged: (String) -> Unit,
    onSave: () -> Unit
) {
    var expandedReason by remember { mutableStateOf(false) }
    var reasonText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Initialize reason text if provided
        if (currentStatus == AttendanceStatus.ABSENT || currentStatus == AttendanceStatus.EXCUSED) {
            expandedReason = true
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Student Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = studentName,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "ID: $studentId",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (arrivalTime != null) {
                        Text(
                            text = "Arrival: $arrivalTime",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Attendance Status Selection
            AttendanceStatusSelector(
                selectedStatus = currentStatus,
                onStatusSelected = { status ->
                    onStatusSelected(status)

                    // Show reason field for certain statuses
                    expandedReason = status == AttendanceStatus.ABSENT ||
                            status == AttendanceStatus.EXCUSED ||
                            status == AttendanceStatus.LATE
                }
            )

            // Reason TextField (expandable)
            AnimatedVisibility(visible = expandedReason) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = {
                            reasonText = it
                            onReasonChanged(it)
                        },
                        label = { Text("Reason") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = onSave,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}