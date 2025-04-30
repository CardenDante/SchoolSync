// PaymentModels.kt - Additional models for payment verification
package com.mihs.schoolsync.ui.finance.models

import com.google.gson.annotations.SerializedName

/**
 * Request model for verifying a payment
 */
data class PaymentVerifyRequest(
    @SerializedName("status")
    val status: FinancePaymentStatus,

    @SerializedName("verified_amount")
    val verifiedAmount: Double,

    @SerializedName("admin_notes")
    val adminNotes: String? = null
)