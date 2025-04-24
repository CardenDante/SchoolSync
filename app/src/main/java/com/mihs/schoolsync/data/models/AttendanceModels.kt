// AttendanceModels.kt
package com.mihs.schoolsync.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE, EXCUSED, CHECK_IN
}

@Entity(
    tableName = "attendance_records",
    indices = [
        Index("studentId"),
        Index("date"),
        Index("courseOfferingId"),
        Index("cambridgeSubjectId")
    ]
    // Removed the foreign key constraint
)
data class AttendanceRecord(
    @PrimaryKey val id: Int = 0,
    val studentId: Int,
    val date: LocalDate,
    val status: AttendanceStatus,
    val arrivalTime: LocalTime? = null,
    val checkInTimestamp: LocalDateTime? = null,
    val courseOfferingId: Int? = null,
    val cambridgeSubjectId: Int? = null,
    val classSectionId: Int? = null,
    val reason: String? = null,
    val notes: String? = null,
    val recordedBy: Int,
    val isVerified: Boolean = false,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class AttendanceStatistics(
    val id: Int = 0,
    val classSectionId: Int,
    val academicTermId: Int,
    val date: LocalDate,
    val totalStudents: Int = 0,
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val lateCount: Int = 0,
    val excusedCount: Int = 0,
    val checkInCount: Int = 0,
    val attendanceRate: Float = 0f,
    val punctualityRate: Float = 0f
)