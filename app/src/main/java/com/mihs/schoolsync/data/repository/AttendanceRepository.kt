    // AttendanceRepository.kt
    package com.mihs.schoolsync.data.repository

    import com.mihs.schoolsync.data.models.AttendanceRecord
    import com.mihs.schoolsync.data.remote.AttendanceRecordResponse
    import com.mihs.schoolsync.data.models.AttendanceStatus
    import com.mihs.schoolsync.data.remote.AttendanceApiService
    import com.mihs.schoolsync.data.remote.DailyAttendanceRequest
    import com.mihs.schoolsync.data.remote.CourseAttendanceRequest
    import com.mihs.schoolsync.data.remote.CambridgeSubjectAttendanceRequest
    import com.mihs.schoolsync.data.remote.BulkAttendanceRequest
    import com.mihs.schoolsync.data.remote.BulkCourseAttendanceRequest
    import com.mihs.schoolsync.data.remote.BulkCambridgeSubjectAttendanceRequest
    import com.mihs.schoolsync.data.remote.CheckInRequest
    import com.mihs.schoolsync.data.remote.QRCheckInRequest
    import com.mihs.schoolsync.data.remote.AttendanceReportParams
    import java.time.LocalDate
    import javax.inject.Inject
    import javax.inject.Singleton
    import java.time.LocalTime
    import java.time.LocalDateTime

    @Singleton
    class AttendanceRepository @Inject constructor(
        private val attendanceApiService: AttendanceApiService
    ) {
        // Daily Attendance
        suspend fun recordDailyAttendance(
            studentId: Int,
            date: LocalDate,
            status: AttendanceStatus,
            reason: String? = null,
            notes: String? = null
        ): AttendanceRecord {
            val response = attendanceApiService.recordDailyAttendance(
                DailyAttendanceRequest(
                    studentId = studentId,
                    date = date,
                    status = status.name.lowercase(),
                    reason = reason,
                    notes = notes
                )
            )
            return mapToAttendanceRecord(response)
        }

        suspend fun bulkRecordDailyAttendance(
            date: LocalDate,
            classSectionId: Int,
            records: List<Map<String, Any>>
        ): List<AttendanceRecord> {
            val response = attendanceApiService.bulkRecordDailyAttendance(
                BulkAttendanceRequest(
                    date = date,
                    classSectionId = classSectionId,
                    records = records
                )
            )
            return response.map { mapToAttendanceRecord(it) }
        }

        // Course Attendance
        suspend fun recordCourseAttendance(
            studentId: Int,
            date: LocalDate,
            status: AttendanceStatus,
            courseOfferingId: Int,
            reason: String? = null,
            notes: String? = null
        ): AttendanceRecord {
            val response = attendanceApiService.recordCourseAttendance(
                CourseAttendanceRequest(
                    studentId = studentId,
                    date = date,
                    status = status.name.lowercase(),
                    courseOfferingId = courseOfferingId,
                    reason = reason,
                    notes = notes
                )
            )
            return mapToAttendanceRecord(response)
        }

        // Cambridge Subject Attendance
        suspend fun recordCambridgeSubjectAttendance(
            studentId: Int,
            date: LocalDate,
            status: AttendanceStatus,
            cambridgeSubjectId: Int,
            reason: String? = null,
            notes: String? = null
        ): AttendanceRecord {
            val response = attendanceApiService.recordCambridgeSubjectAttendance(
                CambridgeSubjectAttendanceRequest(
                    studentId = studentId,
                    date = date,
                    status = status.name.lowercase(),
                    cambridgeSubjectId = cambridgeSubjectId,
                    reason = reason,
                    notes = notes
                )
            )
            return mapToAttendanceRecord(response)
        }

        // Check-In
        suspend fun recordCheckIn(
            studentId: Int,
            arrivalTime: LocalTime? = null
        ): AttendanceRecord {
            val response = attendanceApiService.recordCheckIn(
                CheckInRequest(
                    studentId = studentId,
                    arrivalTime = arrivalTime
                )
            )
            return mapToAttendanceRecord(response)
        }

        suspend fun processQrCheckIn(
            studentIdentifier: String
        ): AttendanceRecord {
            val response = attendanceApiService.processQrCheckIn(
                QRCheckInRequest(
                    studentIdentifier = studentIdentifier
                )
            )
            return mapToAttendanceRecord(response)
        }

        // Get Methods
        suspend fun getStudentAttendance(
            studentId: Int,
            startDate: LocalDate,
            endDate: LocalDate
        ): List<Map<String, Any>> {
            return attendanceApiService.getStudentAttendance(studentId, startDate, endDate)
        }

        suspend fun getClassAttendance(
            classSectionId: Int,
            date: LocalDate
        ): Map<String, Any> {
            return attendanceApiService.getClassAttendance(classSectionId, date)
        }

        suspend fun getTodayCheckIns(): List<Map<String, Any>> {
            return attendanceApiService.getTodayCheckIns()
        }

        suspend fun getAttendanceDashboard(
            classSectionId: Int? = null
        ): Map<String, Any> {
            return attendanceApiService.getAttendanceDashboard(classSectionId)
        }

        suspend fun generateAttendanceReport(
            startDate: LocalDate,
            endDate: LocalDate,
            studentId: Int? = null,
            classSectionId: Int? = null,
            courseOfferingId: Int? = null,
            cambridgeSubjectId: Int? = null,
            includeWeekends: Boolean = false
        ): Map<String, Any> {
            return attendanceApiService.generateAttendanceReport(
                AttendanceReportParams(
                    startDate = startDate,
                    endDate = endDate,
                    studentId = studentId,
                    classSectionId = classSectionId,
                    courseOfferingId = courseOfferingId,
                    cambridgeSubjectId = cambridgeSubjectId,
                    includeWeekends = includeWeekends
                )
            )
        }

        // Helper methods
        private fun mapToAttendanceRecord(response: AttendanceRecordResponse): AttendanceRecord {
            return AttendanceRecord(
                id = response.id,
                studentId = response.studentId,
                date = response.date,
                status = AttendanceStatus.valueOf(response.status.uppercase()),
                arrivalTime = response.arrivalTime,
                checkInTimestamp = response.checkInTimestamp,
                courseOfferingId = response.courseOfferingId,
                cambridgeSubjectId = response.cambridgeSubjectId,
                reason = response.reason,
                notes = response.notes,
                recordedBy = response.recordedBy,
                isVerified = response.isVerified,
                createdAt = response.createdAt,
                updatedAt = response.updatedAt
            )
        }
    }