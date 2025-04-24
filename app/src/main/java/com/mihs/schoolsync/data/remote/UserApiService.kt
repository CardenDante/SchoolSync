package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.data.models.User
import com.mihs.schoolsync.data.models.UserCreateRequest
import com.mihs.schoolsync.data.models.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.*

interface UserApiService {
    @GET("users/me")
    suspend fun getCurrentUser(): Response<User>

    @GET("users")
    suspend fun getAllUsers(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): Response<List<User>>

    @GET("users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Int
    ): Response<User>

    @POST("users")
    suspend fun createUser(
        @Body userCreateRequest: UserCreateRequest
    ): Response<User>

    @PATCH("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body userUpdateRequest: UserUpdateRequest
    ): Response<User>

    @PATCH("users/me")
    suspend fun updateCurrentUser(
        @Body userUpdateRequest: UserUpdateRequest
    ): Response<User>

    @DELETE("users/{userId}")
    suspend fun deleteUser(
        @Path("userId") userId: Int
    ): Response<User>

    @POST("users/{userId}/activate")
    suspend fun activateUser(
        @Path("userId") userId: Int
    ): Response<User>

    @POST("users/{userId}/deactivate")
    suspend fun deactivateUser(
        @Path("userId") userId: Int
    ): Response<User>
}