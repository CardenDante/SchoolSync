// CreateFeeStructureScreen.kt
package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.finance.models.*
import com.mihs.schoolsync.ui.finance.viewmodel.FeeStructureViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFeeStructureScreen(
    navigateBack: () -> Unit,
    onSuccess: (Int) -> Unit,
    feeStructureViewModel: FeeStructureViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Structure form state
    var name by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("") }
    var applicableGrade by remember { mutableStateOf<String?>(null) }
    var isActive by remember { mutableStateOf(true) }
    var effectiveDate by remember { mutableStateOf(Date()) }

    // Fee items list
    var feeItems by remember { mutableStateOf(listOf<FeeItemCreateRequest>()) }

    // Add item dialog
    var showAddItemDialog by remember { mutableStateOf(false) }

    // Validation errors
    var nameError by remember { mutableStateOf<String?>(null) }
    var academicYearError by remember { mutableStateOf<String?>(null) }

    // Observe save state
    val saveState by feeStructureViewModel.saveState.collectAsState()

    // Handle save state changes
    LaunchedEffect(saveState) {
        when (saveState) {
            is FeeStructureViewModel.SaveState.Success -> {
                val structure = (saveState as FeeStructureViewModel.SaveState.Success).structure
                feeStructureViewModel.resetSaveState()
                onSuccess(structure.id)
            }
            else -> { /* Handle other states in the UI */ }
        }
    }

    // Submit function
    fun submitForm() {
        // Validate form
        var isValid = true

        if (name.isBlank()) {
            nameError = "Name is required"
            isValid = false
        } else {
            nameError = null
        }

        if (academicYear.isBlank()) {
            academicYearError = "Academic year is required"
            isValid = false
        } else {
            academicYearError = null
        }

        if (feeItems.isEmpty()) {
            // Show error toast or message about empty items
            isValid = false
        }

        if (!isValid) return

        val structure = FeeStructureCreateRequest(
            name = name,
            academicYear = academicYear,
            applicableGrade = applicableGrade,
            isActive = isActive,
            effectiveDate = effectiveDate
        )

        feeStructureViewModel.createFeeStructure(structure, feeItems)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Fee Structure") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Form fields
                Text(
                    text = "Structure Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = { Text("Structure Name") },
                    placeholder = { Text("Enter name") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Academic Year
                OutlinedTextField(
                    value = academicYear,
                    onValueChange = { academicYear = it; academicYearError = null },
                    label = { Text("Academic Year") },
                    placeholder = { Text("e.g. 2024-2025") },
                    isError = academicYearError != null,
                    supportingText = academicYearError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Applicable Grade
                OutlinedTextField(
                    value = applicableGrade ?: "",
                    onValueChange = { applicableGrade = if (it.isBlank()) null else it },
                    label = { Text("Applicable Grade (Optional)") },
                    placeholder = { Text("e.g. Grade 8 or All") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Active checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                    Text("Active Fee Structure")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider()

                Spacer(modifier = Modifier.height(24.dp))

                // Fee Items Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fee Items",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = { showAddItemDialog = true }
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Item",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Item")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fee Items List
                if (feeItems.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No fee items added yet. Add at least one fee item.",
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        feeItems.forEachIndexed { index, item ->
                            FeeItemCard(
                                item = item,
                                onRemove = {
                                    feeItems = feeItems.toMutableList().apply {
                                        removeAt(index)
                                    }
                                }
                            )
                        }
                    }

                    // Total amount
                    val totalAmount = feeItems.sumOf { it.amount }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Fee Amount",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = formatMoney(totalAmount),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = { submitForm() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = saveState !is FeeStructureViewModel.SaveState.Loading
                ) {
                    if (saveState is FeeStructureViewModel.SaveState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Create Fee Structure")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Error state
            if (saveState is FeeStructureViewModel.SaveState.Error) {
                val errorMessage = (saveState as FeeStructureViewModel.SaveState.Error).message
                AlertDialog(
                    onDismissRequest = { feeStructureViewModel.resetSaveState() },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        Button(onClick = { feeStructureViewModel.resetSaveState() }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }

    // Add Item Dialog
    if (showAddItemDialog) {
        AddFeeItemDialog(
            onDismiss = { showAddItemDialog = false },
            onAddItem = { newItem ->
                feeItems = feeItems + newItem
                showAddItemDialog = false
            }
        )
    }
}

@Composable
fun FeeItemCard(
    item: FeeItemCreateRequest,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.feeType.toString().replace("_", " "),
                        fontWeight = FontWeight.Bold
                    )
                    item.description?.let {
                        if (it.isNotBlank()) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Text(
                    text = formatMoney(item.amount),
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove Item",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Additional details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item.term?.let {
                    if (it.isNotBlank()) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text("Term: $it") }
                        )
                    }
                }

                if (item.isRecurring) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Recurring") }
                    )
                }

                if (item.appliesToNewStudents) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text("New Students") }
                    )
                }
            }
        }
    }
}