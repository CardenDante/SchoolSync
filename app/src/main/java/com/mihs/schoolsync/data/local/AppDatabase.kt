package com.mihs.schoolsync.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mihs.schoolsync.data.models.AttendanceRecord
import com.mihs.schoolsync.utils.Converters

@Database(
    entities = [AttendanceRecord::class], // Add other entities here
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // Define your DAOs here
    abstract fun attendanceDao(): AttendanceDao
}
