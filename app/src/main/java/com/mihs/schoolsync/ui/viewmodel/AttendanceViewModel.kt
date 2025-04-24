// AttendanceViewModel.kt
package com.mihs.schoolsync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mihs.schoolsync.data.models.AttendanceRecord
import com.mihs.schoolsync.data.models.AttendanceStatus
import com.mihs.schoolsync.data.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val attendanceRepository: AttendanceRepository
) : ViewModel() {
    // State flows for different screens
    private val _studentsAttendance = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val studentsAttendance: StateFlow<List<Map<String, Any>>> = _studentsAttendance.asStateFlow()

    private val _classAttendance = MutableStateFlow<Map<String, Any>>(emptyMap())
    val classAttendance: StateFlow<Map<String, Any>> = _classAttendance.asStateFlow()

    private val _todayCheckIns = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val todayCheckIns: StateFlow<List<Map<String, Any>>> = _todayCheckIns.asStateFlow()

    private val _attendanceDashboard = MutableStateFlow<Map<String, Any>>(emptyMap())
    val attendanceDashboard: StateFlow<Map<String, Any>> = _attendanceDashboard.asStateFlow()

    private val _attendanceReport = MutableStateFlow<Map<String, Any>>(emptyMap())
    val attendanceReport: StateFlow<Map<String, Any>> = _attendanceReport.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Daily Attendance
    fun recordDailyAttendance(
        studentId: Int,
        date: LocalDate,
        status: AttendanceStatus,
        reason: String? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                attendanceRepository.recordDailyAttendance(
                    studentId = studentId,
                    date = date,
                    status = status,
                    reason = reason,
                    notes = notes
                )

                // Refresh data as needed
                fetchStudentAttendance(studentId, date, date)

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
    // Add to AttendanceViewModel:
    fun recordBulkDailyAttendance(
        date: LocalDate,
        classSectionId: Int,
        records: List<Map<String, Any>>
    ) {
        bulkRecordDailyAttendance(date, classSectionId, records)
    }

    fun recordBulkCourseAttendance(
        date: LocalDate,
        classSectionId: Int,
        courseOfferingId: Int,
        records: List<Map<String, Any>>
    ) {
        // Implement or adapt to existing functions
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                // Call repository method or adapt to existing methods
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun bulkRecordDailyAttendance(
        date: LocalDate,
        classSectionId: Int,
        records: List<Map<String, Any>>
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                attendanceRepository.bulkRecordDailyAttendance(
                    date = date,
                    classSectionId = classSectionId,
                    records = records
                )

                // Refresh class attendance data
                fetchClassAttendance(classSectionId, date)

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Course Attendance
    fun recordCourseAttendance(
        studentId: Int,
        date: LocalDate,
        status: AttendanceStatus,
        courseOfferingId: Int,
        reason: String? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                attendanceRepository.recordCourseAttendance(
                    studentId = studentId,
                    date = date,
                    status = status,
                    courseOfferingId = courseOfferingId,
                    reason = reason,
                    notes = notes
                )

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Cambridge Subject Attendance
    fun recordCambridgeSubjectAttendance(
        studentId: Int,
        date: LocalDate,
        status: AttendanceStatus,
        cambridgeSubjectId: Int,
        reason: String? = null,
        notes: String? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                attendanceRepository.recordCambridgeSubjectAttendance(
                    studentId = studentId,
                    date = date,
                    status = status,
                    cambridgeSubjectId = cambridgeSubjectId,
                    reason = reason,
                    notes = notes
                )

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Check-In
    fun recordCheckIn(
        studentId: Int,
        arrivalTime: LocalTime? = null
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                attendanceRepository.recordCheckIn(
                    studentId = studentId,
                    arrivalTime = arrivalTime
                )

                // Refresh today's check-ins
                fetchTodayCheckIns()

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun processQrCheckIn(studentIdentifier: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                attendanceRepository.processQrCheckIn(studentIdentifier)

                // Refresh today's check-ins
                fetchTodayCheckIns()

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Fetch data methods
    fun fetchStudentAttendance(
        studentId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = attendanceRepository.getStudentAttendance(
                    studentId = studentId,
                    startDate = startDate,
                    endDate = endDate
                )

                _studentsAttendance.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchClassAttendance(
        classSectionId: Int,
        date: LocalDate
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = attendanceRepository.getClassAttendance(
                    classSectionId = classSectionId,
                    date = date
                )

                _classAttendance.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchTodayCheckIns() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = attendanceRepository.getTodayCheckIns()

                _todayCheckIns.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchAttendanceDashboard(classSectionId: Int? = null) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = attendanceRepository.getAttendanceDashboard(
                    classSectionId = classSectionId
                )

                _attendanceDashboard.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun generateAttendanceReport(
        startDate: LocalDate,
        endDate: LocalDate,
        studentId: Int? = null,
        classSectionId: Int? = null,
        courseOfferingId: Int? = null,
        cambridgeSubjectId: Int? = null,
        includeWeekends: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null

                val result = attendanceRepository.generateAttendanceReport(
                    startDate = startDate,
                    endDate = endDate,
                    studentId = studentId,
                    classSectionId = classSectionId,
                    courseOfferingId = courseOfferingId,
                    cambridgeSubjectId = cambridgeSubjectId,
                    includeWeekends = includeWeekends
                )

                _attendanceReport.value = result

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}