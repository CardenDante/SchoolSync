// PaymentRequestModels.kt - Models for payment requests
package com.mihs.schoolsync.ui.finance.models

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Model for payment request creation
 */
data class PaymentRequestCreate(
    @SerializedName("student_id")
    val studentId: Int,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("due_date")
    val dueDate: Date,

    @SerializedName("payment_type")
    val paymentType: FinancePaymentType,

    @SerializedName("description")
    val description: String,

    @SerializedName("is_mandatory")
    val isMandatory: Boolean
)

/**
 * Response model for payment request
 */
data class PaymentRequest(
    @SerializedName("id")
    val id: Int,

    @SerializedName("student_id")
    val studentId: Int,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("due_date")
    val dueDate: Date,

    @SerializedName("payment_type")
    val paymentType: FinancePaymentType,

    @SerializedName("description")
    val description: String,

    @SerializedName("is_mandatory")
    val isMandatory: Boolean,

    @SerializedName("is_paid")
    val isPaid: Boolean,

    @SerializedName("requested_by")
    val requestedBy: Int,

    @SerializedName("request_date")
    val requestDate: Date
)