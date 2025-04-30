// FeeViewModel.kt
package com.mihs.schoolsync.ui.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.repository.FeeRepository
import com.mihs.schoolsync.ui.finance.models.FeeStatementResponse
import com.mihs.schoolsync.ui.finance.models.FinancePaymentType
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class FeeViewModel @Inject constructor(
    private val feeRepository: FeeRepository
) : ViewModel() {

    // Fee statement state
    private val _feeStatementState = MutableStateFlow<FeeStatementState>(FeeStatementState.Idle)
    val feeStatementState: StateFlow<FeeStatementState> = _feeStatementState.asStateFlow()

    // Payment submission state
    private val _paymentSubmissionState = MutableStateFlow<PaymentSubmissionState>(PaymentSubmissionState.Idle)
    val paymentSubmissionState: StateFlow<PaymentSubmissionState> = _paymentSubmissionState.asStateFlow()

    // Student payments history state
    private val _paymentsHistoryState = MutableStateFlow<PaymentsHistoryState>(PaymentsHistoryState.Idle)
    val paymentsHistoryState: StateFlow<PaymentsHistoryState> = _paymentsHistoryState.asStateFlow()

    /**
     * Get fee statement for a student
     */
    fun getFeeStatement(studentId: Int, startDate: Date? = null, endDate: Date? = null) {
        viewModelScope.launch {
            _feeStatementState.value = FeeStatementState.Loading

            try {
                val result = feeRepository.getFeeStatement(studentId, startDate, endDate)
                _feeStatementState.value = when (result) {
                    is Result.Success -> FeeStatementState.Success(result.data)
                    is Result.Error -> FeeStatementState.Error(result.message)
                    else -> FeeStatementState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _feeStatementState.value = FeeStatementState.Error("Error fetching fee statement: ${e.message}")
            }
        }
    }

    /**
     * Submit a payment
     */
    fun submitPayment(
        studentId: Int,
        amount: Double,
        bankReference: String,
        paymentDate: Date,
        paymentType: FinancePaymentType,
        parentNotes: String? = null,
        bankSlipFile: File? = null
    ) {
        viewModelScope.launch {
            _paymentSubmissionState.value = PaymentSubmissionState.Loading

            try {
                val result = feeRepository.submitPayment(
                    studentId, amount, bankReference, paymentDate,
                    paymentType, parentNotes, bankSlipFile
                )

                _paymentSubmissionState.value = when (result) {
                    is Result.Success -> PaymentSubmissionState.Success(result.data)
                    is Result.Error -> PaymentSubmissionState.Error(result.message)
                    else -> PaymentSubmissionState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _paymentSubmissionState.value = PaymentSubmissionState.Error("Error submitting payment: ${e.message}")
            }
        }
    }

    /**
     * Get payment history for a student
     */
    fun getStudentPayments(studentId: Int) {
        viewModelScope.launch {
            _paymentsHistoryState.value = PaymentsHistoryState.Loading

            try {
                val result = feeRepository.getStudentPayments(studentId)
                _paymentsHistoryState.value = when (result) {
                    is Result.Success -> PaymentsHistoryState.Success(result.data)
                    is Result.Error -> PaymentsHistoryState.Error(result.message)
                    else -> PaymentsHistoryState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _paymentsHistoryState.value = PaymentsHistoryState.Error("Error fetching payment history: ${e.message}")
            }
        }
    }

    /**
     * Reset payment submission state
     */
    fun resetPaymentSubmissionState() {
        _paymentSubmissionState.value = PaymentSubmissionState.Idle
    }

    /**
     * State classes for fee module
     */
    sealed class FeeStatementState {
        object Idle : FeeStatementState()
        object Loading : FeeStatementState()
        data class Success(val statement: FeeStatementResponse) : FeeStatementState()
        data class Error(val message: String) : FeeStatementState()
    }

    sealed class PaymentSubmissionState {
        object Idle : PaymentSubmissionState()
        object Loading : PaymentSubmissionState()
        data class Success(val payment: PaymentResponse) : PaymentSubmissionState()
        data class Error(val message: String) : PaymentSubmissionState()
    }

    sealed class PaymentsHistoryState {
        object Idle : PaymentsHistoryState()
        object Loading : PaymentsHistoryState()
        data class Success(val payments: List<PaymentResponse>) : PaymentsHistoryState()
        data class Error(val message: String) : PaymentsHistoryState()
    }
}