// ReceiptViewModel.kt
package com.mihs.schoolsync.ui.finance.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.repository.ReceiptRepository
import com.mihs.schoolsync.ui.finance.models.ReceiptResponse
import com.mihs.schoolsync.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // Receipt state
    private val _receiptState = MutableStateFlow<ReceiptState>(ReceiptState.Idle)
    val receiptState: StateFlow<ReceiptState> = _receiptState.asStateFlow()

    // Download state
    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    /**
     * Get receipt for a payment
     */
    fun getReceipt(paymentId: Int) {
        viewModelScope.launch {
            _receiptState.value = ReceiptState.Loading

            try {
                val result = receiptRepository.getReceipt(paymentId)
                _receiptState.value = when (result) {
                    is Result.Success -> ReceiptState.Success(result.data)
                    is Result.Error -> ReceiptState.Error(result.message)
                    else -> ReceiptState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _receiptState.value = ReceiptState.Error("Error fetching receipt: ${e.message}")
            }
        }
    }

    /**
     * Generate receipt for a payment (for admin use)
     */
    fun generateReceipt(paymentId: Int) {
        viewModelScope.launch {
            _receiptState.value = ReceiptState.Loading

            try {
                val result = receiptRepository.generateReceipt(paymentId)
                _receiptState.value = when (result) {
                    is Result.Success -> ReceiptState.Success(result.data)
                    is Result.Error -> ReceiptState.Error(result.message)
                    else -> ReceiptState.Error("Unknown error occurred")
                }
            } catch (e: Exception) {
                _receiptState.value = ReceiptState.Error("Error generating receipt: ${e.message}")
            }
        }
    }

    /**
     * Download receipt PDF
     * Returns the download state for handling UI feedback
     */
    suspend fun downloadReceipt(receipt: ReceiptResponse): DownloadState {
        _downloadState.value = DownloadState.Loading

        try {
            val result = receiptRepository.downloadReceiptPdf(receipt)
            val state = when (result) {
                is Result.Success -> DownloadState.Success(result.data)
                is Result.Error -> DownloadState.Error(result.message)
                else -> DownloadState.Error("Unknown error occurred")
            }
            _downloadState.value = state
            return state
        } catch (e: Exception) {
            val errorState = DownloadState.Error("Error downloading receipt: ${e.message}")
            _downloadState.value = errorState
            return errorState
        }
    }

    /**
     * Reset download state
     */
    fun resetDownloadState() {
        _downloadState.value = DownloadState.Idle
    }

    /**
     * State classes for receipt management
     */
    sealed class ReceiptState {
        object Idle : ReceiptState()
        object Loading : ReceiptState()
        data class Success(val receipt: ReceiptResponse) : ReceiptState()
        data class Error(val message: String) : ReceiptState()
    }

    sealed class DownloadState {
        object Idle : DownloadState()
        object Loading : DownloadState()
        data class Success(val file: File) : DownloadState()
        data class Error(val message: String) : DownloadState()
    }
}