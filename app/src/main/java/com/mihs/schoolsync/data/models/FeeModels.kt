// FeeModels.kt
package com.mihs.schoolsync.ui.finance.models

import com.google.gson.annotations.SerializedName
import java.util.Date

// Enum classes for finance module
enum class FinanceTransactionType {
    @SerializedName("CHARGE")
    CHARGE,
    @SerializedName("PAYMENT")
    PAYMENT,
    @SerializedName("WAIVER")
    WAIVER,
    @SerializedName("ADJUSTMENT")
    ADJUSTMENT
}

enum class FinanceFeeType {
    @SerializedName("TUITION")
    TUITION,
    @SerializedName("ADMISSION")
    ADMISSION,
    @SerializedName("EXAM")
    EXAM,
    @SerializedName("ACTIVITY")
    ACTIVITY,
    @SerializedName("DEVELOPMENT")
    DEVELOPMENT,
    @SerializedName("LIBRARY")
    LIBRARY,
    @SerializedName("TRANSPORT")
    TRANSPORT,
    @SerializedName("UNIFORM")
    UNIFORM,
    @SerializedName("BOOKS")
    BOOKS,
    @SerializedName("OTHER")
    OTHER
}

enum class FinancePaymentStatus {
    @SerializedName("PENDING")
    PENDING,
    @SerializedName("VERIFIED")
    VERIFIED,
    @SerializedName("REJECTED")
    REJECTED,
    @SerializedName("PARTIALLY_VERIFIED")
    PARTIALLY_VERIFIED
}

enum class FinancePaymentType {
    @SerializedName("BANK_TRANSFER")
    BANK_TRANSFER,
    @SerializedName("MOBILE_MONEY")
    MOBILE_MONEY,
    @SerializedName("CASH")
    CASH,
    @SerializedName("CHECK")
    CHECK,
    @SerializedName("ONLINE_PAYMENT")
    ONLINE_PAYMENT
}

// Response classes
data class FeeStatementResponse(
    @SerializedName("student_id")
    val studentId: Int,
    @SerializedName("current_balance")
    val currentBalance: Double,
    @SerializedName("last_update")
    val lastUpdate: Date,
    val transactions: List<FeeTransactionResponse>
)

data class FeeTransactionResponse(
    val id: Int,
    @SerializedName("account_id")
    val accountId: Int,
    val amount: Double,
    @SerializedName("transaction_type")
    val transactionType: FinanceTransactionType,
    @SerializedName("fee_type")
    val feeType: FinanceFeeType? = null,
    val description: String? = null,
    @SerializedName("transaction_date")
    val transactionDate: Date,
    @SerializedName("academic_term")
    val academicTerm: String? = null,
    @SerializedName("recorded_by")
    val recordedBy: Int,
    @SerializedName("payment_id")
    val paymentId: Int? = null
)

data class PaymentCreateRequest(
    @SerializedName("student_id")
    val studentId: Int,
    val amount: Double,
    @SerializedName("bank_reference")
    val bankReference: String,
    @SerializedName("payment_date")
    val paymentDate: Date,
    @SerializedName("payment_type")
    val paymentType: FinancePaymentType,
    @SerializedName("parent_notes")
    val parentNotes: String? = null
)

data class PaymentResponse(
    val id: Int,
    @SerializedName("student_id")
    val studentId: Int,
    val amount: Double,
    @SerializedName("bank_reference")
    val bankReference: String,
    @SerializedName("payment_date")
    val paymentDate: Date,
    @SerializedName("submission_date")
    val submissionDate: Date,
    @SerializedName("verification_date")
    val verificationDate: Date? = null,
    @SerializedName("payment_type")
    val paymentType: FinancePaymentType,
    val status: FinancePaymentStatus,
    @SerializedName("bank_slip_url")
    val bankSlipUrl: String? = null,
    @SerializedName("parent_notes")
    val parentNotes: String? = null,
    @SerializedName("admin_notes")
    val adminNotes: String? = null,
    @SerializedName("verified_by")
    val verifiedBy: Int? = null
)