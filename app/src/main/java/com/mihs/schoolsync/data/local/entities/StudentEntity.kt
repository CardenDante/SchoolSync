// StudentEntity.kt
package com.mihs.schoolsync.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mihs.schoolsync.data.models.StudentStatus

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: Int,
    val studentId: String,
    val applicationId: Int,
    val status: StudentStatus,
    val isActive: Boolean,
    val admissionDate: String,
    val lastUpdated: String,
    val createdAt: String
)