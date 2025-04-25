package com.mihs.schoolsync.data.remote

import android.util.Log
import com.mihs.schoolsync.utils.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiService: AuthApiService
) : Interceptor {
    private val TAG = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip authentication for auth endpoints to avoid infinite loops
        if (isAuthEndpoint(originalRequest)) {
            return chain.proceed(originalRequest)
        }

        // Get the access token
        val accessToken = runBlocking { tokenManager.getAccessToken() }

        // If we don't have a token, proceed with the original request (will likely get 401)
        if (accessToken.isNullOrEmpty()) {
            Log.d(TAG, "No access token available")
            return chain.proceed(originalRequest)
        }

        // Add the Authorization header to the request
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        // Proceed with the authenticated request
        var response = chain.proceed(authenticatedRequest)

        // If we get a 401 Unauthorized, try to refresh the token
        if (response.code == 401) {
            Log.d(TAG, "Received 401 Unauthorized, attempting to refresh token")

            response.close()  // Close the response before retrying

            val refreshToken = runBlocking { tokenManager.getRefreshToken() }

            // If we don't have a refresh token, we can't refresh, so return the 401
            if (refreshToken.isNullOrEmpty()) {
                Log.d(TAG, "No refresh token available")
                return chain.proceed(originalRequest)
            }

            // Attempt to refresh the token
            try {
                val refreshResponse = runBlocking {
                    authApiService.refreshToken(mapOf("refresh_token" to refreshToken))
                }

                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    Log.d(TAG, "Token refresh successful")

                    // Save the new tokens
                    val loginResponse = refreshResponse.body()!!
                    runBlocking {
                        tokenManager.saveTokens(
                            accessToken = loginResponse.accessToken,
                            refreshToken = loginResponse.refreshToken ?: "",
                            userId = loginResponse.user.id.toString()
                        )
                    }

                    // Create a new request with the new token
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer ${loginResponse.accessToken}")
                        .build()

                    // Retry the request with the new token
                    return chain.proceed(newRequest)
                } else {
                    Log.d(TAG, "Token refresh failed with status: ${refreshResponse.code()}")

                    // If the refresh failed, clear the tokens and proceed with the original request
                    runBlocking { tokenManager.clearTokens() }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing token", e)

                // If there was an error refreshing, clear the tokens
                runBlocking { tokenManager.clearTokens() }
            }
        }

        return response
    }

    private fun isAuthEndpoint(request: Request): Boolean {
        val path = request.url.toString()
        return path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/auth/refresh-token") ||
                path.contains("/health-check")
    }
}