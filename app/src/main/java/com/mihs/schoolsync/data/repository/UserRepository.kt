// Updated UserRepository.kt to use User instead of UserResponse
package com.mihs.schoolsync.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mihs.schoolsync.data.models.User
import com.mihs.schoolsync.data.models.UserCreateRequest
import com.mihs.schoolsync.data.models.UserUpdateRequest
import com.mihs.schoolsync.data.remote.UserApiService
import com.mihs.schoolsync.utils.Result
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    companion object {
        private const val TAG = "UserRepository"
    }

    suspend fun getCurrentUser(): Result<User> {
        return try {
            Log.d(TAG, "Fetching current user")
            val response = userApiService.getCurrentUser()

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Current user fetched successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when fetching current user")
                    Result.Error("Failed to fetch user data")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching current user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching current user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching current user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching current user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getAllUsers(skip: Int = 0, limit: Int = 100): Result<List<User>> {
        return try {
            Log.d(TAG, "Fetching all users, skip: $skip, limit: $limit")
            val response = userApiService.getAllUsers(skip, limit)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Users fetched successfully: ${it.size} users")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when fetching users")
                    Result.Error("Failed to fetch users")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching users: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching users", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching users", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching users", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getUserById(userId: Int): Result<User> {
        return try {
            Log.d(TAG, "Fetching user with ID: $userId")
            val response = userApiService.getUserById(userId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "User fetched successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when fetching user")
                    Result.Error("Failed to fetch user data")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun createUser(userCreateRequest: UserCreateRequest): Result<User> {
        return try {
            Log.d(TAG, "Creating new user: ${userCreateRequest.username}")
            val response = userApiService.createUser(userCreateRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "User created successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when creating user")
                    Result.Error("Failed to create user")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error creating user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when creating user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when creating user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when creating user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun updateUser(userId: Int, userUpdateRequest: UserUpdateRequest): Result<User> {
        return try {
            Log.d(TAG, "Updating user with ID: $userId")
            val response = userApiService.updateUser(userId, userUpdateRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "User updated successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when updating user")
                    Result.Error("Failed to update user")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error updating user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when updating user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when updating user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when updating user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun updateCurrentUser(userUpdateRequest: UserUpdateRequest): Result<User> {
        return try {
            Log.d(TAG, "Updating current user")
            val response = userApiService.updateCurrentUser(userUpdateRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Current user updated successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when updating current user")
                    Result.Error("Failed to update user profile")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error updating current user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when updating current user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when updating current user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when updating current user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun deleteUser(userId: Int): Result<User> {
        return try {
            Log.d(TAG, "Deleting user with ID: $userId")
            val response = userApiService.deleteUser(userId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "User deleted successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when deleting user")
                    Result.Error("Failed to delete user")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error deleting user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when deleting user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when deleting user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when deleting user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun activateUser(userId: Int): Result<User> {
        return try {
            Log.d(TAG, "Activating user with ID: $userId")
            val response = userApiService.activateUser(userId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "User activated successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when activating user")
                    Result.Error("Failed to activate user")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error activating user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when activating user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when activating user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when activating user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun deactivateUser(userId: Int): Result<User> {
        return try {
            Log.d(TAG, "Deactivating user with ID: $userId")
            val response = userApiService.deactivateUser(userId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "User deactivated successfully")
                    Result.Success(it)
                } ?: run {
                    Log.e(TAG, "Empty response when deactivating user")
                    Result.Error("Failed to deactivate user")
                }
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error deactivating user: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when deactivating user", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when deactivating user", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when deactivating user", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

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
