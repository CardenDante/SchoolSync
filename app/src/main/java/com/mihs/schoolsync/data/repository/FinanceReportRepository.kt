// FinanceReportRepository.kt
package com.mihs.schoolsync.data.repository

import com.mihs.schoolsync.data.remote.FinanceReportApiService
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import com.mihs.schoolsync.ui.finance.viewmodel.FinanceReportViewModel
import com.mihs.schoolsync.utils.Result
import java.util.*
import javax.inject.Inject

/**
 * Repository interface for financial reports
 */
interface FinanceReportRepository {
    suspend fun getFinanceSummary(): Result<FinanceReportViewModel.FinanceSummary>
    suspend fun getPaymentsByDateRange(startDate: Date, endDate: Date): Result<List<PaymentResponse>>
    suspend fun getOutstandingBalances(): Result<List<FinanceReportViewModel.StudentBalance>>
}

/**
 * Implementation of financial reports repository
 */
class FinanceReportRepositoryImpl @Inject constructor(
    private val reportApiService: FinanceReportApiService
) : FinanceReportRepository {

    override suspend fun getFinanceSummary(): Result<FinanceReportViewModel.FinanceSummary> {
        return try {
            val response = reportApiService.getFinanceSummary()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(mapToFinanceSummary(response.body()!!))
            } else {
                Result.Error(response.message() ?: "Failed to get finance summary")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching finance summary: ${e.message}")
        }
    }

    override suspend fun getPaymentsByDateRange(startDate: Date, endDate: Date): Result<List<PaymentResponse>> {
        return try {
            val response = reportApiService.getPaymentsByDateRange(startDate, endDate)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(response.message() ?: "Failed to get payments by date range")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching payments: ${e.message}")
        }
    }

    override suspend fun getOutstandingBalances(): Result<List<FinanceReportViewModel.StudentBalance>> {
        return try {
            val response = reportApiService.getOutstandingBalances()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(mapToStudentBalances(response.body()!!))
            } else {
                Result.Error(response.message() ?: "Failed to get outstanding balances")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching outstanding balances: ${e.message}")
        }
    }

    // Mapping functions from API responses to ViewModel data classes
    private fun mapToFinanceSummary(apiResponse: FinanceReportApiService.FinanceSummaryResponse): FinanceReportViewModel.FinanceSummary {
        return FinanceReportViewModel.FinanceSummary(
            totalOutstandingFees = apiResponse.totalOutstandingFees,
            paymentsThisMonth = apiResponse.paymentsThisMonth,
            paymentsThisYear = apiResponse.paymentsThisYear,
            recentTransactions = apiResponse.recentTransactions.map {
                FinanceReportViewModel.TransactionSummary(
                    id = it.id,
                    type = it.type,
                    amount = it.amount,
                    description = it.description,
                    studentName = it.studentName,
                    date = it.date
                )
            }
        )
    }

    private fun mapToStudentBalances(apiResponse: List<FinanceReportApiService.StudentBalanceResponse>): List<FinanceReportViewModel.StudentBalance> {
        return apiResponse.map {
            FinanceReportViewModel.StudentBalance(
                studentId = it.studentId,
                studentName = it.studentName,
                studentClass = it.studentClass,
                balance = it.balance
            )
        }
    }
}