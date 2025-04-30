// FeeStructureModels.kt
package com.mihs.schoolsync.ui.finance.models

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Models for fee structure management
 */
data class FeeStructureResponse(
    val id: Int,
    val name: String,
    @SerializedName("academic_year")
    val academicYear: String,
    @SerializedName("applicable_grade")
    val applicableGrade: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("effective_date")
    val effectiveDate: Date,
    @SerializedName("created_by")
    val createdBy: Int,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date?,
    val items: List<FeeItemResponse>
)

data class FeeItemResponse(
    val id: Int,
    @SerializedName("fee_structure_id")
    val feeStructureId: Int,
    @SerializedName("fee_type")
    val feeType: FinanceFeeType,
    val description: String?,
    val amount: Double,
    val term: String?,
    @SerializedName("is_recurring")
    val isRecurring: Boolean,
    @SerializedName("applies_to_new_students")
    val appliesToNewStudents: Boolean
)

/**
 * Request models for fee structure creation/update
 */
data class FeeStructureCreateRequest(
    val name: String,
    @SerializedName("academic_year")
    val academicYear: String,
    @SerializedName("applicable_grade")
    val applicableGrade: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("effective_date")
    val effectiveDate: Date
)

data class FeeStructureUpdateRequest(
    val name: String,
    @SerializedName("academic_year")
    val academicYear: String,
    @SerializedName("applicable_grade")
    val applicableGrade: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("effective_date")
    val effectiveDate: Date
)

data class FeeItemCreateRequest(
    @SerializedName("fee_type")
    val feeType: FinanceFeeType,
    val description: String?,
    val amount: Double,
    val term: String?,
    @SerializedName("is_recurring")
    val isRecurring: Boolean,
    @SerializedName("applies_to_new_students")
    val appliesToNewStudents: Boolean
)

/**
 * Combined request model for creating fee structure with items
 */
data class FeeStructureWithItemsCreate(
    val structure: FeeStructureCreateRequest,
    val items: List<FeeItemCreateRequest>
)