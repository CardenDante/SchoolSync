// CourseTabs.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mihs.schoolsync.data.models.CourseOffering

@Composable
fun CourseTabs(
    courses: List<CourseOffering>,
    selectedCourseId: Int?,
    onCourseSelected: (Int?) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
    ) {
        // Add "All" tab
        FilterChip(
            selected = selectedCourseId == null,
            onClick = { onCourseSelected(null) },
            label = { Text("All") },
            modifier = Modifier.padding(end = 8.dp)
        )

        // Course tabs
        courses.forEach { course ->
            FilterChip(
                selected = selectedCourseId == course.id,
                onClick = { onCourseSelected(course.id) },
                label = { Text(course.subjectName ?: "Unknown Course") },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}