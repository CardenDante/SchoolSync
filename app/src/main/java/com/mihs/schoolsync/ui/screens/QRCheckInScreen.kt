// QRCheckInScreen.kt
package com.mihs.schoolsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.hilt.navigation.compose.hiltViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.mihs.schoolsync.ui.viewmodel.AttendanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCheckInScreen(
    navigateBack: () -> Unit,
    attendanceViewModel: AttendanceViewModel = hiltViewModel()
) {
    val loading by attendanceViewModel.loading.collectAsState()
    val error by attendanceViewModel.error.collectAsState()
    var scanSuccess by remember { mutableStateOf<Boolean?>(null) }
    var lastScannedId by remember { mutableStateOf<String?>(null) }

    // QR Scanner Launcher
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            result.contents?.let { qrData ->
                // Process the QR code data
                lastScannedId = qrData
                attendanceViewModel.processQrCheckIn(qrData)
                scanSuccess = true
            } ?: run {
                // User cancelled the scan
                scanSuccess = false
            }
        }
    )

    LaunchedEffect(Unit) {
        // Launch QR scanner when screen opens
        scanLauncher.launch(ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt("Scan Student QR Code")
            setCameraId(0)
            setBeepEnabled(true)
            setOrientationLocked(false)
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Check-In") },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (loading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Processing check-in...")
            } else if (error != null) {
                Icon(
                    Icons.Default.Error,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error: $error",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        attendanceViewModel.clearError()
                        scanLauncher.launch(ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            setPrompt("Scan Student QR Code")
                            setCameraId(0)
                            setBeepEnabled(true)
                            setOrientationLocked(false)
                        })
                    }
                ) {
                    Text("Try Again")
                }
            } else if (scanSuccess == true) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Check-in successful!",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "ID: $lastScannedId",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        scanSuccess = null
                        scanLauncher.launch(ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            setPrompt("Scan Student QR Code")
                            setCameraId(0)
                            setBeepEnabled(true)
                            setOrientationLocked(false)
                        })
                    }
                ) {
                    Text("Scan Next Student")
                }
            } else if (scanSuccess == false) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = "Cancelled",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Scan cancelled",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        scanSuccess = null
                        scanLauncher.launch(ScanOptions().apply {
                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                            setPrompt("Scan Student QR Code")
                            setCameraId(0)
                            setBeepEnabled(true)
                            setOrientationLocked(false)
                        })
                    }
                ) {
                    Text("Try Again")
                }
            } else {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Preparing camera...")
            }
        }
    }
}