// ClassSection.kt
package com.mihs.schoolsync.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_sections")
data class ClassSection(
    @PrimaryKey
    val id: Int,
    val name: String,
    val classLevelId: Int,
    val academicYearId: Int,
    val academicTermId: Int,
    val isActive: Boolean = true
)