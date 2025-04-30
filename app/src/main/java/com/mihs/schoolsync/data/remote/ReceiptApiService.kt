// ReceiptApiService.kt
package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.ui.finance.models.ReceiptResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

/**
 * API service for receipt-related endpoints
 */
interface ReceiptApiService {
    /**
     * Get receipt for a payment
     */
    @GET("/api/v1/receipts/payment/{payment_id}")
    suspend fun getReceipt(
        @Path("payment_id") paymentId: Int
    ): Response<ReceiptResponse>

    /**
     * Generate a receipt for a payment (admin only)
     */
    @POST("/api/v1/payments/{payment_id}/receipt")
    suspend fun generateReceipt(
        @Path("payment_id") paymentId: Int
    ): Response<ReceiptResponse>

    /**
     * Download receipt PDF
     */
    @GET
    suspend fun downloadReceiptPdf(
        @Url url: String
    ): Response<ResponseBody>
}