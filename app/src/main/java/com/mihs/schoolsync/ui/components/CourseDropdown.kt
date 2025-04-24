// CourseDropdown.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.mihs.schoolsync.data.models.CourseOffering

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDropdown(
    courses: List<CourseOffering>,
    selectedCourseId: Int?,
    onCourseSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = courses.find { it.id == selectedCourseId }?.subjectName ?: "Select Course",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            courses.forEach { course ->
                DropdownMenuItem(
                    text = {
                        Text(course.subjectName ?: "Unknown Course")
                    },
                    onClick = {
                        onCourseSelected(course.id)
                        expanded = false
                    }
                )
            }
        }
    }
}