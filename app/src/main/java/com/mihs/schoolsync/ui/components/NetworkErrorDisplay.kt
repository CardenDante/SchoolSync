package com.mihs.schoolsync.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mihs.schoolsync.utils.NetworkConnectivityManager
import kotlinx.coroutines.flow.collectLatest

/**
 * A composable that displays network error information and provides retry functionality.
 *
 * @param message The error message to display
 * @param onRetry Callback to retry the operation
 * @param showServerInfo Whether to show detailed server information
 */
@Composable
fun NetworkErrorDisplay(
    message: String,
    onRetry: () -> Unit,
    showServerInfo: Boolean = false,
    connectivityManager: NetworkConnectivityManager? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SignalWifiOff,
            contentDescription = "Network Error",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Connection Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (showServerInfo && connectivityManager != null) {
            ServerStatusInfo(connectivityManager)

            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Try Again")
        }
    }
}

/**
 * Displays server connectivity status information.
 */
@Composable
private fun ServerStatusInfo(connectivityManager: NetworkConnectivityManager) {
    var isNetworkAvailable by remember { mutableStateOf(connectivityManager.isNetworkAvailable()) }
    var isServerReachable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Check server reachability in a non-UI thread
        isServerReachable = connectivityManager.isServerReachable()

        // Monitor network status changes
        connectivityManager.networkStatus.collectLatest {
            isNetworkAvailable = it
            if (it) {
                isServerReachable = connectivityManager.isServerReachable()
            } else {
                isServerReachable = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Network Status:",
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Internet Connection:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (isNetworkAvailable) "Available" else "Unavailable",
                    color = if (isNetworkAvailable)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Server Connection:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = if (isServerReachable) "Reachable" else "Unreachable",
                    color = if (isServerReachable)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}