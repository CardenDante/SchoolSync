package com.mihs.schoolsync.data.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    val user: UserResponse
)

data class UserResponse(
    val id: Int,
    val uuid: String,
    val username: String,
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("user_type")
    val userType: String,
    val status: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_verified")
    val isVerified: Boolean,
    val roles: List<String>
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("user_type")
    val userType: String = "STUDENT"
)