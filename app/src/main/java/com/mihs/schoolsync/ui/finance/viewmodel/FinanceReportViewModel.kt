// FinanceReportViewModel.kt
package com.mihs.schoolsync.ui.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.repository.FinanceReportRepository
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class FinanceReportViewModel @Inject constructor(
    private val reportRepository: FinanceReportRepository
) : ViewModel() {

    // Finance summary state
    private val _summaryState = MutableStateFlow<SummaryState>(SummaryState.Idle)
    val summaryState: StateFlow<SummaryState> = _summaryState.asStateFlow()

    // Payments by date range
    private val _paymentsState = MutableStateFlow<PaymentsState>(PaymentsState.Idle)
    val paymentsState: StateFlow<PaymentsState> = _paymentsState.asStateFlow()

    // Outstanding balances
    private val _balancesState = MutableStateFlow<BalancesState>(BalancesState.Idle)
    val balancesState: StateFlow<BalancesState> = _balancesState.asStateFlow()

    /**
     * Get financial summary data
     */
    fun getFinanceSummary() {
        viewModelScope.launch {
            _summaryState.value = SummaryState.Loading

            try {
                val result = reportRepository.getFinanceSummary()
                _summaryState.value = when (result) {
                    is Result.Success -> SummaryState.Success(result.data)
                    is Result.Error -> SummaryState.Error(result.message)
                    else -> SummaryState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _summaryState.value = SummaryState.Error("Error fetching finance summary: ${e.message}")
            }
        }
    }

    /**
     * Get payments by date range
     */
    fun getPaymentsByDateRange(startDate: Date, endDate: Date) {
        viewModelScope.launch {
            _paymentsState.value = PaymentsState.Loading

            try {
                val result = reportRepository.getPaymentsByDateRange(startDate, endDate)
                _paymentsState.value = when (result) {
                    is Result.Success -> PaymentsState.Success(result.data)
                    is Result.Error -> PaymentsState.Error(result.message)
                    else -> PaymentsState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _paymentsState.value = PaymentsState.Error("Error fetching payments: ${e.message}")
            }
        }
    }

    /**
     * Get outstanding balances
     */
    fun getOutstandingBalances() {
        viewModelScope.launch {
            _balancesState.value = BalancesState.Loading

            try {
                val result = reportRepository.getOutstandingBalances()
                _balancesState.value = when (result) {
                    is Result.Success -> BalancesState.Success(result.data)
                    is Result.Error -> BalancesState.Error(result.message)
                    else -> BalancesState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _balancesState.value = BalancesState.Error("Error fetching outstanding balances: ${e.message}")
            }
        }
    }

    /**
     * Data class for finance summary
     */
    data class FinanceSummary(
        val totalOutstandingFees: Double,
        val paymentsThisMonth: Double,
        val paymentsThisYear: Double,
        val recentTransactions: List<TransactionSummary>
    )

    /**
     * Data class for transaction summary
     */
    data class TransactionSummary(
        val id: Int,
        val type: String,
        val amount: Double,
        val description: String,
        val studentName: String,
        val date: Date
    )

    /**
     * Data class for student balance
     */
    data class StudentBalance(
        val studentId: Int,
        val studentName: String,
        val studentClass: String?,
        val balance: Double
    )

    /**
     * State classes for finance reports
     */
    sealed class SummaryState {
        object Idle : SummaryState()
        object Loading : SummaryState()
        data class Success(val summary: FinanceSummary) : SummaryState()
        data class Error(val message: String) : SummaryState()
    }

    sealed class PaymentsState {
        object Idle : PaymentsState()
        object Loading : PaymentsState()
        data class Success(val payments: List<PaymentResponse>) : PaymentsState()
        data class Error(val message: String) : PaymentsState()
    }

    sealed class BalancesState {
        object Idle : BalancesState()
        object Loading : BalancesState()
        data class Success(val balances: List<StudentBalance>) : BalancesState()
        data class Error(val message: String) : BalancesState()
    }
}