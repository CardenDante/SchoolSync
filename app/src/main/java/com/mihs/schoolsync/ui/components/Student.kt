package com.mihs.schoolsync.ui.components

data class Student(
    val id: Int,
    val fullName: String,
    val studentId: String,
    val email: String? = null,
    val phoneNumber: String? = null
)