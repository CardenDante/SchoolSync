// ReceiptScreen.kt
package com.mihs.schoolsync.ui.finance.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.mihs.schoolsync.R
import com.mihs.schoolsync.ui.finance.models.ReceiptResponse
import com.mihs.schoolsync.ui.finance.viewmodel.ReceiptViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptScreen(
    paymentId: Int,
    navigateBack: () -> Unit,
    receiptViewModel: ReceiptViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Observe receipt state
    val receiptState by receiptViewModel.receiptState.collectAsState()
    val downloadState by receiptViewModel.downloadState.collectAsState()

    // Load receipt for payment
    LaunchedEffect(paymentId) {
        receiptViewModel.getReceipt(paymentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receipt") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (receiptState is ReceiptViewModel.ReceiptState.Success) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val receipt = (receiptState as ReceiptViewModel.ReceiptState.Success).receipt
                                    receiptViewModel.downloadReceipt(receipt)
                                }
                            },
                            enabled = downloadState !is ReceiptViewModel.DownloadState.Loading
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Download Receipt"
                            )
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    val receipt = (receiptState as ReceiptViewModel.ReceiptState.Success).receipt
                                    val downloadResult = receiptViewModel.downloadReceipt(receipt)

                                    // If download successful, share the file
                                    if (downloadResult is ReceiptViewModel.DownloadState.Success) {
                                        shareReceipt(context, downloadResult.file)
                                    }
                                }
                            },
                            enabled = downloadState !is ReceiptViewModel.DownloadState.Loading
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share Receipt"
                            )
                        }
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
            when (receiptState) {
                is ReceiptViewModel.ReceiptState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ReceiptViewModel.ReceiptState.Error -> {
                    val errorMessage = (receiptState as ReceiptViewModel.ReceiptState.Error).message
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
                            onClick = { receiptViewModel.getReceipt(paymentId) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Retry")
                        }
                    }
                }

                is ReceiptViewModel.ReceiptState.Success -> {
                    val receipt = (receiptState as ReceiptViewModel.ReceiptState.Success).receipt

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Receipt Content
                        ReceiptContent(receipt)

                        Spacer(modifier = Modifier.height(24.dp))

                        // Download Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    receiptViewModel.downloadReceipt(receipt)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = downloadState !is ReceiptViewModel.DownloadState.Loading
                        ) {
                            Icon(
                                Icons.Default.Download,
                                contentDescription = "Download Receipt",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download Receipt")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Share Button
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    val downloadResult = receiptViewModel.downloadReceipt(receipt)

                                    // If download successful, share the file
                                    if (downloadResult is ReceiptViewModel.DownloadState.Success) {
                                        shareReceipt(context, downloadResult.file)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = downloadState !is ReceiptViewModel.DownloadState.Loading
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share Receipt",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Share Receipt")
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Show download progress or error
                    when (downloadState) {
                        is ReceiptViewModel.DownloadState.Loading -> {
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
                                        Text("Downloading receipt...")
                                    }
                                }
                            }
                        }

                        is ReceiptViewModel.DownloadState.Error -> {
                            val errorMessage = (downloadState as ReceiptViewModel.DownloadState.Error).message
                            SnackbarHost(
                                hostState = remember { SnackbarHostState() }.apply {
                                    LaunchedEffect(downloadState) {
                                        showSnackbar(errorMessage)
                                    }
                                },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }

                        is ReceiptViewModel.DownloadState.Success -> {
                            SnackbarHost(
                                hostState = remember { SnackbarHostState() }.apply {
                                    LaunchedEffect(downloadState) {
                                        showSnackbar("Receipt downloaded successfully")
                                    }
                                },
                                modifier = Modifier.align(Alignment.BottomCenter)
                            )
                        }

                        else -> { /* Idle state, do nothing */ }
                    }
                }

                else -> {
                    // Idle state, do nothing
                }
            }
        }
    }
}

@Composable
fun ReceiptContent(receipt: ReceiptResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo or School Name
            // Placeholder for school logo
            Text(
                text = receipt.schoolInfo.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = receipt.schoolInfo.address,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Tel: ${receipt.schoolInfo.phone} | Email: ${receipt.schoolInfo.email}",
                style = MaterialTheme.typography.bodySmall
            )

            Divider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            )

            // RECEIPT headline
            Text(
                text = "OFFICIAL RECEIPT",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Receipt No: ${receipt.receiptNumber}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Student details
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Student Name:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = receipt.student.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Admission No:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = receipt.student.adminNumber,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment details
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Payment Date:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = formatReceiptDate(receipt.payment.date),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Payment Method:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = receipt.payment.method,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reference
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Reference:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.width(100.dp)
                )
                Text(
                    text = receipt.payment.reference,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Amount Received",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = formatMoney(receipt.payment.amount),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = receipt.payment.amountInWords,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Outstanding balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Outstanding Balance:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatMoney(receipt.balance),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (receipt.balance > 0) MaterialTheme.colorScheme.error else Color.Green
                )
            }

            Divider(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
            )

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Verified By:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = receipt.verifiedBy,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date Issued:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = formatReceiptDate(receipt.date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Thank you for your payment",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "This is a computer-generated receipt and does not require a signature",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// Helper function to format date on receipts
fun formatReceiptDate(date: Date): String {
    return SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
}

// Function to share receipt PDF
fun shareReceipt(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share Receipt"))
}