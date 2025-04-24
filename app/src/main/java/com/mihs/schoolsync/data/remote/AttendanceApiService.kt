// AttendanceApiService.kt
package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.data.models.AttendanceRecord
import retrofit2.http.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

interface AttendanceApiService {
    // Daily Attendance
    @POST("attendance/daily")
    suspend fun recordDailyAttendance(@Body data: DailyAttendanceRequest): AttendanceRecordResponse

    @POST("attendance/daily/bulk")
    suspend fun bulkRecordDailyAttendance(@Body data: BulkAttendanceRequest): List<AttendanceRecordResponse>

    // Course Attendance
    @POST("attendance/course")
    suspend fun recordCourseAttendance(@Body data: CourseAttendanceRequest): AttendanceRecordResponse

    @POST("attendance/course/bulk")
    suspend fun bulkRecordCourseAttendance(@Body data: BulkCourseAttendanceRequest): List<AttendanceRecordResponse>

    // Cambridge Subject Attendance
    @POST("attendance/cambridge-subject")
    suspend fun recordCambridgeSubjectAttendance(@Body data: CambridgeSubjectAttendanceRequest): AttendanceRecordResponse

    @POST("attendance/cambridge-subject/bulk")
    suspend fun bulkRecordCambridgeSubjectAttendance(@Body data: BulkCambridgeSubjectAttendanceRequest): List<AttendanceRecordResponse>

    // Check-In
    @POST("attendance/check-in")
    suspend fun recordCheckIn(@Body data: CheckInRequest): AttendanceRecordResponse

    @POST("attendance/check-in/qr")
    suspend fun processQrCheckIn(@Body data: QRCheckInRequest): AttendanceRecordResponse

    // Get Methods
    @GET("attendance/student/{studentId}")
    suspend fun getStudentAttendance(
        @Path("studentId") studentId: Int,
        @Query("start_date") startDate: LocalDate,
        @Query("end_date") endDate: LocalDate
    ): List<Map<String, Any>>

    @GET("attendance/class/{classSectionId}/date/{date}")
    suspend fun getClassAttendance(
        @Path("classSectionId") classSectionId: Int,
        @Path("date") date: LocalDate
    ): Map<String, Any>

    @GET("attendance/check-in/today")
    suspend fun getTodayCheckIns(): List<Map<String, Any>>

    @GET("attendance/dashboard")
    suspend fun getAttendanceDashboard(
        @Query("class_section_id") classSectionId: Int?
    ): Map<String, Any>

    @POST("attendance/reports")
    suspend fun generateAttendanceReport(@Body params: AttendanceReportParams): Map<String, Any>
}

// DTOs
data class DailyAttendanceRequest(
    val studentId: Int,
    val date: LocalDate,
    val status: String,
    val reason: String? = null,
    val notes: String? = null
)

data class CourseAttendanceRequest(
    val studentId: Int,
    val date: LocalDate,
    val status: String,
    val reason: String? = null,
    val notes: String? = null,
    val courseOfferingId: Int
)

data class CambridgeSubjectAttendanceRequest(
    val studentId: Int,
    val date: LocalDate,
    val status: String,
    val reason: String? = null,
    val notes: String? = null,
    val cambridgeSubjectId: Int
)

data class BulkAttendanceRequest(
    val date: LocalDate,
    val classSectionId: Int,
    val records: List<Map<String, Any>>
)

data class BulkCourseAttendanceRequest(
    val date: LocalDate,
    val classSectionId: Int,
    val courseOfferingId: Int,
    val records: List<Map<String, Any>>
)

data class BulkCambridgeSubjectAttendanceRequest(
    val date: LocalDate,
    val classSectionId: Int,
    val cambridgeSubjectId: Int,
    val records: List<Map<String, Any>>
)

data class CheckInRequest(
    val studentId: Int,
    val arrivalTime: LocalTime? = null
)

data class QRCheckInRequest(
    val studentIdentifier: String
)

data class AttendanceRecordResponse(
    val id: Int,
    val studentId: Int,
    val date: LocalDate,
    val status: String,
    val arrivalTime: LocalTime? = null,
    val checkInTimestamp: LocalDateTime? = null,
    val courseOfferingId: Int? = null,
    val cambridgeSubjectId: Int? = null,
    val reason: String? = null,
    val notes: String? = null,
    val isVerified: Boolean = false,
    val recordedBy: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class AttendanceReportParams(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val studentId: Int? = null,
    val classSectionId: Int? = null,
    val courseOfferingId: Int? = null,
    val cambridgeSubjectId: Int? = null,
    val includeWeekends: Boolean = false
)
