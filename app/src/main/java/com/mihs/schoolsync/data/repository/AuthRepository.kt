package com.mihs.schoolsync.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mihs.schoolsync.data.models.LoginRequest
import com.mihs.schoolsync.data.models.LoginResponse
import com.mihs.schoolsync.data.models.RegisterRequest
import com.mihs.schoolsync.data.remote.AuthApiService
import com.mihs.schoolsync.utils.Result
import com.mihs.schoolsync.utils.TokenManager
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            Log.d(TAG, "Attempting login for: $email")
            val response = authApiService.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    // Save tokens
                    Log.d(TAG, "Login successful, saving tokens")
                    tokenManager.saveTokens(
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken ?: "",
                        userId = loginResponse.user.id.toString()
                    )
                    Result.Success(loginResponse)
                } ?: run {
                    Log.e(TAG, "Login response body is null")
                    Result.Error("Login response is empty")
                }
            } else {
                // Parse error response
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Login failed with code: ${response.code()}, error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody, response.code())
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during login", e)
            Result.Error("Network error: Check your internet connection")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during login: ${e.code()}", e)
            Result.Error("HTTP error ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during login", e)
            Result.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun register(
        username: String,
        email: String,
        password: String,
        fullName: String
    ): Result<LoginResponse> {
        return try {
            Log.d(TAG, "Attempting registration for: $email")
            val response = authApiService.register(
                RegisterRequest(
                    username = username,
                    email = email,
                    password = password,
                    confirmPassword = password,
                    fullName = fullName
                )
            )

            if (response.isSuccessful) {
                response.body()?.let { registrationResponse ->
                    // Save tokens from registration response
                    Log.d(TAG, "Registration successful, saving tokens")
                    tokenManager.saveTokens(
                        accessToken = registrationResponse.accessToken,
                        refreshToken = registrationResponse.refreshToken ?: "",
                        userId = registrationResponse.user.id.toString()
                    )
                    Result.Success(registrationResponse)
                } ?: run {
                    Log.e(TAG, "Registration response body is null")
                    Result.Error("Registration response is empty")
                }
            } else {
                // Parse error response
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Registration failed with code: ${response.code()}, error: $errorBody")

                val errorMessage = parseErrorMessage(errorBody, response.code())
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during registration", e)
            Result.Error("Network error: Check your internet connection")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during registration: ${e.code()}", e)
            Result.Error("HTTP error ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during registration", e)
            Result.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun refreshAccessToken(): Result<LoginResponse> {
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            Log.e(TAG, "Refresh token is null or empty")
            return Result.Error("No refresh token available")
        }
        return refreshToken(refreshToken)
    }

    suspend fun refreshToken(refreshToken: String): Result<LoginResponse> {
        return try {
            Log.d(TAG, "Attempting to refresh token")
            val response = authApiService.refreshToken(
                mapOf("refresh_token" to refreshToken)
            )

            if (response.isSuccessful) {
                response.body()?.let { refreshResponse ->
                    // Update tokens
                    Log.d(TAG, "Token refresh successful, updating tokens")
                    tokenManager.saveTokens(
                        accessToken = refreshResponse.accessToken,
                        refreshToken = refreshResponse.refreshToken ?: "",
                        userId = refreshResponse.user.id.toString()
                    )
                    Result.Success(refreshResponse)
                } ?: run {
                    Log.e(TAG, "Refresh token response body is null")
                    Result.Error("Refresh token response is empty")
                }
            } else {
                // Parse error response
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Token refresh failed with code: ${response.code()}, error: $errorBody")

                // If unauthorized, clear tokens
                if (response.code() == 401) {
                    Log.d(TAG, "Unauthorized refresh token, clearing tokens")
                    tokenManager.clearTokens()
                }

                val errorMessage = parseErrorMessage(errorBody, response.code())
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during token refresh", e)
            Result.Error("Network error: Check your internet connection")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during token refresh: ${e.code()}", e)
            Result.Error("HTTP error ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during token refresh", e)
            Result.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    suspend fun testApiConnection(): Result<String> {
        return try {
            Log.d(TAG, "Testing API connection")
            val response = authApiService.healthCheck()

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "API connection successful: $it")
                    Result.Success("Connected to API successfully")
                } ?: Result.Error("Empty response from API")
            } else {
                Log.e(TAG, "API connection test failed with code: ${response.code()}")
                Result.Error("Failed to connect to API: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error testing API connection", e)
            Result.Error("Failed to connect to API: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    // Helper function to parse error messages from API responses
    private fun parseErrorMessage(errorBody: String?, statusCode: Int): String {
        if (errorBody.isNullOrEmpty()) {
            return "Error: HTTP $statusCode"
        }

        return try {
            val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
            when {
                errorJson.has("detail") -> errorJson.get("detail").asString
                errorJson.has("message") -> errorJson.get("message").asString
                else -> "Error: HTTP $statusCode"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing error response", e)
            "Error: HTTP $statusCode"
        }
    }
}