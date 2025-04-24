// CourseOffering.kt
package com.mihs.schoolsync.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_offerings")
data class CourseOffering(
    @PrimaryKey
    val id: Int,
    val offeringCode: String? = null,
    val subjectName: String? = null,
    val cambridgeSubjectId: Int? = null,
    val academicYearId: Int,
    val academicTermId: Int,
    val isActive: Boolean = true
)