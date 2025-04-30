// CommonUIComponents.kt
package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * A reusable dropdown selector component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSelector(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    optionLabel: (T) -> String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = optionLabel(selectedOption))
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Select")
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * A reusable loading view component
 */
@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * A reusable error view component with retry option
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onRetry,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Retry",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Retry")
        }
    }
}

/**
 * A confirmation dialog component
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}

/**
 * A custom dialog component with content slot
 */
@Composable
fun CustomDialog(
    title: String,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
    buttons: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f, fill = false)) {
                    content()
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    buttons()
                }
            }
        }
    }
}

/**
 * Date picker component
 */
@Composable
fun DatePickerField(
    label: String,
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDate(java.util.Date(value)),
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Select Date"
                )
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                onValueChange(it)
                showDatePicker = false
            },
            initialSelectedDateMillis = value
        )
    }
}

/**
 * Simple date picker dialog
 */
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit,
    initialSelectedDateMillis: Long
) {
    // This is a simplified date picker implementation
    // In a real app, you would use a more sophisticated date picker library
    // like the Material Design date picker or a third-party library

    val calendar = remember { java.util.Calendar.getInstance() }
    calendar.timeInMillis = initialSelectedDateMillis

    var year by remember { mutableStateOf(calendar.get(java.util.Calendar.YEAR)) }
    var month by remember { mutableStateOf(calendar.get(java.util.Calendar.MONTH)) }
    var day by remember { mutableStateOf(calendar.get(java.util.Calendar.DAY_OF_MONTH)) }

    CustomDialog(
        title = "Select Date",
        onDismiss = onDismissRequest,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Simple number pickers for year, month, day
                // In a real app, you would use a more sophisticated date picker

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Day picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Day")
                        NumberPicker(
                            value = day,
                            onValueChange = { day = it },
                            range = 1..31
                        )
                    }

                    // Month picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Month")
                        NumberPicker(
                            value = month + 1, // +1 because Calendar.MONTH is 0-based
                            onValueChange = { month = it - 1 },
                            range = 1..12
                        )
                    }

                    // Year picker
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Year")
                        NumberPicker(
                            value = year,
                            onValueChange = { year = it },
                            range = 2000..2100
                        )
                    }
                }
            }
        },
        buttons = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }

            Button(
                onClick = {
                    calendar.set(year, month, day)
                    onDateSelected(calendar.timeInMillis)
                }
            ) {
                Text("OK")
            }
        }
    )
}

/**
 * Simple number picker
 */
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                if (value < range.last) {
                    onValueChange(value + 1)
                }
            }
        ) {
            Text("▲")
        }

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge
        )

        IconButton(
            onClick = {
                if (value > range.first) {
                    onValueChange(value - 1)
                }
            }
        ) {
            Text("▼")
        }
    }
}

/**
 * Format date to string
 */
fun formatDate(date: java.util.Date): String {
    val formatter = java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault())
    return formatter.format(date)
}