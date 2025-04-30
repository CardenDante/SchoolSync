//// FeeModule.kt
//package com.mihs.schoolsync.ui.finance
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import com.mihs.schoolsync.models.FeeStructure
//import com.mihs.schoolsync.models.PaymentStatus
//import com.mihs.schoolsync.data.models.Student
//import com.mihs.schoolsync.navigation.*
//import com.mihs.schoolsync.ui.components.ErrorView
//import com.mihs.schoolsync.ui.components.LoadingView
//import com.mihs.schoolsync.ui.theme.AmberWarning
//import com.mihs.schoolsync.ui.viewmodel.PaymentViewModel
//import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
//import com.mihs.schoolsync.utils.Result
//
///**
// * Finance Dashboard for the SchoolSync application
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FinanceDashboard(
//    navController: NavController,
//    paymentViewModel: PaymentViewModel,
//    studentViewModel: StudentViewModel,
//    isAdmin: Boolean = false // Determine if current user is admin
//) {
//    // Load pending payments for admin and student list
//    LaunchedEffect(true) {
//        if (isAdmin) {
//            paymentViewModel.getPendingPayments()
//        }
//        studentViewModel.getStudents()
//    }
//
//    val pendingPaymentsState = paymentViewModel.pendingPayments.collectAsState().value
//    val studentsState = studentViewModel.students.collectAsState().value
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Finance") },
//                actions = {
//                    if (isAdmin) {
//                        // Admin actions
//                        IconButton(onClick = { navController.navigate(FEE_STRUCTURE_ROUTE) }) {
//                            Icon(Icons.Default.List, contentDescription = "Fee Structures")
//                        }
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            item {
//                Text(
//                    text = "Finance Management",
//                    style = MaterialTheme.typography.headlineSmall,
//                    modifier = Modifier.padding(vertical = 16.dp)
//                )
//            }
//
//            // Admin-specific items
//            if (isAdmin) {
//                item {
//                    // Pending Payments Card
//                    FinanceActionCard(
//                        title = "Pending Payments",
//                        icon = Icons.Default.Payments,
//                        description = when (pendingPaymentsState) {
//                            is Result.Success -> "${pendingPaymentsState.data.size} payment(s) awaiting verification"
//                            is Result.Loading -> "Loading pending payments..."
//                            is Result.Error -> "Error: ${pendingPaymentsState.message}"
//                            else -> "View and verify pending payments"
//                        },
//                        actionText = "View",
//                        onClick = { navController.navigate(PAYMENTS_PENDING_ROUTE) },
//                        badgeCount = if (pendingPaymentsState is Result.Success) pendingPaymentsState.data.size else null
//                    )
//                }
//
//                item {
//                    // Fee Structures Card
//                    FinanceActionCard(
//                        title = "Fee Structures",
//                        icon = Icons.Default.AccountBalance,
//                        description = "Manage fee structures and assignments",
//                        actionText = "Manage",
//                        onClick = { navController.navigate(FEE_STRUCTURE_ROUTE) }
//                    )
//                }
//
//                item {
//                    Text(
//                        text = "Student Fees",
//                        style = MaterialTheme.typography.titleLarge,
//                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
//                    )
//                }
//
//                // Student list for admin
//                when (studentsState) {
//                    is Result.Loading -> {
//                        item {
//                            LoadingView(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(200.dp)
//                            )
//                        }
//                    }
//                    is Result.Success -> {
//                        items(studentsState.data) { student ->
//                            StudentFeeItem(
//                                student = student,
//                                onStatementClick = {
//                                    navController.navigate("$FEE_STATEMENT_ROUTE/${student.id}")
//                                }
//                            )
//                        }
//                    }
//                    is Result.Error -> {
//                        item {
//                            ErrorView(
//                                message = studentsState.message,
//                                onRetry = { studentViewModel.getStudents() },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(200.dp)
//                            )
//                        }
//                    }
//                    else -> { /* Initial state, do nothing */ }
//                }
//            } else {
//                // Parent/Student view
//                // For demo purposes, we'll just show a single student
//                // In a real app, you'd get the student associated with the parent
//                when (studentsState) {
//                    is Result.Success -> {
//                        val student = studentsState.data.firstOrNull()
//                        if (student != null) {
//                            item {
//                                // Current Balance Card
//                                FinanceActionCard(
//                                    title = "Fee Balance",
//                                    icon = Icons.Default.AccountBalance,
//                                    description = "View your current fee balance and statement",
//                                    actionText = "View Statement",
//                                    onClick = { navController.navigate("$FEE_STATEMENT_ROUTE/${student.id}") }
//                                )
//                            }
//
//                            item {
//                                // Make Payment Card
//                                FinanceActionCard(
//                                    title = "Make Payment",
//                                    icon = Icons.Default.Payment,
//                                    description = "Submit a new fee payment",
//                                    actionText = "Pay Now",
//                                    onClick = { navController.navigate("$PAYMENT_SUBMIT_ROUTE/${student.id}") }
//                                )
//                            }
//
//                            item {
//                                // Payment History Card
//                                FinanceActionCard(
//                                    title = "Payment History",
//                                    icon = Icons.Default.History,
//                                    description = "View your payment history and receipts",
//                                    actionText = "View History",
//                                    onClick = { navController.navigate("$PAYMENT_STUDENT_HISTORY_ROUTE/${student.id}") }
//                                )
//                            }
//                        } else {
//                            item {
//                                Text(
//                                    text = "No student information found",
//                                    style = MaterialTheme.typography.bodyLarge,
//                                    modifier = Modifier.padding(32.dp)
//                                )
//                            }
//                        }
//                    }
//                    is Result.Loading -> {
//                        item {
//                            LoadingView(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(200.dp)
//                            )
//                        }
//                    }
//                    is Result.Error -> {
//                        item {
//                            ErrorView(
//                                message = studentsState.message,
//                                onRetry = { studentViewModel.getStudents() },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(200.dp)
//                            )
//                        }
//                    }
//                    else -> { /* Initial state, do nothing */ }
//                }
//            }
//
//            // Add some bottom padding
//            item {
//                Spacer(modifier = Modifier.height(72.dp))
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun FinanceActionCard(
//    title: String,
//    icon: ImageVector,
//    description: String,
//    actionText: String,
//    onClick: () -> Unit,
//    badgeCount: Int? = null
//) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(12.dp),
//        onClick = onClick
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Box {
//                Icon(
//                    icon,
//                    contentDescription = null,
//                    modifier = Modifier.size(48.dp),
//                    tint = MaterialTheme.colorScheme.primary
//                )
//
//                // Optional badge for counters
//                badgeCount?.let { count ->
//                    if (count > 0) {
//                        Badge(
//                            containerColor = AmberWarning,
//                            modifier = Modifier.align(Alignment.TopEnd)
//                        ) {
//                            Text(count.toString())
//                        }
//                    }
//                }
//            }
//
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(start = 16.dp)
//            ) {
//                Text(
//                    text = title,
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold
//                )
//                Text(
//                    text = description,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.Gray
//                )
//            }
//
//            Button(onClick = onClick) {
//                Text(actionText)
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StudentFeeItem(
//    student: Student,
//    onStatementClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        shape = RoundedCornerShape(8.dp),
//        onClick = onStatementClick
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Avatar or icon
//            Surface(
//                shape = CircleShape,
//                color = MaterialTheme.colorScheme.primaryContainer,
//                modifier = Modifier.size(40.dp)
//            ) {
//                Box(contentAlignment = Alignment.Center) {
//                    Text(
//                        text = student.firstName.firstOrNull()?.toString() ?: "S",
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//            }
//
//            // Student details
//            Column(
//                modifier = Modifier
//                    .weight(1f)
//                    .padding(start = 16.dp)
//            ) {
//                Text(
//                    text = "${student.firstName} ${student.lastName}",
//                    fontWeight = FontWeight.Bold
//                )
//
//                Row {
//                    student.admissionNumber?.let {
//                        Text(
//                            text = "Adm: $it",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = Color.Gray
//                        )
//                    }
//
//                    student.currentClass?.let {
//                        Text(
//                            text = " | Class: ${it.name}",
//                            style = MaterialTheme.typography.bodySmall,
//                            color = Color.Gray
//                        )
//                    }
//                }
//            }
//
//            // Action icon
//            IconButton(onClick = onStatementClick) {
//                Icon(
//                    Icons.Default.Receipt,
//                    contentDescription = "Fee Statement",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//    }
//}