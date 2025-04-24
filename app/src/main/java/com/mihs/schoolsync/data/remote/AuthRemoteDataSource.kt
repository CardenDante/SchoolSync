package com.mihs.schoolsync.data.remote

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mihs.schoolsync.data.models.LoginRequest
import com.mihs.schoolsync.data.models.LoginResponse
import com.mihs.schoolsync.data.models.RegisterRequest
import com.mihs.schoolsync.utils.Result
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRemoteDataSource @Inject constructor(
    private val authApiService: AuthApiService
) {
    companion object {
        private const val TAG = "AuthRemoteDataSource"
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            Log.d(TAG, "Attempting login API call for: $email")
            val response = authApiService.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Login API call successful")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Login API call returned empty body")
                    Result.Error("Login response is empty")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Login API call failed: ${response.code()}, body: $errorBody")

                val errorMessage = parseErrorMessage(errorBody, response.code())
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during login API call", e)
            Result.Error("Network error: Check your internet connection")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during login API call: ${e.code()}", e)
            Result.Error("HTTP error ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during login API call", e)
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
            Log.d(TAG, "Attempting registration API call for: $email")
            val registerRequest = RegisterRequest(
                username = username,
                email = email,
                password = password,
                confirmPassword = password,
                fullName = fullName
            )

            Log.d(TAG, "Registration request: $registerRequest")
            val response = authApiService.register(registerRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Registration API call successful")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Registration API call returned empty body")
                    Result.Error("Registration response is empty")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Registration API call failed: ${response.code()}, body: $errorBody")

                val errorMessage = parseErrorMessage(errorBody, response.code())
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error during registration API call", e)
            Result.Error("Network error: Check your internet connection")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error during registration API call: ${e.code()}", e)
            Result.Error("HTTP error ${e.code()}: ${e.message()}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during registration API call", e)
            Result.Error("An unexpected error occurred: ${e.localizedMessage ?: "Unknown error"}")
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