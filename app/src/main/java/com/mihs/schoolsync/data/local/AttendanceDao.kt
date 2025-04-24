package com.mihs.schoolsync.data.local

import androidx.room.*
import com.mihs.schoolsync.data.models.AttendanceRecord
import java.time.LocalDate

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AttendanceRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AttendanceRecord>)

    @Update
    suspend fun updateRecord(record: AttendanceRecord)

    @Delete
    suspend fun deleteRecord(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY date DESC")
    suspend fun getRecordsForStudent(studentId: Int): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getRecordsForStudentInRange(
        studentId: Int,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE date = :date ORDER BY studentId ASC")
    suspend fun getRecordsByDate(date: LocalDate): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE courseOfferingId = :courseId AND date = :date")
    suspend fun getCourseAttendanceByDate(courseId: Int, date: LocalDate): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE classSectionId = :classId AND date = :date")
    suspend fun getClassAttendanceByDate(classId: Int, date: LocalDate): List<AttendanceRecord>
}
