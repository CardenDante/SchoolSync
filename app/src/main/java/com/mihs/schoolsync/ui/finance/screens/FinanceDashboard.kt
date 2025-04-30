// FinanceDashboard.kt
package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation.NavController
import com.mihs.schoolsync.data.models.StudentDetail
import com.mihs.schoolsync.ui.finance.viewmodel.FeeViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceDashboard(
    navController: NavController,
    isAdmin: Boolean,
    feeViewModel: FeeViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    // Load student data
    LaunchedEffect(Unit) {
        studentViewModel.getStudents()
    }

    // Observe student list state
    val studentListState by studentViewModel.studentListState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            when (studentListState) {
                is StudentViewModel.StudentListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is StudentViewModel.StudentListState.Success -> {
                    val students = (studentListState as StudentViewModel.StudentListState.Success).response.items

                    if (isAdmin) {
                        AdminFinanceView(
                            students = students,
                            navController = navController
                        )
                    } else {
                        ParentFinanceView(
                            students = students,
                            navController = navController
                        )
                    }
                }
                is StudentViewModel.StudentListState.Error -> {
                    val error = (studentListState as StudentViewModel.StudentListState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error loading students",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { studentViewModel.getStudents() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
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

@Composable
fun AdminFinanceView(
    students: List<StudentDetail>,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Quick Action Cards
        Text(
            text = "Finance Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Action cards row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FinanceActionCard(
                title = "Fee Structures",
                icon = Icons.Default.ListAlt,
                onClick = {
                    // Navigate to fee structures
                },
                modifier = Modifier.weight(1f)
            )

            FinanceActionCard(
                title = "Pending Payments",
                icon = Icons.Default.Payments,
                onClick = {
                    // Navigate to pending payments
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Students list
        Text(
            text = "Students",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (students.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No students found")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(students) { student ->
                    StudentFinanceCard(
                        student = student,
                        onClick = {
                            // Navigate to fee statement
                            navController.navigate("fee_statement/${student.id}")
                        }
                    )
                }

                // Add bottom padding
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }
}

@Composable
fun ParentFinanceView(
    students: List<StudentDetail>,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Finance",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // For parent view, just show first student
        val student = students.firstOrNull()

        if (student == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No student information available")
            }
        } else {
            // Fee balance card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = student.studentId,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    student.currentClass?.let {
                        Text(
                            text = "Class: $it",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Current Fee Balance",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = formatMoney(student.feeBalance ?: 0.0),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if ((student.feeBalance ?: 0.0) > 0) MaterialTheme.colorScheme.error else Color.Green,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navController.navigate("fee_statement/${student.id}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Receipt,
                            contentDescription = "View Statement",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("View Fee Statement")
                    }
                }
            }

            // Actions card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Payment Options",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Button(
                        onClick = { navController.navigate("payment_submit/${student.id}") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Payment,
                            contentDescription = "Make Payment",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Make Payment")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { /* Navigate to payment history */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "Payment History",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Payment History")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
                    .padding(bottom = 8.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentFinanceCard(
    student: StudentDetail,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = student.studentId.firstOrNull()?.toString()?.uppercase() ?: "S",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Student details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = student.studentId,
                    fontWeight = FontWeight.Bold
                )

                student.currentClass?.let {
                    Text(
                        text = "Class: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Fee balance
                student.feeBalance?.let {
                    Text(
                        text = "Balance: ${formatMoney(it)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (it > 0) MaterialTheme.colorScheme.error else Color.Green,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Arrow icon
            IconButton(onClick = onClick) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "View Details"
                )
            }
        }
    }
}