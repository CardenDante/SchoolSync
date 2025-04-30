// FeeStructureScreen.kt
package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.finance.models.FeeStructureResponse
import com.mihs.schoolsync.ui.finance.viewmodel.FeeStructureViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeStructureScreen(
    navigateBack: () -> Unit,
    onCreateNew: () -> Unit,
    onEditStructure: (Int) -> Unit,
    feeStructureViewModel: FeeStructureViewModel = hiltViewModel()
) {
    // Load fee structures
    LaunchedEffect(Unit) {
        feeStructureViewModel.getFeeStructures()
    }

    // Observe fee structures state
    val feeStructuresState by feeStructureViewModel.feeStructuresState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fee Structures") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Add new fee structure button for admin
                    IconButton(onClick = onCreateNew) {
                        Icon(Icons.Default.Add, contentDescription = "Add New Fee Structure")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateNew) {
                Icon(Icons.Default.Add, contentDescription = "Create New Fee Structure")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (feeStructuresState) {
                is FeeStructureViewModel.FeeStructuresState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is FeeStructureViewModel.FeeStructuresState.Error -> {
                    val errorMessage = (feeStructuresState as FeeStructureViewModel.FeeStructuresState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { feeStructureViewModel.getFeeStructures() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is FeeStructureViewModel.FeeStructuresState.Success -> {
                    val feeStructures = (feeStructuresState as FeeStructureViewModel.FeeStructuresState.Success).structures

                    if (feeStructures.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No Fee Structures Found",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Create a new fee structure to get started",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Button(
                                onClick = onCreateNew,
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Create",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Create New")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(feeStructures) { feeStructure ->
                                FeeStructureItem(
                                    feeStructure = feeStructure,
                                    onEditClick = { onEditStructure(feeStructure.id) }
                                )
                            }
                        }
                    }
                }

                else -> {
                    // Idle state, do nothing
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeStructureItem(
    feeStructure: FeeStructureResponse,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        onClick = onEditClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row with name and active status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = feeStructure.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (feeStructure.isActive) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF43A047), // Green for active
                        contentColor = Color.White
                    ) {
                        Text(
                            text = "Active",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Academic year and term
            Text(
                text = "Academic Year: ${feeStructure.academicYear}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Grade/Class: ${feeStructure.applicableGrade ?: "All Classes"}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fee items summary
            Text(
                text = "Fee Items: ${feeStructure.items.size}",
                style = MaterialTheme.typography.bodyMedium
            )

            // Total amount
            val totalAmount = feeStructure.items.sumOf { it.amount }
            Text(
                text = "Total: ${formatMoney(totalAmount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Edit button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onEditClick
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        }
    }
}