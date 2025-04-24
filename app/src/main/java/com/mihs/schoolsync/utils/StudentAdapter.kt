// Create a StudentAdapter.kt utility file
package com.mihs.schoolsync.utils

import com.mihs.schoolsync.data.models.Student
import com.mihs.schoolsync.data.models.StudentDetail

// Extension properties to help use Student/StudentDetail in UI
val Student.fullName: String
    get() = this.studentId // Or use a combination of fields if available

val StudentDetail.fullName: String
    get() = this.studentId // Or use a combination of fields if available