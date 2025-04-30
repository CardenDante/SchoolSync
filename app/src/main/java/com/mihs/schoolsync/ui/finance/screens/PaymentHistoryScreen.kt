// PaymentHistoryScreen.kt
package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.finance.models.FinancePaymentStatus
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import com.mihs.schoolsync.ui.finance.viewmodel.FeeViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    studentId: Int,
    navigateBack: () -> Unit,
    onGenerateReceipt: (Int) -> Unit, // Navigate to receipt screen for a payment
    feeViewModel: FeeViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Load payment history and student info
    LaunchedEffect(studentId) {
        feeViewModel.getStudentPayments(studentId)
        studentViewModel.getStudent(studentId)
    }

    // Observe states
    val paymentsState by feeViewModel.paymentsHistoryState.collectAsState()
    val studentState by studentViewModel.studentDetailState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment History") },
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
            when {
                paymentsState is FeeViewModel.PaymentsHistoryState.Loading ||
                        studentState is StudentViewModel.StudentDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                paymentsState is FeeViewModel.PaymentsHistoryState.Error -> {
                    val errorMessage = (paymentsState as FeeViewModel.PaymentsHistoryState.Error).message
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
                            onClick = { feeViewModel.getStudentPayments(studentId) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                paymentsState is FeeViewModel.PaymentsHistoryState.Success -> {
                    val payments = (paymentsState as FeeViewModel.PaymentsHistoryState.Success).payments
                    val student = if (studentState is StudentViewModel.StudentDetailState.Success) {
                        (studentState as StudentViewModel.StudentDetailState.Success).student
                    } else null

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Student Info (if available)
                        student?.let {
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
                                        fontWeight = FontWeight.Bold
                                    )
                                    student.currentClass?.let {
                                        Text(
                                            text = "Class: $it",
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        // Payment History List
                        Text(
                            text = "Payment History",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (payments.isEmpty()) {
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
                                        text = "No payment history found",
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(payments) { payment ->
                                    PaymentHistoryItem(
                                        payment = payment,
                                        onGenerateReceiptClick = { onGenerateReceipt(payment.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading payment history...")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryItem(
    payment: PaymentResponse,
    onGenerateReceiptClick: () -> Unit
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
            // Payment details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatMoney(payment.amount),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Ref: ${payment.bankReference}",
                        color = Color.Gray
                    )
                }

                // Status chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = getStatusColor(payment.status),
                    contentColor = Color.White
                ) {
                    Text(
                        text = formatStatus(payment.status),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Payment Date:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatPaymentDate(payment.paymentDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = "Submitted:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatPaymentDate(payment.submissionDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Parent notes if available
            payment.parentNotes?.let {
                if (it.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Notes: $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Admin notes if available and payment is verified/rejected
            if (payment.status != FinancePaymentStatus.PENDING) {
                payment.adminNotes?.let {
                    if (it.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Admin Notes: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Generate receipt button if payment is verified
            if (payment.status == FinancePaymentStatus.VERIFIED ||
                payment.status == FinancePaymentStatus.PARTIALLY_VERIFIED) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onGenerateReceiptClick,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = "Generate Receipt",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Receipt")
                }
            }
        }
    }
}

// Helper functions for formatting
fun formatStatus(status: FinancePaymentStatus): String {
    return when (status) {
        FinancePaymentStatus.PENDING -> "Pending"
        FinancePaymentStatus.VERIFIED -> "Verified"
        FinancePaymentStatus.REJECTED -> "Rejected"
        FinancePaymentStatus.PARTIALLY_VERIFIED -> "Partially Verified"
    }
}

fun getStatusColor(status: FinancePaymentStatus): Color {
    return when (status) {
        FinancePaymentStatus.PENDING -> Color(0xFFFFA000) // Amber
        FinancePaymentStatus.VERIFIED -> Color(0xFF43A047) // Green
        FinancePaymentStatus.REJECTED -> Color(0xFFE53935) // Red
        FinancePaymentStatus.PARTIALLY_VERIFIED -> Color(0xFF1E88E5) // Blue
    }
}

private fun formatPaymentDate(date: Date): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
}