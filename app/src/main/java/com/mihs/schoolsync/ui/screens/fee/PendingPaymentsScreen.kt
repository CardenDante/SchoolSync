//// PendingPaymentsScreen.kt
//package com.mihs.schoolsync.ui.screens.fee
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Verified
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
//import com.mihs.schoolsync.ui.viewmodels.PaymentViewModel
//import com.mihs.schoolsync.utils.Result
//import com.mihs.schoolsync.utils.formatDate
//import com.mihs.schoolsync.utils.formatMoney
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PendingPaymentsScreen(
//    navigateBack: () -> Unit,
//    navigateToVerifyPayment: (Int) -> Unit,
//    viewModel: PaymentViewModel = hiltViewModel()
//) {
//    // Load pending payments
//    LaunchedEffect(true) {
//        viewModel.getPendingPayments()
//    }
//
//    val pendingPaymentsState = viewModel.pendingPayments.collectAsState().value
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Pending Payments") },
//                navigationIcon = {
//                    IconButton(onClick = navigateBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        when (pendingPaymentsState) {
//            is Result.Loading -> {
//                LoadingView(modifier = Modifier.padding(paddingValues))
//            }
//            is Result.Success -> {
//                val payments = pendingPaymentsState.data
//
//                if (payments.isEmpty()) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValues)
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Column(
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = "No pending payments",
//                                style = MaterialTheme.typography.titleLarge,
//                                textAlign = TextAlign.Center
//                            )
//                            Text(
//                                text = "All payments have been verified",
//                                style = MaterialTheme.typography.bodyMedium,
//                                textAlign = TextAlign.Center,
//                                color = Color.Gray
//                            )
//                        }
//                    }
//                } else {
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(paddingValues)
//                            .padding(horizontal = 16.dp)
//                    ) {
//                        item {
//                            Text(
//                                text = "${payments.size} pending payment(s)",
//                                modifier = Modifier.padding(vertical = 16.dp),
//                                style = MaterialTheme.typography.titleMedium
//                            )
//                        }
//
//                        items(payments) { payment ->
//                            PendingPaymentItem(
//                                payment = payment,
//                                onVerifyClick = { navigateToVerifyPayment(payment.id) }
//                            )
//                        }
//
//                        // Add some bottom padding
//                        item {
//                            Spacer(modifier = Modifier.height(72.dp))
//                        }
//                    }
//                }
//            }
//            is Result.Error -> {
//                ErrorView(
//                    message = pendingPaymentsState.message,
//                    onRetry = { viewModel.getPendingPayments() },
//                    modifier = Modifier.padding(paddingValues)
//                )
//            }
//            else -> { /* Initial state, do nothing */ }
//        }
//    }
//}
//
//@Composable
//fun PendingPaymentItem(
//    payment: Payment,
//    onVerifyClick: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp),
//        shape = RoundedCornerShape(12.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            // Student info
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    Text(
//                        text = payment.student?.let { "${it.firstName} ${it.lastName}" } ?: "Student #${payment.studentId}",
//                        fontWeight = FontWeight.Bold
//                    )
//                    payment.student?.admissionNumber?.let {
//                        Text(
//                            text = "Adm: $it",
//                            color = Color.Gray,
//                            style = MaterialTheme.typography.bodySmall
//                        )
//                    }
//                }
//
//                Surface(
//                    shape = RoundedCornerShape(50),
//                    color = AmberWarning.copy(alpha = 0.2f)
//                ) {
//                    Text(
//                        text = "Pending",
//                        color = AmberWarning,
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//                    )
//                }
//            }
//
//            Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//            // Payment details
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    Text(
//                        text = "Amount:",
//                        color = Color.Gray,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(
//                        text = "Reference:",
//                        color = Color.Gray,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(
//                        text = "Date:",
//                        color = Color.Gray,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(
//                        text = "Type:",
//                        color = Color.Gray,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                }
//
//                Column(horizontalAlignment = Alignment.End) {
//                    Text(
//                        text = formatMoney(payment.amount),
//                        fontWeight = FontWeight.Bold
//                    )
//                    Text(text = payment.bankReference)
//                    Text(text = formatDate(payment.paymentDate))
//                    Text(text = payment.paymentType.name.replace("_", " "))
//                }
//            }
//
//            if (!payment.parentNotes.isNullOrBlank()) {
//                Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//                Column {
//                    Text(
//                        text = "Notes:",
//                        color = Color.Gray,
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Text(text = payment.parentNotes)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Verify button
//            Button(
//                onClick = onVerifyClick,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Icon(
//                    Icons.Default.Verified,
//                    contentDescription = "Verify",
//                    modifier = Modifier.padding(end = 8.dp)
//                )
//                Text("Verify Payment")
//            }
//        }
//    }
//}