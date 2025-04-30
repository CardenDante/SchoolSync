// PaymentApiService.kt
package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.ui.finance.models.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.Date

/**
 * API service for payment-related endpoints
 */
interface PaymentApiService {
    /**
     * Submit a payment
     */
    @POST("/api/v1/payments/submit")
    suspend fun submitPayment(
        @Body payment: PaymentCreateRequest
    ): Response<PaymentResponse>

    /**
     * Submit a payment with a bank slip file
     */
    @Multipart
    @POST("/api/v1/payments/submit")
    suspend fun submitPaymentWithFile(
        @Part("payment_data") paymentData: RequestBody,
        @Part bankSlip: MultipartBody.Part
    ): Response<PaymentResponse>

    /**
     * Get all pending payments that need verification (admin only)
     */
    @GET("/api/v1/payments/pending")
    suspend fun getPendingPayments(): Response<List<PaymentResponse>>

    /**
     * Get payment history for a student
     */
    @GET("/api/v1/payments/student/{student_id}")
    suspend fun getStudentPayments(
        @Path("student_id") studentId: Int
    ): Response<List<PaymentResponse>>

    /**
     * Verify a payment (admin only)
     */
    @POST("/api/v1/payments/{payment_id}/verify")
    suspend fun verifyPayment(
        @Path("payment_id") paymentId: Int,
        @Body verificationData: PaymentVerifyRequest
    ): Response<PaymentResponse>

    /**
     * Create a payment request for a student (admin only)
     */
    @POST("/api/v1/payments/requests")
    suspend fun createPaymentRequest(
        @Body requestData: PaymentRequestCreate
    ): Response<PaymentRequest>
}