package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.data.models.LoginRequest
import com.mihs.schoolsync.data.models.LoginResponse
import com.mihs.schoolsync.data.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<LoginResponse>

    @POST("auth/refresh-token")
    suspend fun refreshToken(@Body refreshToken: Map<String, String>): Response<LoginResponse>

    @GET("health-check")
    suspend fun healthCheck(): Response<Map<String, String>>
}