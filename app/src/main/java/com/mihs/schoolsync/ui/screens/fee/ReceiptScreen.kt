//// ReceiptScreen.kt
//package com.mihs.schoolsync.ui.screens.fee
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Download
//import androidx.compose.material.icons.filled.Share
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.mihs.schoolsync.R
//import com.mihs.schoolsync.models.Receipt
//import com.mihs.schoolsync.ui.components.ErrorView
//import com.mihs.schoolsync.ui.components.LoadingView
//import com.mihs.schoolsync.ui.viewmodels.PaymentViewModel
//import com.mihs.schoolsync.utils.Result
//import com.mihs.schoolsync.utils.formatDate
//import com.mihs.schoolsync.utils.formatMoney
//import kotlinx.coroutines.launch
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.*
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ReceiptScreen(
//    paymentId: Int,
//    navigateBack: () -> Unit,
//    viewModel: PaymentViewModel = hiltViewModel()
//) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    val scrollState = rememberScrollState()
//
//    // Generate receipt if not already generated
//    LaunchedEffect(paymentId) {
//        viewModel.generateReceipt(paymentId)
//    }
//
//    val receiptState = viewModel.receipt.collectAsState().value
//
//    // Sample receipt data (for demo)
//    val receipt = remember { mutableStateOf<Receipt?>(null) }
//
//    // Update receipt when state changes
//    LaunchedEffect(receiptState) {
//        if (receiptState is Result.Success) {
//            receipt.value = receiptState.data
//        }
//    }
//
//    // Functions to handle PDF actions (download, share)
//    fun downloadPdf() {
//        // In a real app, this would download the PDF from the URL
//        // and save it to the device's Downloads folder
//        // For demo purposes, we'll just show a toast
//        android.widget.Toast.makeText(
//            context,
//            "Receipt downloaded",
//            android.widget.Toast.LENGTH_SHORT
//        ).show()
//    }
//
//    fun sharePdf() {
//        // In a real app, this would share the PDF via intent
//        // For demo purposes, we'll just create a dummy share intent
//        val intent = Intent(Intent.ACTION_SEND).apply {
//            type = "application/pdf"
//            putExtra(Intent.EXTRA_SUBJECT, "Payment Receipt")
//            putExtra(Intent.EXTRA_TEXT, "Please find attached payment receipt.")
//            // In real app: putExtra(Intent.EXTRA_STREAM, uri)
//        }
//        context.startActivity(Intent.createChooser(intent, "Share Receipt"))
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Receipt") },
//                navigationIcon = {
//                    IconButton(onClick = navigateBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = { downloadPdf() }) {
//                        Icon(Icons.Default.Download, contentDescription = "Download")
//                    }
//                    IconButton(onClick = { sharePdf() }) {
//                        Icon(Icons.Default.Share, contentDescription = "Share")
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
//            when (receiptState) {
//                is Result.Loading -> {
//                    LoadingView()
//                }
//                is Result.Success -> {
//                    // Receipt PDF viewer
//                    Card(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .padding(16.dp),
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(16.dp)
//                                .verticalScroll(scrollState),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            // School Logo/Name
//                            Text(
//                                text = "SCHOOL SYNC ACADEMY",
//                                style = MaterialTheme.typography.titleLarge,
//                                fontWeight = FontWeight.Bold,
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier.padding(bottom = 4.dp)
//                            )
//
//                            Text(
//                                text = "P.O. Box 123, City",
//                                style = MaterialTheme.typography.bodyMedium,
//                                textAlign = TextAlign.Center
//                            )
//
//                            Text(
//                                text = "Tel: 123-456-7890",
//                                style = MaterialTheme.typography.bodyMedium,
//                                textAlign = TextAlign.Center,
//                                modifier = Modifier.padding(bottom = 16.dp)
//                            )
//
//                            // Receipt title
//                            Surface(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 8.dp),
//                                color = MaterialTheme.colorScheme.primaryContainer
//                            ) {
//                                Text(
//                                    text = "OFFICIAL RECEIPT",
//                                    style = MaterialTheme.typography.titleMedium,
//                                    fontWeight = FontWeight.Bold,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.padding(vertical = 8.dp)
//                                )
//                            }
//
//                            // Receipt details
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 16.dp)
//                            ) {
//                                ReceiptField(
//                                    label = "Receipt No:",
//                                    value = receiptState.data.receiptNumber
//                                )
//
//                                ReceiptField(
//                                    label = "Date:",
//                                    value = formatDate(receiptState.data.generatedAt)
//                                )
//
//                                ReceiptField(
//                                    label = "Student Name:",
//                                    value = receiptState.data.student?.let {
//                                        "${it.firstName} ${it.lastName}"
//                                    } ?: "Student #${receiptState.data.studentId}"
//                                )
//
//                                receiptState.data.student?.admissionNumber?.let {
//                                    ReceiptField(label = "Adm No:", value = it)
//                                }
//
//                                receiptState.data.student?.currentClass?.let {
//                                    ReceiptField(label = "Class:", value = it.name)
//                                }
//
//                                Spacer(modifier = Modifier.height(16.dp))
//
//                                // Payment details in a table-like format
//                                Column(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .border(
//                                            width = 1.dp,
//                                            color = Color.Gray,
//                                            shape = RoundedCornerShape(4.dp)
//                                        )
//                                ) {
//                                    // Table header
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .background(Color.LightGray)
//                                            .padding(8.dp)
//                                    ) {
//                                        Text(
//                                            text = "Description",
//                                            fontWeight = FontWeight.Bold,
//                                            modifier = Modifier.weight(2f)
//                                        )
//                                        Text(
//                                            text = "Amount",
//                                            fontWeight = FontWeight.Bold,
//                                            textAlign = TextAlign.End,
//                                            modifier = Modifier.weight(1f)
//                                        )
//                                    }
//
//                                    // Payment row
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(8.dp)
//                                    ) {
//                                        Text(
//                                            text = "Fee Payment",
//                                            modifier = Modifier.weight(2f)
//                                        )
//                                        Text(
//                                            text = formatMoney(receiptState.data.amount),
//                                            textAlign = TextAlign.End,
//                                            modifier = Modifier.weight(1f)
//                                        )
//                                    }
//
//                                    // Divider
//                                    Divider(color = Color.Gray)
//
//                                    // Total row
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(8.dp)
//                                    ) {
//                                        Text(
//                                            text = "Total Paid",
//                                            fontWeight = FontWeight.Bold,
//                                            modifier = Modifier.weight(2f)
//                                        )
//                                        Text(
//                                            text = formatMoney(receiptState.data.amount),
//                                            fontWeight = FontWeight.Bold,
//                                            textAlign = TextAlign.End,
//                                            modifier = Modifier.weight(1f)
//                                        )
//                                    }
//
//                                    // Balance row
//                                    Row(
//                                        modifier = Modifier
//                                            .fillMaxWidth()
//                                            .padding(8.dp)
//                                    ) {
//                                        Text(
//                                            text = "Outstanding Balance",
//                                            fontWeight = FontWeight.Bold,
//                                            modifier = Modifier.weight(2f)
//                                        )
//                                        Text(
//                                            text = formatMoney(receiptState.data.balance),
//                                            fontWeight = FontWeight.Bold,
//                                            textAlign = TextAlign.End,
//                                            modifier = Modifier.weight(1f)
//                                        )
//                                    }
//                                }
//
//                                Spacer(modifier = Modifier.height(32.dp))
//
//                                // Signature
//                                Column(
//                                    modifier = Modifier.fillMaxWidth()
//                                ) {
//                                    Divider(
//                                        modifier = Modifier.width(200.dp),
//                                        color = Color.Gray
//                                    )
//                                    Text(
//                                        text = "Authorized Signature",
//                                        style = MaterialTheme.typography.bodySmall,
//                                        modifier = Modifier.padding(top = 4.dp)
//                                    )
//                                }
//
//                                Spacer(modifier = Modifier.height(32.dp))
//
//                                // Footer
//                                Text(
//                                    text = "This is a computer-generated receipt and does not require a physical signature.",
//                                    style = MaterialTheme.typography.bodySmall,
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.fillMaxWidth()
//                                )
//                            }
//                        }
//                    }
//                }
//                is Result.Error -> {
//                    ErrorView(
//                        message = receiptState.message,
//                        onRetry = { viewModel.generateReceipt(paymentId) }
//                    )
//                }
//                else -> { /* Initial state, do nothing */ }
//            }
//        }
//    }
//}
//
//@Composable
//fun ReceiptField(label: String, value: String) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        horizontalArrangement = Arrangement.Start
//    ) {
//        Text(
//            text = label,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.width(120.dp)
//        )
//        Text(text = value)
//    }
//}