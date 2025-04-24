package com.mihs.schoolsync.utils

object InputValidator {
    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$")
        return email.matches(emailRegex)
    }

    fun isValidPassword(password: String): Boolean {
        // At least 8 characters, one uppercase, one lowercase, one number
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")
        return password.matches(passwordRegex)
    }

    fun isValidUsername(username: String): Boolean {
        return username.length >= 3 && username.length <= 20
    }
}