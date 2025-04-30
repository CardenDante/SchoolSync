// FeeApiService.kt
package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.ui.finance.models.FeeStatementResponse
import com.mihs.schoolsync.ui.finance.models.PaymentCreateRequest
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.util.Date

/**
 * API service for fee and payment related endpoints
 */
interface FeeApiService {
    /**
     * Get fee statement for a student
     */
    @GET("/api/v1/fees/students/{student_id}/statement")
    suspend fun getFeeStatement(
        @Path("student_id") studentId: Int,
        @Query("start_date") startDate: Date? = null,
        @Query("end_date") endDate: Date? = null
    ): Response<FeeStatementResponse>

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
     * Get payment history for a student
     */
    @GET("/api/v1/payments/student/{student_id}")
    suspend fun getStudentPayments(
        @Path("student_id") studentId: Int
    ): Response<List<PaymentResponse>>
}