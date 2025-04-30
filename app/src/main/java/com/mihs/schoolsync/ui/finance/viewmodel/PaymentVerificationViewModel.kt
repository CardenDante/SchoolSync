// PaymentVerificationViewModel.kt
package com.mihs.schoolsync.ui.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.repository.PaymentRepository
import com.mihs.schoolsync.ui.finance.models.FinancePaymentStatus
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import com.mihs.schoolsync.ui.finance.models.PaymentVerifyRequest
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentVerificationViewModel @Inject constructor(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    // Pending payments state
    private val _pendingPaymentsState = MutableStateFlow<PendingPaymentsState>(PendingPaymentsState.Idle)
    val pendingPaymentsState: StateFlow<PendingPaymentsState> = _pendingPaymentsState.asStateFlow()

    // Verification state
    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState.asStateFlow()

    /**
     * Get all pending payments that need verification
     */
    fun getPendingPayments() {
        viewModelScope.launch {
            _pendingPaymentsState.value = PendingPaymentsState.Loading

            try {
                val result = paymentRepository.getPendingPayments()
                _pendingPaymentsState.value = when (result) {
                    is Result.Success -> PendingPaymentsState.Success(result.data)
                    is Result.Error -> PendingPaymentsState.Error(result.message)
                    else -> PendingPaymentsState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _pendingPaymentsState.value = PendingPaymentsState.Error("Error fetching pending payments: ${e.message}")
            }
        }
    }

    /**
     * Verify a payment
     */
    fun verifyPayment(
        paymentId: Int,
        status: FinancePaymentStatus,
        verifiedAmount: Double,
        adminNotes: String? = null
    ) {
        viewModelScope.launch {
            _verificationState.value = VerificationState.Loading

            try {
                val verifyRequest = PaymentVerifyRequest(
                    status = status,
                    verifiedAmount = verifiedAmount,
                    adminNotes = adminNotes
                )

                val result = paymentRepository.verifyPayment(paymentId, verifyRequest)
                _verificationState.value = when (result) {
                    is Result.Success -> VerificationState.Success(result.data)
                    is Result.Error -> VerificationState.Error(result.message)
                    else -> VerificationState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error("Error verifying payment: ${e.message}")
            }
        }
    }

    /**
     * Reset verification state
     */
    fun resetVerificationState() {
        _verificationState.value = VerificationState.Idle
    }

    /**
     * State classes for pending payments and verification
     */
    sealed class PendingPaymentsState {
        object Idle : PendingPaymentsState()
        object Loading : PendingPaymentsState()
        data class Success(val payments: List<PaymentResponse>) : PendingPaymentsState()
        data class Error(val message: String) : PendingPaymentsState()
    }

    sealed class VerificationState {
        object Idle : VerificationState()
        object Loading : VerificationState()
        data class Success(val payment: PaymentResponse) : VerificationState()
        data class Error(val message: String) : VerificationState()
    }
}