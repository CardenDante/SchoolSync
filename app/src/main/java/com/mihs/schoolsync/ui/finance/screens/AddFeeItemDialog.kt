// AddFeeItemDialog.kt
package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mihs.schoolsync.ui.finance.models.FeeItemCreateRequest
import com.mihs.schoolsync.ui.finance.models.FinanceFeeType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeeItemDialog(
    onDismiss: () -> Unit,
    onAddItem: (FeeItemCreateRequest) -> Unit
) {
    // Form state
    var selectedFeeType by remember { mutableStateOf(FinanceFeeType.TUITION) }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var term by remember { mutableStateOf("") }
    var isRecurring by remember { mutableStateOf(false) }
    var appliesToNewStudents by remember { mutableStateOf(false) }

    // Validation state
    var amountError by remember { mutableStateOf<String?>(null) }

    // Fee type dropdown
    var expandedFeeTypeDropdown by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Add Fee Item",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fee Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedFeeTypeDropdown,
                    onExpandedChange = { expandedFeeTypeDropdown = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedFeeType.toString().replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Fee Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFeeTypeDropdown)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedFeeTypeDropdown,
                        onDismissRequest = { expandedFeeTypeDropdown = false }
                    ) {
                        FinanceFeeType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.toString().replace("_", " ")) },
                                onClick = {
                                    selectedFeeType = type
                                    expandedFeeTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("Enter description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = if (it.toDoubleOrNull() == null || it.toDoubleOrNull()!! <= 0) {
                            "Please enter a valid amount"
                        } else {
                            null
                        }
                    },
                    label = { Text("Amount") },
                    isError = amountError != null,
                    supportingText = amountError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Term
                OutlinedTextField(
                    value = term,
                    onValueChange = { term = it },
                    label = { Text("Term (Optional)") },
                    placeholder = { Text("e.g. Term 1, Semester 1") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Checkboxes
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it }
                    )
                    Text("Recurring Fee")
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = appliesToNewStudents,
                        onCheckedChange = { appliesToNewStudents = it }
                    )
                    Text("Applies to New Students Only")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            // Validate form
                            val amountVal = amount.toDoubleOrNull()
                            if (amountVal == null || amountVal <= 0) {
                                amountError = "Please enter a valid amount"
                                return@Button
                            }

                            val newItem = FeeItemCreateRequest(
                                feeType = selectedFeeType,
                                description = if (description.isBlank()) null else description,
                                amount = amountVal,
                                term = if (term.isBlank()) null else term,
                                isRecurring = isRecurring,
                                appliesToNewStudents = appliesToNewStudents
                            )

                            onAddItem(newItem)
                        }
                    ) {
                        Text("Add Item")
                    }
                }
            }
        }
    }
}