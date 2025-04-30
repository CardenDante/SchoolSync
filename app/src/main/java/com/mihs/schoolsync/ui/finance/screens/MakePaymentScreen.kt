// MakePaymentScreen.kt
package com.mihs.schoolsync.ui.finance.screens

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.ui.finance.models.FinancePaymentType
import com.mihs.schoolsync.ui.finance.viewmodel.FeeViewModel
import com.mihs.schoolsync.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakePaymentScreen(
    studentId: Int,
    navigateBack: () -> Unit,
    onSuccess: () -> Unit,
    feeViewModel: FeeViewModel = hiltViewModel(),
    studentViewModel: StudentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // State to track student and payment submission
    val studentState by studentViewModel.studentDetailState.collectAsState()
    val paymentSubmissionState by feeViewModel.paymentSubmissionState.collectAsState()

    // Form state
    var amount by remember { mutableStateOf("") }
    var bankReference by remember { mutableStateOf("") }
    var paymentDate by remember { mutableStateOf(Calendar.getInstance().time) }
    var paymentType by remember { mutableStateOf(FinancePaymentType.BANK_TRANSFER) }
    var notes by remember { mutableStateOf("") }

    // File selection state
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("No file selected") }

    // Validation state
    var amountError by remember { mutableStateOf<String?>(null) }
    var referenceError by remember { mutableStateOf<String?>(null) }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }

    // Payment method dropdown
    var expandedPaymentTypeDropdown by remember { mutableStateOf(false) }

    // File picker
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedFileUri = it
            // Get file name from URI
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        selectedFileName = cursor.getString(nameIndex)
                    }
                }
            }
        }
    }

    // Load student data
    LaunchedEffect(studentId) {
        studentViewModel.getStudent(studentId)
    }

    // Handle payment submission state changes
    LaunchedEffect(paymentSubmissionState) {
        when (paymentSubmissionState) {
            is FeeViewModel.PaymentSubmissionState.Success -> {
                // Clear the state and navigate back
                feeViewModel.resetPaymentSubmissionState()
                onSuccess()
            }
            else -> {}
        }
    }

    // Submit payment function
    fun submitPayment() {
        // Validate form
        var isValid = true

        if (amount.isEmpty() || amount.toDoubleOrNull() == null || amount.toDoubleOrNull()!! <= 0) {
            amountError = "Please enter a valid amount"
            isValid = false
        } else {
            amountError = null
        }

        if (bankReference.isEmpty()) {
            referenceError = "Please enter a reference number"
            isValid = false
        } else {
            referenceError = null
        }

        if (!isValid) return

        // Convert the selected URI to a File if it exists
        var bankSlipFile: File? = null
        selectedFileUri?.let { uri ->
            try {
                // Create a temporary file
                val tempFile = File(context.cacheDir, selectedFileName)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                bankSlipFile = tempFile
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }

        // Submit payment
        coroutineScope.launch {
            feeViewModel.submitPayment(
                studentId = studentId,
                amount = amount.toDouble(),
                bankReference = bankReference,
                paymentDate = paymentDate,
                paymentType = paymentType,
                parentNotes = if (notes.isNotBlank()) notes else null,
                bankSlipFile = bankSlipFile
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Make Payment") },
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
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Student info card
                when (studentState) {
                    is StudentViewModel.StudentDetailState.Success -> {
                        val student = (studentState as StudentViewModel.StudentDetailState.Success).student
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Payment for",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = student.studentId,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                student.currentClass?.let {
                                    Text(
                                        text = "Class: $it",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                student.feeBalance?.let {
                                    Text(
                                        text = "Current Balance: ${formatMoney(it)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (it > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    is StudentViewModel.StudentDetailState.Loading -> {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        )
                    }
                    is StudentViewModel.StudentDetailState.Error -> {
                        Text(
                            text = "Error loading student: ${(studentState as StudentViewModel.StudentDetailState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    else -> {
                        // Idle state, show nothing
                    }
                }

                // Payment form
                Text(
                    text = "Payment Details",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it; amountError = null },
                    label = { Text("Amount") },
                    placeholder = { Text("Enter amount") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError != null,
                    singleLine = true,
                    supportingText = amountError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bank Reference
                OutlinedTextField(
                    value = bankReference,
                    onValueChange = { bankReference = it; referenceError = null },
                    label = { Text("Reference Number") },
                    placeholder = { Text("Enter bank reference") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = referenceError != null,
                    singleLine = true,
                    supportingText = referenceError?.let { { Text(it) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Date
                Column {
                    Text(
                        text = "Payment Date",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(formatDate(paymentDate))
                            Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                        }
                    }
                }

                // Payment Type Dropdown
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedPaymentTypeDropdown,
                    onExpandedChange = { expandedPaymentTypeDropdown = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = paymentType.name.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPaymentTypeDropdown)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expandedPaymentTypeDropdown,
                        onDismissRequest = { expandedPaymentTypeDropdown = false }
                    ) {
                        FinancePaymentType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replace("_", " ")) },
                                onClick = {
                                    paymentType = type
                                    expandedPaymentTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                // Bank Slip Upload
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Text(
                        text = "Upload Bank Slip (Optional)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { filePicker.launch("image/*") }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedFileName,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(Icons.Default.AttachFile, contentDescription = "Attach File")
                        }
                    }
                }

                // Notes
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Enter additional notes") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    minLines = 3
                )

                // Submit Button
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { submitPayment() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = paymentSubmissionState !is FeeViewModel.PaymentSubmissionState.Loading
                ) {
                    if (paymentSubmissionState is FeeViewModel.PaymentSubmissionState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Submit Payment")
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Loading indicator
            if (paymentSubmissionState is FeeViewModel.PaymentSubmissionState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Error message
            if (paymentSubmissionState is FeeViewModel.PaymentSubmissionState.Error) {
                AlertDialog(
                    onDismissRequest = { feeViewModel.resetPaymentSubmissionState() },
                    title = { Text("Error") },
                    text = { Text((paymentSubmissionState as FeeViewModel.PaymentSubmissionState.Error).message) },
                    confirmButton = {
                        Button(onClick = { feeViewModel.resetPaymentSubmissionState() }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }

    // Date picker dialog (simplified)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            // Simple calendar picker
            // For a real app, you would use the Material DatePicker component
            // or another date picker library
            // This is a placeholder for demonstration purposes
            Text("Date picker would be shown here")
        }
    }
}