//// VerifyPaymentScreen.kt
//package com.mihs.schoolsync.ui.screens.fee
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.ReceiptLong
//import androidx.compose.material.icons.filled.Verified
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.rememberImagePainter
//import com.mihs.schoolsync.models.Payment
//import com.mihs.schoolsync.models.PaymentStatus
//import com.mihs.schoolsync.models.PaymentVerifyRequest
//import com.mihs.schoolsync.ui.components.ErrorView
//import com.mihs.schoolsync.ui.components.LoadingView
//import com.mihs.schoolsync.ui.theme.GreenSuccess
//import com.mihs.schoolsync.ui.theme.RedError
//import com.mihs.schoolsync.ui.viewmodels.PaymentViewModel
//import com.mihs.schoolsync.utils.Result
//import com.mihs.schoolsync.utils.formatDate
//import com.mihs.schoolsync.utils.formatMoney
//import kotlinx.coroutines.launch
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun VerifyPaymentScreen(
//    paymentId: Int,
//    navigateBack: () -> Unit,
//    navigateToGenerateReceipt: (Int) -> Unit,
//    onPaymentVerified: () -> Unit,
//    viewModel: PaymentViewModel = hiltViewModel()
//) {
//    val coroutineScope = rememberCoroutineScope()
//    val payment = remember { mutableStateOf<Payment?>(null) }
//    val scrollState = rememberScrollState()
//
//    // Form state
//    var verifiedAmount by remember { mutableStateOf<String>("") }
//    var adminNotes by remember { mutableStateOf("") }
//    var selectedStatus by remember { mutableStateOf(PaymentStatus.VERIFIED) }
//
//    // Error state
//    var amountError by remember { mutableStateOf<String?>(null) }
//    var verificationDialogVisible by remember { mutableStateOf(false) }
//
//    // Fetch payment details
//    LaunchedEffect(paymentId) {
//        // This is simplified - in a real app, you'd have a dedicated endpoint to fetch a single payment
//        viewModel.getStudentPayments(0) // This would be replaced with a getPayment(paymentId) call
//
//        // For now, let's pretend we got the payment
//        // This is just for demonstration purposes
//        payment.value = Payment(
//            id = paymentId,
//            studentId = 1,
//            amount = 5000.0,
//            bankReference = "MPESA123456",
//            paymentDate = java.util.Date(),
//            submissionDate = java.util.Date(),
//            paymentType = com.mihs.schoolsync.models.PaymentType.BANK_TRANSFER,
//            status = PaymentStatus.PENDING,
//            student = com.mihs.schoolsync.models.Student(
//                id = 1,
//                firstName = "John",
//                lastName = "Doe",
//                admissionNumber = "ADM123",
//                currentClass = com.mihs.schoolsync.models.ClassSection(
//                    id = 1,
//                    name = "Form 1A",
//                    classLevelId = 1
//                )
//            )
//        )
//
//        // Initialize form with payment amount
//        verifiedAmount = payment.value?.amount.toString()
//    }
//
//    // Track payment verification state
//    val paymentState = viewModel.payment.collectAsState().value
//
//    // Handle successful verification
//    LaunchedEffect(paymentState) {
//        if (paymentState is Result.Success) {
//            onPaymentVerified()
//        }
//    }
//
//    fun validateAndShowConfirmation() {
//        // Validate amount
//        if (verifiedAmount.isEmpty() || verifiedAmount.toDoubleOrNull() == null || verifiedAmount.toDouble() <= 0) {
//            amountError = "Please enter a valid amount"
//            return
//        }
//
//        // If amount is less than claimed amount and status is VERIFIED, change to PARTIALLY_VERIFIED
//        val claimedAmount = payment.value?.amount ?: 0.0
//        val actualAmount = verifiedAmount.toDouble()
//
//        if (actualAmount < claimedAmount && selectedStatus == PaymentStatus.VERIFIED) {
//            selectedStatus = PaymentStatus.PARTIALLY_VERIFIED
//        }
//
//        // Show confirmation dialog
//        verificationDialogVisible = true
//    }
//
//    fun verifyPayment() {
//        coroutineScope.launch {
//            val verifyData = PaymentVerifyRequest(
//                status = selectedStatus,
//                verifiedAmount = verifiedAmount.toDouble(),
//                adminNotes = adminNotes.takeIf { it.isNotBlank() }
//            )
//            viewModel.verifyPayment(paymentId, verifyData)
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Verify Payment") },
//                navigationIcon = {
//                    IconButton(onClick = navigateBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    // Only show generate receipt button after verification
//                    if (paymentState is Result.Success) {
//                        IconButton(onClick = { navigateToGenerateReceipt(paymentId) }) {
//                            Icon(Icons.Default.ReceiptLong, contentDescription = "Generate Receipt")
//                        }
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            if (payment.value == null) {
//                LoadingView()
//            } else {
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp)
//                        .verticalScroll(scrollState)
//                ) {
//                    // Payment information card
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(bottom = 16.dp),
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        ) {
//                            // Student info
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Column {
//                                    Text(
//                                        text = payment.value?.student?.let { "${it.firstName} ${it.lastName}" }
//                                            ?: "Student #${payment.value?.studentId}",
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                    payment.value?.student?.admissionNumber?.let {
//                                        Text(
//                                            text = "Adm: $it",
//                                            color = Color.Gray,
//                                            style = MaterialTheme.typography.bodySmall
//                                        )
//                                    }
//                                    payment.value?.student?.currentClass?.let {
//                                        Text(
//                                            text = "Class: ${it.name}",
//                                            color = Color.Gray,
//                                            style = MaterialTheme.typography.bodySmall
//                                        )
//                                    }
//                                }
//                            }
//
//                            Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//                            // Payment details
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Column {
//                                    Text(
//                                        text = "Claimed Amount:",
//                                        color = Color.Gray,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                    Text(
//                                        text = "Reference:",
//                                        color = Color.Gray,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                    Text(
//                                        text = "Payment Date:",
//                                        color = Color.Gray,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                    Text(
//                                        text = "Submission Date:",
//                                        color = Color.Gray,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                    Text(
//                                        text = "Payment Type:",
//                                        color = Color.Gray,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                }
//
//                                Column(horizontalAlignment = Alignment.End) {
//                                    Text(
//                                        text = formatMoney(payment.value?.amount ?: 0.0),
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                    Text(text = payment.value?.bankReference ?: "")
//                                    Text(text = formatDate(payment.value?.paymentDate))
//                                    Text(text = formatDate(payment.value?.submissionDate))
//                                    Text(text = payment.value?.paymentType?.name?.replace("_", " ") ?: "")
//                                }
//                            }
//
//                            // Parent Notes (if any)
//                            payment.value?.parentNotes?.let { notes ->
//                                if (notes.isNotBlank()) {
//                                    Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//                                    Column {
//                                        Text(
//                                            text = "Parent Notes:",
//                                            color = Color.Gray,
//                                            style = MaterialTheme.typography.bodySmall
//                                        )
//                                        Text(text = notes)
//                                    }
//                                }
//                            }
//
//                            // Bank slip (if any)
//                            payment.value?.bankSlipUrl?.let { url ->
//                                Divider(modifier = Modifier.padding(vertical = 8.dp))
//
//                                Text(
//                                    text = "Bank Slip:",
//                                    color = Color.Gray,
//                                    style = MaterialTheme.typography.bodySmall
//                                )
//
//                                // In a real app, load the image from the URL
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(200.dp)
//                                        .padding(top = 8.dp)
//                                ) {
//                                    Text(
//                                        text = "Bank slip image would be displayed here",
//                                        modifier = Modifier.align(Alignment.Center)
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    // Verification form
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        ) {
//                            Text(
//                                text = "Verification Details",
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier.padding(bottom = 16.dp)
//                            )
//
//                            // Verified amount
//                            Column {
//                                OutlinedTextField(
//                                    value = verifiedAmount,
//                                    onValueChange = {
//                                        verifiedAmount = it
//                                        amountError = null
//                                    },
//                                    label = { Text("Verified Amount") },
//                                    modifier = Modifier.fillMaxWidth(),
//                                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
//                                        keyboardType = KeyboardType.Number
//                                    ),
//                                    isError = amountError != null,
//                                    singleLine = true
//                                )
//                                if (amountError != null) {
//                                    Text(
//                                        text = amountError!!,
//                                        color = MaterialTheme.colorScheme.error,
//                                        style = MaterialTheme.typography.bodySmall,
//                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                                    )
//                                }
//                            }
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            // Verification status
//                            Column {
//                                Text(
//                                    text = "Verification Status",
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    modifier = Modifier.padding(bottom = 8.dp)
//                                )
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
//                                ) {
//                                    // Verified option
//                                    FilterChip(
//                                        selected = selectedStatus == PaymentStatus.VERIFIED,
//                                        onClick = { selectedStatus = PaymentStatus.VERIFIED },
//                                        label = { Text("Verified") },
//                                        leadingIcon = {
//                                            if (selectedStatus == PaymentStatus.VERIFIED) {
//                                                Icon(
//                                                    Icons.Default.Verified,
//                                                    contentDescription = null,
//                                                    tint = GreenSuccess
//                                                )
//                                            }
//                                        }
//                                    )
//
//                                    // Partially Verified option
//                                    FilterChip(
//                                        selected = selectedStatus == PaymentStatus.PARTIALLY_VERIFIED,
//                                        onClick = { selectedStatus = PaymentStatus.PARTIALLY_VERIFIED },
//                                        label = { Text("Partial") }
//                                    )
//
//                                    // Rejected option
//                                    FilterChip(
//                                        selected = selectedStatus == PaymentStatus.REJECTED,
//                                        onClick = { selectedStatus = PaymentStatus.REJECTED },
//                                        label = { Text("Rejected") },
//                                        leadingIcon = {
//                                            if (selectedStatus == PaymentStatus.REJECTED) {
//                                                Icon(
//                                                    Icons.Default.Close,
//                                                    contentDescription = null,
//                                                    tint = RedError
//                                                )
//                                            }
//                                        }
//                                    )
//                                }
//                            }
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            // Admin notes
//                            OutlinedTextField(
//                                value = adminNotes,
//                                onValueChange = { adminNotes = it },
//                                label = { Text("Admin Notes (Optional)") },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(120.dp),
//                                maxLines = 5
//                            )
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            // Verify button
//                            Button(
//                                onClick = { validateAndShowConfirmation() },
//                                modifier = Modifier.fillMaxWidth(),
//                                enabled = paymentState !is Result.Loading
//                            ) {
//                                Icon(
//                                    Icons.Default.Verified,
//                                    contentDescription = "Verify",
//                                    modifier = Modifier.padding(end = 8.dp)
//                                )
//                                Text("Verify Payment")
//                            }
//                        }
//                    }
//                }
//
//                // Confirmation dialog
//                if (verificationDialogVisible) {
//                    AlertDialog(
//                        onDismissRequest = { verificationDialogVisible = false },
//                        title = { Text("Confirm Verification") },
//                        text = {
//                            Text("Are you sure you want to mark this payment as ${selectedStatus.name.replace("_", " ")}?")
//                        },
//                        confirmButton = {
//                            Button(
//                                onClick = {
//                                    verificationDialogVisible = false
//                                    verifyPayment()
//                                }
//                            ) {
//                                Text("Confirm")
//                            }
//                        },
//                        dismissButton = {
//                            TextButton(
//                                onClick = { verificationDialogVisible = false }
//                            ) {
//                                Text("Cancel")
//                            }
//                        }
//                    )
//                }
//
//                // Show loading or error state
//                when (paymentState) {
//                    is Result.Loading -> {
//                        Box(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(paddingValues),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CircularProgressIndicator()
//                        }
//                    }
//                    is Result.Error -> {
//                        AlertDialog(
//                            onDismissRequest = { /* Cannot dismiss error */ },
//                            title = { Text("Error") },
//                            text = { Text(paymentState.message) },
//                            confirmButton = {
//                                Button(
//                                    onClick = { viewModel.resetPaymentState() }
//                                ) {
//                                    Text("OK")
//                                }
//                            }
//                        )
//                    }
//                    is Result.Success -> {
//                        AlertDialog(
//                            onDismissRequest = { onPaymentVerified() },
//                            title = { Text("Payment Verified") },
//                            text = {
//                                Column {
//                                    Text("Payment has been successfully verified.")
//                                    if (selectedStatus == PaymentStatus.VERIFIED) {
//                                        Text("Would you like to generate a receipt?")
//                                    }
//                                }
//                            },
//                            confirmButton = {
//                                if (selectedStatus == PaymentStatus.VERIFIED) {
//                                    Button(
//                                        onClick = { navigateToGenerateReceipt(paymentId) }
//                                    ) {
//                                        Text("Generate Receipt")
//                                    }
//                                } else {
//                                    Button(onClick = onPaymentVerified) {
//                                        Text("Done")
//                                    }
//                                }
//                            },
//                            dismissButton = {
//                                if (selectedStatus == PaymentStatus.VERIFIED) {
//                                    TextButton(onClick = onPaymentVerified) {
//                                        Text("Skip")
//                                    }
//                                }
//                            }
//                        )
//                    }
//                    else -> { /* Initial state, do nothing */ }
//                }
//            }
//        }
//    }
//}