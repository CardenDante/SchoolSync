package com.mihs.schoolsync.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread

/**
 * Class to monitor network connectivity and check internet access.
 * Provides a Flow that emits network status changes.
 */
@Singleton
class NetworkConnectivityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    /**
     * Returns a Flow that emits network status changes.
     * Will emit true if the network is available and false otherwise.
     */
    val networkStatus: Flow<Boolean> = callbackFlow {
        // Initially send the current network status
        trySend(isNetworkAvailable())

        // Create a callback to monitor network changes
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.d("Network available")
                trySend(true)
            }

            override fun onLost(network: Network) {
                Timber.d("Network lost")
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                if (hasInternet) {
                    // Verify internet connectivity by pinging the server
                    thread {
                        val hasRealConnection = pingServer()
                        Timber.d("Internet ping test: $hasRealConnection")
                        trySend(hasRealConnection)
                    }
                } else {
                    trySend(false)
                }
            }
        }

        // Register callback to monitor all network changes
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)

        // When the flow collection is cancelled, unregister the callback
        awaitClose {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        }
    }.distinctUntilChanged() // Only emit when the status changes

    /**
     * Checks if the device has an active network connection.
     */
    fun isNetworkAvailable(): Boolean {
        val activeNetwork = connectivityManager?.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Checks if the server is reachable.
     * @param hostName The server host name to check
     * @param port The server port to check
     * @param timeout The timeout in milliseconds
     */
    fun isServerReachable(
        hostName: String = "192.168.100.22",
        port: Int = 8000,
        timeout: Int = 3000
    ): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(hostName, port), timeout)
                true
            }
        } catch (e: IOException) {
            Timber.e(e, "Server connectivity check failed")
            false
        }
    }

    /**
     * Pings the server to check if it's responding.
     */
    fun pingServer(): Boolean {
        val serverUrl = "http://192.168.100.22:8000/api/v1/health" // Add a health endpoint to your API
        return try {
            val url = URL(serverUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.readTimeout = 3000
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            connection.disconnect()

            responseCode == 200
        } catch (e: Exception) {
            Timber.e(e, "Failed to ping server")
            false
        }
    }
}