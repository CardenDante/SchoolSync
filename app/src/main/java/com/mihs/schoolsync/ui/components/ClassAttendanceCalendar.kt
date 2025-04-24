// ClassAttendanceCalendar.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.*

@Composable
fun ClassAttendanceCalendar(
    yearMonth: YearMonth,
    attendanceData: Map<LocalDate, Map<String, Any>>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Calendar header
            Text(
                text = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + yearMonth.year,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weekday headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val daysOfWeek = DayOfWeek.values()
                for (dayOfWeek in daysOfWeek) {
                    Text(
                        text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar days
            val firstDayOfMonth = yearMonth.atDay(1)
            val firstCalendarDay = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            val lastDayOfMonth = yearMonth.atEndOfMonth()
            val lastCalendarDay = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))

            val calendarDays = mutableListOf<LocalDate>()
            var currentDay = firstCalendarDay
            while (!currentDay.isAfter(lastCalendarDay)) {
                calendarDays.add(currentDay)
                currentDay = currentDay.plusDays(1)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.height(250.dp)
            ) {
                items(calendarDays) { day ->
                    CalendarDay(
                        date = day,
                        isCurrentMonth = day.month == yearMonth.month,
                        hasData = attendanceData.containsKey(day),
                        attendanceData = attendanceData[day],
                        onDateSelected = { onDateSelected(day) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate,
    isCurrentMonth: Boolean,
    hasData: Boolean,
    attendanceData: Map<String, Any>?,
    onDateSelected: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date.equals(today)

    // Calculate attendance data
    var attendanceRate = 0f
    var backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)

    if (hasData && attendanceData != null) {
        val summary = attendanceData["summary"] as? Map<*, *>
        if (summary != null) {
            val totalStudents = summary["total"] as? Int ?: 0
            val presentCount = summary["present"] as? Int ?: 0

            if (totalStudents > 0) {
                attendanceRate = presentCount.toFloat() / totalStudents

                // Color based on attendance rate
                backgroundColor = when {
                    attendanceRate >= 0.9f -> Color(0xFFD0F0D0) // Good (light green)
                    attendanceRate >= 0.7f -> Color(0xFFFFF0C0) // Fair (light yellow)
                    else -> Color(0xFFF0D0D0) // Poor (light red)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f) // Square shape
            .clip(RoundedCornerShape(4.dp))
            .background(
                if (isToday) MaterialTheme.colorScheme.primaryContainer
                else if (isCurrentMonth) backgroundColor
                else Color.Transparent
            )
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(enabled = hasData) { onDateSelected() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCurrentMonth)
                    if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )

            if (hasData && attendanceRate > 0) {
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${(attendanceRate * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        attendanceRate >= 0.9f -> Color(0xFF388E3C) // Dark green
                        attendanceRate >= 0.7f -> Color(0xFFF57F17) // Dark yellow/orange
                        else -> Color(0xFFD32F2F) // Dark red
                    }
                )
            }
        }
    }
}