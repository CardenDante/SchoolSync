package com.mihs.schoolsync.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class User(
    val id: Int,
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_superuser")
    val isSuperuser: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val roles: List<String>,
    val username: String,
    val uuid: String,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("user_type")
    val userType: String,
    val status: String,
    @SerializedName("is_verified")
    val isVerified: Boolean,
    @SerializedName("last_login")
    val lastLogin: String?
)

data class UserCreateRequest(
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    val password: String,
    @SerializedName("confirm_password")
    val confirmPassword: String,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("is_superuser")
    val isSuperuser: Boolean = false,
    val username: String,
    @SerializedName("user_type")
    val userType: String,
    @SerializedName("phone_number")
    val phoneNumber: String?
)

data class UserUpdateRequest(
    val email: String? = null,
    @SerializedName("full_name")
    val fullName: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null
)