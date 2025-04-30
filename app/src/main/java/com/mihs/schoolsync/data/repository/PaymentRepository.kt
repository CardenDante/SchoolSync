// PaymentRepository.kt
package com.mihs.schoolsync.data.repository

import android.content.Context
import com.mihs.schoolsync.data.remote.PaymentApiService
import com.mihs.schoolsync.ui.finance.models.*
import com.mihs.schoolsync.utils.Result
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.Date
import javax.inject.Inject

/**
 * Repository interface for payment-related operations
 */
interface PaymentRepository {
    suspend fun submitPayment(
        studentId: Int,
        amount: Double,
        bankReference: String,
        paymentDate: Date,
        paymentType: FinancePaymentType,
        parentNotes: String? = null,
        bankSlipFile: File? = null
    ): Result<PaymentResponse>

    suspend fun getPendingPayments(): Result<List<PaymentResponse>>

    suspend fun getStudentPayments(studentId: Int): Result<List<PaymentResponse>>

    suspend fun verifyPayment(
        paymentId: Int,
        verificationData: PaymentVerifyRequest
    ): Result<PaymentResponse>

    suspend fun createPaymentRequest(
        studentId: Int,
        amount: Double,
        dueDate: Date,
        paymentType: FinancePaymentType,
        description: String,
        isMandatory: Boolean
    ): Result<PaymentRequest>
}

/**
 * Implementation of the payment repository
 */
class PaymentRepositoryImpl @Inject constructor(
    private val paymentApiService: PaymentApiService,
    @ApplicationContext private val context: Context
) : PaymentRepository {

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

                paymentApiService.submitPaymentWithFile(paymentDataPart, multipartBody)
            } else {
                // Submit without file
                paymentApiService.submitPayment(paymentRequest)
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

    override suspend fun getPendingPayments(): Result<List<PaymentResponse>> {
        return try {
            val response = paymentApiService.getPendingPayments()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(response.message() ?: "Failed to get pending payments")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching pending payments: ${e.message}")
        }
    }

    override suspend fun getStudentPayments(studentId: Int): Result<List<PaymentResponse>> {
        return try {
            val response = paymentApiService.getStudentPayments(studentId)
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error(response.message() ?: "Failed to get student payments")
            }
        } catch (e: Exception) {
            Result.Error("Error fetching student payments: ${e.message}")
        }
    }

    override suspend fun verifyPayment(
        paymentId: Int,
        verificationData: PaymentVerifyRequest
    ): Result<PaymentResponse> {
        return try {
            val response = paymentApiService.verifyPayment(paymentId, verificationData)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to verify payment")
            }
        } catch (e: Exception) {
            Result.Error("Error verifying payment: ${e.message}")
        }
    }

    override suspend fun createPaymentRequest(
        studentId: Int,
        amount: Double,
        dueDate: Date,
        paymentType: FinancePaymentType,
        description: String,
        isMandatory: Boolean
    ): Result<PaymentRequest> {
        return try {
            val requestData = PaymentRequestCreate(
                studentId = studentId,
                amount = amount,
                dueDate = dueDate,
                paymentType = paymentType,
                description = description,
                isMandatory = isMandatory
            )

            val response = paymentApiService.createPaymentRequest(requestData)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Failed to create payment request")
            }
        } catch (e: Exception) {
            Result.Error("Error creating payment request: ${e.message}")
        }
    }
}