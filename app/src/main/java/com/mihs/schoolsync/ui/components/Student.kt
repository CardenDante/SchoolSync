package com.mihs.schoolsync.ui.components

import com.mihs.schoolsync.data.models.StudentDetail

data class Student(
    val id: Int,
    val fullName: String,
    val studentId: String
)

// Extension function to convert from StudentDetail to Student
fun StudentDetail.toUiStudent(): Student {
    return Student(
        id = this.id,
        fullName = this.studentId, // Using studentId as fullName or combine with other fields
        studentId = this.studentId
    )
}

// Extension function to convert a list of StudentDetail to a list of Student
fun List<StudentDetail>.toUiStudents(): List<Student> {
    return this.map { it.toUiStudent() }
}