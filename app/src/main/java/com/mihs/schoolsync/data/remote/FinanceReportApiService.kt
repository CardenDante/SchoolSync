// FinanceReportApiService.kt
package com.mihs.schoolsync.data.remote

import com.google.gson.annotations.SerializedName
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

/**
 * API service for financial reports
 */
interface FinanceReportApiService {
    /**
     * Get financial summary data
     */
    @GET("/api/v1/reports/finance/summary")
    suspend fun getFinanceSummary(): Response<FinanceSummaryResponse>

    /**
     * Get payments by date range
     */
    @GET("/api/v1/reports/finance/payments")
    suspend fun getPaymentsByDateRange(
        @Query("start_date") startDate: Date,
        @Query("end_date") endDate: Date
    ): Response<List<PaymentResponse>>

    /**
     * Get students with outstanding balances
     */
    @GET("/api/v1/reports/finance/outstanding")
    suspend fun getOutstandingBalances(): Response<List<StudentBalanceResponse>>

    /**
     * Data class for finance summary response
     */
    data class FinanceSummaryResponse(
        @SerializedName("total_outstanding_fees")
        val totalOutstandingFees: Double,

        @SerializedName("payments_this_month")
        val paymentsThisMonth: Double,

        @SerializedName("payments_this_year")
        val paymentsThisYear: Double,

        @SerializedName("recent_transactions")
        val recentTransactions: List<TransactionSummaryResponse>
    )

    /**
     * Data class for transaction summary response
     */
    data class TransactionSummaryResponse(
        val id: Int,
        val type: String,
        val amount: Double,
        val description: String,
        @SerializedName("student_name")
        val studentName: String,
        val date: Date
    )

    /**
     * Data class for student balance response
     */
    data class StudentBalanceResponse(
        @SerializedName("student_id")
        val studentId: Int,

        @SerializedName("student_name")
        val studentName: String,

        @SerializedName("student_class")
        val studentClass: String?,

        val balance: Double
    )
}