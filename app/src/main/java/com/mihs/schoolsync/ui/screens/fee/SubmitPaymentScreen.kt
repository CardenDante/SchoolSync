//// SubmitPaymentScreen.kt
//package com.mihs.schoolsync.ui.screens.fee
//
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.AttachFile
//import androidx.compose.material.icons.filled.CalendarMonth
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.maxkeppeker.sheets.core.models.base.rememberSheetState
//import com.maxkeppeler.sheets.calendar.CalendarDialog
//import com.maxkeppeler.sheets.calendar.models.CalendarConfig
//import com.maxkeppeler.sheets.calendar.models.CalendarSelection
//import com.mihs.schoolsync.models.PaymentCreateRequest
//import com.mihs.schoolsync.models.PaymentType
//import com.mihs.schoolsync.ui.components.DropdownSelector
//import com.mihs.schoolsync.ui.components.ErrorView
//import com.mihs.schoolsync.ui.components.LoadingView
//import com.mihs.schoolsync.ui.viewmodels.PaymentViewModel
//import com.mihs.schoolsync.ui.viewmodels.StudentViewModel
//import com.mihs.schoolsync.utils.Result
//import kotlinx.coroutines.launch
//import java.io.ByteArrayOutputStream
//import java.io.InputStream
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SubmitPaymentScreen(
//    studentId: Int,
//    navigateBack: () -> Unit,
//    onPaymentSubmitted: () -> Unit,
//    paymentViewModel: PaymentViewModel = hiltViewModel(),
//    studentViewModel: StudentViewModel = hiltViewModel()
//) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//
//    // Load student data
//    LaunchedEffect(studentId) {
//        studentViewModel.getStudent(studentId)
//    }
//
//    val studentState = studentViewModel.student.collectAsState().value
//    val paymentState = paymentViewModel.payment.collectAsState().value
//
//    // Form state
//    var amount by remember { mutableStateOf("") }
//    var bankReference by remember { mutableStateOf("") }
//    var paymentType by remember { mutableStateOf(PaymentType.BANK_TRANSFER) }
//    var paymentDate by remember { mutableStateOf(Date()) }
//    var notes by remember { mutableStateOf("") }
//    var bankSlipUri by remember { mutableStateOf<Uri?>(null) }
//    var bankSlipBytes by remember { mutableStateOf<ByteArray?>(null) }
//
//    // Validation
//    var amountError by remember { mutableStateOf<String?>(null) }
//    var referenceError by remember { mutableStateOf<String?>(null) }
//
//    // Calendar dialog
//    val calendarState = rememberSheetState()
//
//    // File picker
//    val filePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        if (uri != null) {
//            bankSlipUri = uri
//            // Convert uri to ByteArray
//            context.contentResolver.openInputStream(uri)?.use { inputStream ->
//                bankSlipBytes = inputStream.readBytes()
//            }
//        }
//    }
//
//    // Submit payment function
//    fun submitPayment() {
//        // Validate
//        var isValid = true
//
//        if (amount.isBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
//            amountError = "Please enter a valid amount"
//            isValid = false
//        } else {
//            amountError = null
//        }
//
//        if (bankReference.isBlank()) {
//            referenceError = "Please enter a reference number"
//            isValid = false
//        } else {
//            referenceError = null
//        }
//
//        if (isValid) {
//            val paymentRequest = PaymentCreateRequest(
//                studentId = studentId,
//                amount = amount.toDouble(),
//                bankReference = bankReference,
//                paymentDate = paymentDate,
//                paymentType = paymentType,
//                parentNotes = notes.takeIf { it.isNotBlank() }
//            )
//
//            coroutineScope.launch {
//                paymentViewModel.submitPayment(paymentRequest, bankSlipBytes)
//            }
//        }
//    }
//
//    // Handle successful payment submission
//    LaunchedEffect(paymentState) {
//        if (paymentState is Result.Success) {
//            onPaymentSubmitted()
//        }
//    }
//
//    CalendarDialog(
//        state = calendarState,
//        config = CalendarConfig(
//            monthSelection = true,
//            yearSelection = true
//        ),
//        selection = CalendarSelection.Date { date ->
//            paymentDate = Date(date.timeInMillis)
//        }
//    )
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Submit Payment") },
//                navigationIcon = {
//                    IconButton(onClick = navigateBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
//            when (val student = studentState) {
//                is Result.Loading -> {
//                    LoadingView()
//                }
//                is Result.Success -> {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        // Student info card
//                        Card(
//                            modifier = Modifier.fillMaxWidth(),
//                            shape = RoundedCornerShape(12.dp)
//                        ) {
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(16.dp)
//                            ) {
//                                Text(
//                                    text = "Payment for",
//                                    color = Color.Gray
//                                )
//                                Text(
//                                    text = "${student.data.firstName} ${student.data.lastName}",
//                                    style = MaterialTheme.typography.titleLarge
//                                )
//                                if (student.data.admissionNumber != null) {
//                                    Text(
//                                        text = "Admission: ${student.data.admissionNumber}",
//                                        color = Color.Gray
//                                    )
//                                }
//                                if (student.data.currentClass != null) {
//                                    Text(
//                                        text = "Class: ${student.data.currentClass?.name ?: "N/A"}",
//                                        color = Color.Gray
//                                    )
//                                }
//                            }
//                        }
//
//                        // Payment form
//                        Column(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalArrangement = Arrangement.spacedBy(16.dp)
//                        ) {
//                            // Amount field
//                            Column {
//                                OutlinedTextField(
//                                    value = amount,
//                                    onValueChange = { amount = it },
//                                    label = { Text("Amount") },
//                                    modifier = Modifier.fillMaxWidth(),
//                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
//                            // Reference number field
//                            Column {
//                                OutlinedTextField(
//                                    value = bankReference,
//                                    onValueChange = { bankReference = it },
//                                    label = { Text("Reference Number") },
//                                    modifier = Modifier.fillMaxWidth(),
//                                    isError = referenceError != null,
//                                    singleLine = true
//                                )
//                                if (referenceError != null) {
//                                    Text(
//                                        text = referenceError!!,
//                                        color = MaterialTheme.colorScheme.error,
//                                        style = MaterialTheme.typography.bodySmall,
//                                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
//                                    )
//                                }
//                            }
//
//                            // Payment Type dropdown
//                            DropdownSelector(
//                                label = "Payment Type",
//                                options = PaymentType.values().toList(),
//                                selectedOption = paymentType,
//                                onOptionSelected = { paymentType = it },
//                                optionLabel = { it.name.replace("_", " ") }
//                            )
//
//                            // Payment date picker
//                            Column(modifier = Modifier.fillMaxWidth()) {
//                                Text(
//                                    text = "Payment Date",
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    modifier = Modifier.padding(bottom = 8.dp)
//                                )
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(56.dp)
//                                        .border(
//                                            BorderStroke(1.dp, Color.Gray),
//                                            shape = RoundedCornerShape(4.dp)
//                                        )
//                                        .clip(RoundedCornerShape(4.dp))
//                                        .clickable { calendarState.show() }
//                                        .padding(horizontal = 16.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(formatDate(paymentDate))
//                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Select Date")
//                                }
//                            }
//
//                            // Bank slip upload
//                            Column(modifier = Modifier.fillMaxWidth()) {
//                                Text(
//                                    text = "Bank Slip (Optional)",
//                                    style = MaterialTheme.typography.bodyMedium,
//                                    modifier = Modifier.padding(bottom = 8.dp)
//                                )
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .height(56.dp)
//                                        .border(
//                                            BorderStroke(1.dp, Color.Gray),
//                                            shape = RoundedCornerShape(4.dp)
//                                        )
//                                        .clip(RoundedCornerShape(4.dp))
//                                        .clickable { filePickerLauncher.launch("image/*") }
//                                        .padding(horizontal = 16.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(
//                                        text = bankSlipUri?.lastPathSegment ?: "No file selected",
//                                        maxLines = 1
//                                    )
//                                    Icon(Icons.Default.AttachFile, contentDescription = "Attach File")
//                                }
//                            }
//
//                            // Notes field
//                            OutlinedTextField(
//                                value = notes,
//                                onValueChange = { notes = it },
//                                label = { Text("Notes (Optional)") },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .height(120.dp),
//                                maxLines = 5
//                            )
//
//                            // Submit button
//                            Button(
//                                onClick = { submitPayment() },
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 16.dp),
//                                enabled = paymentState !is Result.Loading
//                            ) {
//                                Text("Submit Payment")
//                            }
//                        }
//                    }
//
//                    // Show loading or error state for payment submission
//                    when (paymentState) {
//                        is Result.Loading -> {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(Color.Black.copy(alpha = 0.5f)),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                CircularProgressIndicator()
//                            }
//                        }
//                        is Result.Error -> {
//                            AlertDialog(
//                                onDismissRequest = { },
//                                title = { Text("Error") },
//                                text = { Text(paymentState.message) },
//                                confirmButton = {
//                                    Button(
//                                        onClick = { paymentViewModel.resetPaymentState() }
//                                    ) {
//                                        Text("OK")
//                                    }
//                                }
//                            )
//                        }
//                        else -> { /* Initial or Success state, handled elsewhere */ }
//                    }
//                }
//                is Result.Error -> {
//                    ErrorView(
//                        message = student.message,
//                        onRetry = { studentViewModel.getStudent(studentId) }
//                    )
//                }
//                else -> { /* Initial state, do nothing */ }
//            }
//        }
//    }
//}