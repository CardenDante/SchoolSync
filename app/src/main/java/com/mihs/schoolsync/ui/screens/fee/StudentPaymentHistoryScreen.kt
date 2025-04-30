//// StudentPaymentHistoryScreen.kt
//package com.mihs.schoolsync.ui.screens.fee
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Receipt
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.mihs.schoolsync.models.Payment
//import com.mihs.schoolsync.models.PaymentStatus
//import com.mihs.schoolsync.ui.components.ErrorView
//import com.mihs.schoolsync.ui.components.LoadingView
//import com.mihs.schoolsync.ui.theme.AmberWarning
//import com.mihs.schoolsync.ui.theme.GreenSuccess
//import com.mihs.schoolsync.ui.theme.RedError
//import com.mihs.schoolsync.ui.viewmodels.PaymentViewModel
//import com.mihs.schoolsync.ui.viewmodels.StudentViewModel
//import com.mihs.schoolsync.utils.Result
//import com.mihs.schoolsync.utils.formatDate
//import com.mihs.schoolsync.utils.formatMoney
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StudentPaymentHistoryScreen(
//    studentId: Int,
//    navigateBack: () -> Unit,
//    navigateToReceipt: (Int) -> Unit,
//    paymentViewModel: PaymentViewModel = hiltViewModel(),
//    studentViewModel: StudentViewModel = hiltViewModel()
//) {
//    // Load student data and payments
//    LaunchedEffect(studentId) {
//        studentViewModel.getStudent(studentId)
//        paymentViewModel.getStudentPayments(studentId)
//    }
//
//    val studentState = studentViewModel.student.collectAsState().value
//    val paymentsState = paymentViewModel.studentPayments.collectAsState().value
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Payment History") },
//                navigationIcon = {
//                    IconButton(onClick = navigateBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            // Student Info Card (if student data is available)
//            when (studentState) {
//                is Result.Success -> {
//                    val student = studentState.data
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        ) {
//                            Text(
//                                text = "${student.firstName} ${student.lastName}",
//                                style = MaterialTheme.typography.titleLarge
//                            )
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.spacedBy(16.dp)
//                            ) {
//                                student.admissionNumber?.let {
//                                    Text(
//                                        text = "Adm: $it",
//                                        color = Color.Gray,
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//                                student.currentClass?.let {
//                                    Text(
//                                        text = "Class: ${it.name}",
//                                        color = Color.Gray,
//                                        style = MaterialTheme.typography.bodyMedium
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//                is Result.Loading -> {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(100.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        CircularProgressIndicator()
//                    }
//                }
//                is Result.Error -> {
//                    // Just show a minimal error, as the main focus is the payment history
//                    Text(
//                        text = "Error loading student info: ${studentState.message}",
//                        color = MaterialTheme.colorScheme.error,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                }
//                else -> { /* Initial state, do nothing */ }
//            }
//
//            // Payments List
//            when (paymentsState) {
//                is Result.Loading -> {
//                    LoadingView()
//                }
//                is Result.Success -> {
//                    val payments = paymentsState.data
//
//                    if (payments.isEmpty()) {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(32.dp),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text(
//                                text = "No payment records found",
//                                color = Color.Gray,
//                                textAlign = TextAlign.Center
//                            )
//                        }
//                    } else {
//                        LazyColumn(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp)
//                        ) {
//                            item {
//                                Text(
//                                    text = "Payment History",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    modifier = Modifier.padding(vertical = 8.dp)
//                                )
//                            }
//
//                            items(payments) { payment ->
//                                PaymentHistoryItem(
//                                    payment = payment,
//                                    onReceiptClick = {
//                                        if (payment.status == PaymentStatus.VERIFIED) {
//                                            navigateToReceipt(payment.id)
//                                        }
//                                    }
//                                )
//                            }
//
//                            // Add some bottom padding
//                            item {
//                                Spacer(modifier = Modifier.height(16.dp))
//                            }
//                        }
//                    }
//                }
//                is Result.Error -> {
//                    ErrorView(
//                        message = paymentsState.message,
//                        onRetry = { paymentViewModel.getStudentPayments(studentId) }
//                    )
//                }
//                else -> { /* Initial state, do nothing */ }
//            }
//        }
//    }
//}
//
//@Composable
//fun PaymentHistoryItem(
//    payment: Payment,
//    onReceiptClick: () -> Unit
//) {
//    val statusColor = when (payment.status) {
//        PaymentStatus.VERIFIED -> GreenSuccess
//        PaymentStatus.PARTIALLY_VERIFIED -> GreenSuccess
//        PaymentStatus.PENDING -> AmberWarning
//        PaymentStatus.REJECTED -> RedError
//    }
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(
//                        text = formatMoney(payment.amount),
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(
//                        text = "Ref: ${payment.bankReference}",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(
//                        text = "Submitted: ${formatDate(payment.submissionDate)}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.Gray
//                    )
//                }
//
//                Surface(
//                    shape = RoundedCornerShape(50),
//                    color = statusColor.copy(alpha = 0.2f)
//                ) {
//                    Text(
//                        text = payment.status.name.replace("_", " "),
//                        color = statusColor,
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                    )
//                }
//            }
//
//            Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column {
//                    Text(
//                        text = "Payment Date: ${formatDate(payment.paymentDate)}",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(
//                        text = "Payment Method: ${payment.paymentType.name.replace("_", " ")}",
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    payment.verificationDate?.let {
//                        Text(
//                            text = "Verified: ${formatDate(it)}",
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                }
//
//                // Receipt button (only for verified payments)
//                if (payment.status == PaymentStatus.VERIFIED) {
//                    IconButton(onClick = onReceiptClick) {
//                        Icon(
//                            Icons.Default.Receipt,
//                            contentDescription = "View Receipt",
//                            tint = MaterialTheme.colorScheme.primary
//                        )
//                    }
//                }
//            }
//        }
//    }
//}