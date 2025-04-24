package com.mihs.schoolsync.data.remote

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import com.mihs.schoolsync.utils.TokenManager

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Use runBlocking cautiously
        val token = runBlocking { tokenManager.getAccessToken() }

        val authenticatedRequest = token?.let { accessToken ->
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } ?: originalRequest

        return chain.proceed(authenticatedRequest)
    }
}