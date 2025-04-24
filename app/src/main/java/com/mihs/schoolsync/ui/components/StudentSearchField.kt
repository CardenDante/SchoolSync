// StudentSearchField.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun StudentSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    students: List<Student>,
    onStudentSelected: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = true
            },
            label = { Text("Search Student") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    focused = focusState.isFocused
                    if (focused && value.length >= 3) {
                        expanded = true
                    }
                },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded && focused && students.isNotEmpty() && value.length >= 3,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 300.dp),
            properties = PopupProperties(focusable = false)
        ) {
            LazyColumn {
                items(students.size) { index ->
                    val student = students[index]
                    StudentDropdownItem(
                        student = student,
                        onStudentSelected = {
                            onStudentSelected(student)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudentDropdownItem(
    student: Student,
    onStudentSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onStudentSelected)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = student.fullName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "ID: ${student.studentId}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}