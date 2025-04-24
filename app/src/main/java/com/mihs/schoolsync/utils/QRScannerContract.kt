// QRScannerContract.kt
package com.mihs.schoolsync.utils

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun rememberQRScannerLauncher(
    onResult: (String?) -> Unit
) = rememberLauncherForActivityResult(
    contract = ScanContract(),
    onResult = { result ->
        onResult(result.contents)
    }
)