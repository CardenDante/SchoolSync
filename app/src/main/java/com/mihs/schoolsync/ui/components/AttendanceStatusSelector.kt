// AttendanceStatusSelector.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mihs.schoolsync.data.models.AttendanceStatus

@Composable
fun AttendanceStatusSelector(
    selectedStatus: AttendanceStatus?,
    onStatusSelected: (AttendanceStatus) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AttendanceStatusOption(
            status = AttendanceStatus.PRESENT,
            isSelected = selectedStatus == AttendanceStatus.PRESENT,
            color = MaterialTheme.colorScheme.primary,
            onSelected = { onStatusSelected(AttendanceStatus.PRESENT) }
        )

        AttendanceStatusOption(
            status = AttendanceStatus.ABSENT,
            isSelected = selectedStatus == AttendanceStatus.ABSENT,
            color = MaterialTheme.colorScheme.error,
            onSelected = { onStatusSelected(AttendanceStatus.ABSENT) }
        )

        AttendanceStatusOption(
            status = AttendanceStatus.LATE,
            isSelected = selectedStatus == AttendanceStatus.LATE,
            color = MaterialTheme.colorScheme.tertiary,
            onSelected = { onStatusSelected(AttendanceStatus.LATE) }
        )

        AttendanceStatusOption(
            status = AttendanceStatus.EXCUSED,
            isSelected = selectedStatus == AttendanceStatus.EXCUSED,
            color = MaterialTheme.colorScheme.secondary,
            onSelected = { onStatusSelected(AttendanceStatus.EXCUSED) }
        )
    }
}

@Composable
fun AttendanceStatusOption(
    status: AttendanceStatus,
    isSelected: Boolean,
    color: Color,
    onSelected: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 40.dp)
            .background(
                color = if (isSelected) color else Color.Transparent,
                shape = shape
            )
            .border(
                width = 1.dp,
                color = color,
                shape = shape
            )
            .clickable(onClick = onSelected),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when(status) {
                AttendanceStatus.PRESENT -> "Present"
                AttendanceStatus.ABSENT -> "Absent"
                AttendanceStatus.LATE -> "Late"
                AttendanceStatus.EXCUSED -> "Excused"
                AttendanceStatus.CHECK_IN -> "Check-In"
            },
            color = if (isSelected) Color.White else color,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}