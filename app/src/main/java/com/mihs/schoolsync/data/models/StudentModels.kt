package com.mihs.schoolsync.data.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.ZonedDateTime

enum class StudentStatus {
    PENDING, ACTIVE, GRADUATED, WITHDRAWN, SUSPENDED, EXPELLED, TRANSFERRED, DECEASED
}

enum class DocumentType {
    BIRTH_CERTIFICATE, PASSPORT, NATIONAL_ID, SCHOOL_REPORT, MEDICAL_RECORD,
    VACCINATION_RECORD, GUARDIAN_ID, TRANSFER_LETTER, ADMISSION_LETTER, OTHER
}

enum class DocumentStatus {
    PENDING, VERIFIED, REJECTED, EXPIRED
}

data class Student(
    val id: Int,
    @SerializedName("student_id")
    val studentId: String,
    @SerializedName("application_id")
    val applicationId: Int,
    val status: StudentStatus,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("admission_date")
    val admissionDate: String,
    @SerializedName("last_updated")
    val lastUpdated: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class StudentDetail(
    val id: Int,
    @SerializedName("student_id")
    val studentId: String,
    @SerializedName("application_id")
    val applicationId: Int,
    val status: StudentStatus,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("admission_date")
    val admissionDate: String,
    @SerializedName("last_updated")
    val lastUpdated: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("current_class")
    val currentClass: String?,
    @SerializedName("current_academic_year")
    val currentAcademicYear: String?,
    @SerializedName("fee_balance")
    val feeBalance: Double?,
    @SerializedName("document_count")
    val documentCount: Int?
)

data class StudentListResponse(
    val items: List<StudentDetail>,
    val total: Int,
    val page: Int,
    val size: Int,
    val pages: Int
)

data class StudentCreateRequest(
    @SerializedName("student_id")
    val studentId: String,
    @SerializedName("application_id")
    val applicationId: Int,
    val status: StudentStatus? = StudentStatus.PENDING,
    @SerializedName("is_active")
    val isActive: Boolean? = true,
    @SerializedName("admission_date")
    val admissionDate: String? = null
)

data class StudentUpdateRequest(
    @SerializedName("student_id")
    val studentId: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean? = null
)

data class StudentStatusUpdateRequest(
    val status: StudentStatus,
    val reason: String? = null
)

data class StudentDocument(
    val id: Int,
    @SerializedName("owner_id")
    val ownerId: Int,
    @SerializedName("owner_type")
    val ownerType: String,
    @SerializedName("document_type")
    val documentType: DocumentType,
    @SerializedName("file_path")
    val filePath: String,
    @SerializedName("file_name")
    val fileName: String,
    val description: String?,
    val status: DocumentStatus,
    @SerializedName("expires_at")
    val expiresAt: String?,
    val metadata: Map<String, Any>?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)

data class StudentDocumentCreateRequest(
    @SerializedName("document_type")
    val documentType: DocumentType,
    @SerializedName("file_path")
    val filePath: String,
    @SerializedName("file_name")
    val fileName: String,
    val description: String? = null,
    @SerializedName("expires_at")
    val expiresAt: String? = null,
    val metadata: Map<String, Any>? = null,
    val status: DocumentStatus? = DocumentStatus.PENDING
)

data class StudentAcademicSummary(
    @SerializedName("current_enrollment")
    val currentEnrollment: StudentEnrollmentInfo?,
    @SerializedName("enrollment_history")
    val enrollmentHistory: List<StudentEnrollmentInfo>,
    @SerializedName("attendance_rate")
    val attendanceRate: Double?,
    @SerializedName("term_attendance_rate")
    val termAttendanceRate: Double?,
    @SerializedName("academic_progress")
    val academicProgress: List<Map<String, Any>>
)

data class StudentEnrollmentInfo(
    @SerializedName("enrollment_id")
    val enrollmentId: Int,
    @SerializedName("class_section_id")
    val classSectionId: Int,
    @SerializedName("class_section_name")
    val classSectionName: String,
    @SerializedName("class_level_id")
    val classLevelId: Int,
    @SerializedName("class_level_name")
    val classLevelName: String,
    @SerializedName("academic_year_id")
    val academicYearId: Int,
    @SerializedName("academic_year_name")
    val academicYearName: String,
    val status: String,
    @SerializedName("enrollment_date")
    val enrollmentDate: String
)

data class StudentFinancialSummary(
    @SerializedName("has_account")
    val hasAccount: Boolean,
    @SerializedName("current_balance")
    val currentBalance: Double,
    @SerializedName("total_paid")
    val totalPaid: Double,
    @SerializedName("total_charged")
    val totalCharged: Double,
    @SerializedName("recent_transactions")
    val recentTransactions: List<Map<String, Any>>
)