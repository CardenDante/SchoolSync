// ReceiptModels.kt
package com.mihs.schoolsync.ui.finance.models

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Response model for receipt details
 */
data class ReceiptResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("receipt_number")
    val receiptNumber: String,

    @SerializedName("payment_id")
    val paymentId: Int,

    @SerializedName("student_id")
    val studentId: Int,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("balance")
    val balance: Double,

    @SerializedName("generated_at")
    val date: Date,

    @SerializedName("generated_by")
    val generatedBy: Int,

    @SerializedName("pdf_url")
    val pdfUrl: String?,

    // Nested objects for the receipt display
    @SerializedName("student")
    val student: StudentReceiptInfo,

    @SerializedName("payment")
    val payment: PaymentReceiptInfo,

    @SerializedName("verified_by")
    val verifiedBy: String,

    @SerializedName("school_info")
    val schoolInfo: SchoolInfo
)

/**
 * Student information for receipt
 */
data class StudentReceiptInfo(
    @SerializedName("name")
    val name: String,

    @SerializedName("admin_number")
    val adminNumber: String
)

/**
 * Payment information for receipt
 */
data class PaymentReceiptInfo(
    @SerializedName("amount")
    val amount: Double,

    @SerializedName("amount_in_words")
    val amountInWords: String,

    @SerializedName("method")
    val method: String,

    @SerializedName("reference")
    val reference: String,

    @SerializedName("date")
    val date: Date
)

/**
 * School information for receipt
 */
data class SchoolInfo(
    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("registration")
    val registration: String,

    @SerializedName("address")
    val address: String
)