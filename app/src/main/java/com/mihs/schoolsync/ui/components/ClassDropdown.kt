// ClassDropdown.kt
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
import com.mihs.schoolsync.data.models.ClassSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDropdown(
    classes: List<ClassSection>,
    selectedClassId: Int?,
    onClassSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        TextField(
            value = classes.find { it.id == selectedClassId }?.name ?: "Select Class Section",
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
            classes.forEach { classSection ->
                DropdownMenuItem(
                    text = { Text(classSection.name) },
                    onClick = {
                        onClassSelected(classSection.id)
                        expanded = false
                    }
                )
            }
        }
    }
}