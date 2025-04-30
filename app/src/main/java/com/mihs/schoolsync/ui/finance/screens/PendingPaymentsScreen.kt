package com.mihs.schoolsync.ui.finance.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mihs.schoolsync.ui.finance.models.FinancePaymentStatus
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import com.mihs.schoolsync.ui.finance.viewmodel.PaymentVerificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingPaymentsScreen(
    navigateBack: () -> Unit,
    onVerifySuccess: () -> Unit,
    paymentViewModel: PaymentVerificationViewModel = hiltViewModel()
) {
    // Load pending payments
    LaunchedEffect(Unit) {
        paymentViewModel.getPendingPayments()
    }

    // Observe pending payments state
    val pendingPaymentsState by paymentViewModel.pendingPaymentsState.collectAsState()
    val verificationState by paymentViewModel.verificationState.collectAsState()

    // State for selected payment and verification dialog
    var selectedPayment by remember { mutableStateOf<PaymentResponse?>(null) }
    var showVerificationDialog by remember { mutableStateOf(false) }
    var showBankSlipDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pending Payments") },
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
            when (pendingPaymentsState) {
                is PaymentVerificationViewModel.PendingPaymentsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is PaymentVerificationViewModel.PendingPaymentsState.Error -> {
                    val errorMessage = (pendingPaymentsState as PaymentVerificationViewModel.PendingPaymentsState.Error).message
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
                            onClick = { paymentViewModel.getPendingPayments() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is PaymentVerificationViewModel.PendingPaymentsState.Success -> {
                    val payments = (pendingPaymentsState as PaymentVerificationViewModel.PendingPaymentsState.Success).payments

                    if (payments.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No pending payments to verify",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(payments) { payment ->
                                PendingPaymentItem(
                                    payment = payment,
                                    onVerifyClick = {
                                        selectedPayment = payment
                                        showVerificationDialog = true
                                    },
                                    onViewBankSlipClick = {
                                        if (payment.bankSlipUrl != null) {
                                            selectedPayment = payment
                                            showBankSlipDialog = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                else -> {
                    // Idle state, do nothing
                }
            }

            // Handle verification loading and success states
            when (verificationState) {
                is PaymentVerificationViewModel.VerificationState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .wrapContentHeight(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Verifying payment...")
                            }
                        }
                    }
                }

                is PaymentVerificationViewModel.VerificationState.Success -> {
                    LaunchedEffect(verificationState) {
                        paymentViewModel.resetVerificationState()
                        paymentViewModel.getPendingPayments() // Refresh the list
                        onVerifySuccess()
                    }
                }

                is PaymentVerificationViewModel.VerificationState.Error -> {
                    val errorMessage = (verificationState as PaymentVerificationViewModel.VerificationState.Error).message
                    AlertDialog(
                        onDismissRequest = { paymentViewModel.resetVerificationState() },
                        title = { Text("Verification Error") },
                        text = { Text(errorMessage) },
                        confirmButton = {
                            Button(onClick = { paymentViewModel.resetVerificationState() }) {
                                Text("OK")
                            }
                        }
                    )
                }

                else -> {
                    // Idle state, do nothing
                }
            }
        }
    }

    // Verification Dialog
    if (showVerificationDialog && selectedPayment != null) {
        PaymentVerificationDialog(
            payment = selectedPayment!!,
            onDismiss = { showVerificationDialog = false },
            onVerify = { status, amount, notes ->
                paymentViewModel.verifyPayment(
                    paymentId = selectedPayment!!.id,
                    status = status,
                    verifiedAmount = amount,
                    adminNotes = notes
                )
                showVerificationDialog = false
            }
        )
    }

    // Bank Slip Dialog
    if (showBankSlipDialog && selectedPayment != null && selectedPayment!!.bankSlipUrl != null) {
        Dialog(onDismissRequest = { showBankSlipDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bank Slip",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedPayment!!.bankSlipUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Bank Slip",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Button(
                        onClick = { showBankSlipDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingPaymentItem(
    payment: PaymentResponse,
    onVerifyClick: () -> Unit,
    onViewBankSlipClick: () -> Unit
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
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Status chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFA000), // Amber for pending
                    contentColor = Color.White
                ) {
                    Text(
                        text = "Pending",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date information
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
                        text = formatDate(payment.paymentDate),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = "Submission Date:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = formatDate(payment.submissionDate),
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

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (payment.bankSlipUrl != null) {
                    OutlinedButton(
                        onClick = onViewBankSlipClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "View Bank Slip",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("View Slip")
                    }
                }

                Button(
                    onClick = onVerifyClick
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Verify Payment",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verify")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentVerificationDialog(
    payment: PaymentResponse,
    onDismiss: () -> Unit,
    onVerify: (FinancePaymentStatus, Double, String?) -> Unit
) {
    val context = LocalContext.current

    // Form state
    var verifiedAmount by remember { mutableStateOf(payment.amount.toString()) }
    var adminNotes by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(FinancePaymentStatus.VERIFIED) }

    // Validation state
    var amountError by remember { mutableStateOf<String?>(null) }

    // Status options
    val statusOptions = listOf(
        FinancePaymentStatus.VERIFIED,
        FinancePaymentStatus.PARTIALLY_VERIFIED,
        FinancePaymentStatus.REJECTED
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Verify Payment",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Payment summary
                Text(
                    text = "Payment Summary",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reference:")
                    Text(payment.bankReference, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment Date:")
                    Text(formatDate(payment.paymentDate), fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Amount:")
                    Text(formatMoney(payment.amount), fontWeight = FontWeight.Medium)
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Verification form
                Text(
                    text = "Verification Details",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Verification status
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Status radio options
                statusOptions.forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(formatStatus(status))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verified amount - This is the part with KeyboardOptions issue
                OutlinedTextField(
                    value = verifiedAmount,
                    onValueChange = {
                        verifiedAmount = it
                        // Validate amount
                        amountError = if (it.toDoubleOrNull() == null || it.toDoubleOrNull()!! <= 0) {
                            "Please enter a valid amount"
                        } else {
                            null
                        }
                    },
                    label = { Text("Verified Amount") },
                    isError = amountError != null,
                    // Use just KeyboardOptions instead of the fully qualified name
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    supportingText = amountError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Admin notes
                OutlinedTextField(
                    value = adminNotes,
                    onValueChange = { adminNotes = it },
                    label = { Text("Admin Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
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
                            // Validate amount
                            val amount = verifiedAmount.toDoubleOrNull()
                            if (amount == null || amount <= 0) {
                                amountError = "Please enter a valid amount"
                                return@Button
                            }

                            onVerify(
                                selectedStatus,
                                amount,
                                if (adminNotes.isBlank()) null else adminNotes
                            )
                        }
                    ) {
                        Text("Verify")
                    }
                }
            }
        }
    }
}