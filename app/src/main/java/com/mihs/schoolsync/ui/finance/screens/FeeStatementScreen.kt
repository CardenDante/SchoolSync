// FeeStatementScreen.kt
package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.data.models.StudentDetail
import com.mihs.schoolsync.ui.finance.models.FeeTransactionResponse
import com.mihs.schoolsync.ui.finance.models.FinanceTransactionType
import com.mihs.schoolsync.ui.finance.viewmodel.FeeViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeeStatementScreen(
    studentId: Int,
    navigateBack: () -> Unit,
    navigateToPayment: () -> Unit,
    feeViewModel: FeeViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    // Load fee statement for student
    LaunchedEffect(studentId) {
        feeViewModel.getFeeStatement(studentId)
        studentViewModel.getStudent(studentId)
    }

    // Observe states
    val feeStatementState by feeViewModel.feeStatementState.collectAsState()
    val studentState by studentViewModel.studentDetailState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fee Statement") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = navigateToPayment,
                icon = { Icon(Icons.Default.Payment, contentDescription = "Make Payment") },
                text = { Text("Make Payment") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Student information and fee statement
            when {
                feeStatementState is FeeViewModel.FeeStatementState.Loading ||
                        studentState is StudentViewModel.StudentDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                feeStatementState is FeeViewModel.FeeStatementState.Error -> {
                    val errorMessage = (feeStatementState as FeeViewModel.FeeStatementState.Error).message
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
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Button(
                            onClick = { feeViewModel.getFeeStatement(studentId) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                feeStatementState is FeeViewModel.FeeStatementState.Success &&
                        studentState is StudentViewModel.StudentDetailState.Success -> {
                    // Get data from the state
                    val feeStatement = (feeStatementState as FeeViewModel.FeeStatementState.Success).statement
                    val student = (studentState as StudentViewModel.StudentDetailState.Success).student

                    // Display the fee statement
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Student Info Card
                        StudentInfoCard(student)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fee Balance Card
                        FeeBalanceCard(feeStatement.currentBalance)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Transactions
                        Text(
                            text = "Transactions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (feeStatement.transactions.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No transactions found",
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn {
                                items(feeStatement.transactions) { transaction ->
                                    TransactionItem(transaction)
                                }
                            }
                        }
                    }
                }

                else -> {
                    // Idle state or other unexpected states
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading fee statement...")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentInfoCard(student: StudentDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = student.studentId,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Admission: ${student.studentId}",
                color = Color.Gray
            )
            student.currentClass?.let {
                Text(
                    text = "Class: $it",
                    color = Color.Gray
                )
            }
            student.currentAcademicYear?.let {
                Text(
                    text = "Academic Year: $it",
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun FeeBalanceCard(balance: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Current Balance",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = formatMoney(balance),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = if (balance > 0) Color.Red else Color.Green
            )
        }
    }
}

@Composable
fun TransactionItem(transaction: FeeTransactionResponse) {
    val isPayment = transaction.transactionType == FinanceTransactionType.PAYMENT ||
            transaction.transactionType == FinanceTransactionType.WAIVER ||
            transaction.transactionType == FinanceTransactionType.ADJUSTMENT

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.description ?: transaction.transactionType.toString(),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatDate(transaction.transactionDate),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                transaction.academicTerm?.let {
                    Text(
                        text = "Term: $it",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Text(
                text = if (isPayment) "-${formatMoney(transaction.amount)}" else "+${formatMoney(transaction.amount)}",
                fontWeight = FontWeight.Bold,
                color = if (isPayment) Color.Green else Color.Red
            )
        }
    }
}

// Helper Functions
fun formatMoney(amount: Double): String {
    return "KES ${String.format("%,.2f", amount)}"
}

fun formatDate(date: Date): String {
    return SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(date)
}