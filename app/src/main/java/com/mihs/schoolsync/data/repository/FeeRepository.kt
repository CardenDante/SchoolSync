// FeeRepository.kt
package com.mihs.schoolsync.data.repository

import com.mihs.schoolsync.data.remote.FeeApiService
import com.mihs.schoolsync.ui.finance.models.FeeStatementResponse
import com.mihs.schoolsync.ui.finance.models.FinancePaymentType
import com.mihs.schoolsync.ui.finance.models.PaymentCreateRequest
import com.mihs.schoolsync.ui.finance.models.PaymentResponse
import com.mihs.schoolsync.utils.Result
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Date
import javax.inject.Inject

/**
 * Repository interface for fee-related operations
 */
interface FeeRepository {
    suspend fun getFeeStatement(
        studentId: Int,
        startDate: Date? = null,
        endDate: Date? = null
    ): Result<FeeStatementResponse>

    suspend fun submitPayment(
        studentId: Int,
        amount: Double,
        bankReference: String,
        paymentDate: Date,
        paymentType: FinancePaymentType,
        parentNotes: String? = null,
        bankSlipFile: File? = null
    ): Result<PaymentResponse>

    suspend fun getStudentPayments(studentId: Int): Result<List<PaymentResponse>>
}

/**
 * Implementation of the fee repository
 */
class FeeRepositoryImpl @Inject constructor(
    private val feeApiService: FeeApiService
) : FeeRepository {

    override suspend fun getFeeStatement(
        studentId: Int,
        startDate: Date?,
        endDate: Date?
    ): Result<FeeStatementResponse> {
        return try {
            val response = feeApiService.getFeeStatement(studentId, startDate, endDate)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to get fee statement")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching fee statement: ${e.message}")
        }
    }

    override suspend fun submitPayment(
        studentId: Int,
        amount: Double,
        bankReference: String,
        paymentDate: Date,
        paymentType: FinancePaymentType,
        parentNotes: String?,
        bankSlipFile: File?
    ): Result<PaymentResponse> {
        return try {
            val paymentRequest = PaymentCreateRequest(
                studentId = studentId,
                amount = amount,
                bankReference = bankReference,
                paymentDate = paymentDate,
                paymentType = paymentType,
                parentNotes = parentNotes
            )

            val response = if (bankSlipFile != null && bankSlipFile.exists()) {
                // Create multipart request with file
                val paymentDataJson = Gson().toJson(paymentRequest)
                val paymentDataPart = paymentDataJson.toRequestBody("application/json".toMediaTypeOrNull())

                val filePart = bankSlipFile.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData(
                    "bank_slip",
                    bankSlipFile.name,
                    filePart
                )

                feeApiService.submitPaymentWithFile(paymentDataPart, multipartBody)
            } else {
                // Submit without file
                feeApiService.submitPayment(paymentRequest)
            }

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to submit payment")
            }
        } catch (e: Exception) {
            Result.Error("Error submitting payment: ${e.message}")
        }
    }

    override suspend fun getStudentPayments(studentId: Int): Result<List<PaymentResponse>> {
        return try {
            val response = feeApiService.getStudentPayments(studentId)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(response.message() ?: "Failed to get student payments")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching student payments: ${e.message}")
        }
    }
}