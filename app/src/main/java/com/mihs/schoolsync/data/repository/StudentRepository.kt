package com.mihs.schoolsync.data.repository

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mihs.schoolsync.data.models.*
import com.mihs.schoolsync.data.remote.StudentApiService
import com.mihs.schoolsync.utils.Result
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class StudentRepository @Inject constructor(
    private val studentApiService: StudentApiService
) {
    companion object {
        private const val TAG = "StudentRepository"
    }

    suspend fun createStudent(studentCreateRequest: StudentCreateRequest): Result<Student> {
        return try {
            Log.d(TAG, "Creating student: ${studentCreateRequest.studentId}")
            val response = studentApiService.createStudent(studentCreateRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student created successfully")
                    Result.Success(it)
                } ?: Result.Error("Empty response when creating student")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error creating student: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when creating student", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when creating student", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when creating student", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getStudents(
        studentId: String? = null,
        status: String? = null,
        isActive: Boolean? = null,
        admissionDateStart: String? = null,
        admissionDateEnd: String? = null,
        classSectionId: Int? = null,
        page: Int = 1,
        size: Int = 10
    ): Result<StudentListResponse> {
        return try {
            Log.d(TAG, "Fetching students, page: $page, size: $size")
            val response = studentApiService.getStudents(
                studentId, status, isActive,
                admissionDateStart, admissionDateEnd,
                classSectionId, page, size
            )

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Students fetched successfully: ${it.items.size} items")
                    Result.Success(it)
                } ?: Result.Error("Empty response when fetching students")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching students: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching students", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching students", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching students", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getStudent(studentId: Int): Result<StudentDetail> {
        return try {
            Log.d(TAG, "Fetching student with ID: $studentId")
            val response = studentApiService.getStudent(studentId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student fetched successfully")
                    Result.Success(it)
                } ?: Result.Error("Empty response when fetching student")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching student: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching student", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching student", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching student", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getStudentByStudentId(studentIdStr: String): Result<StudentDetail> {
        return try {
            Log.d(TAG, "Fetching student with student ID: $studentIdStr")
            val response = studentApiService.getStudentByStudentId(studentIdStr)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student fetched successfully")
                    Result.Success(it)
                } ?: Result.Error("Empty response when fetching student")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching student: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching student", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching student", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching student", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun updateStudent(studentId: Int, studentUpdateRequest: StudentUpdateRequest): Result<StudentDetail> {
        return try {
            Log.d(TAG, "Updating student with ID: $studentId")
            val response = studentApiService.updateStudent(studentId, studentUpdateRequest)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student updated successfully")
                    Result.Success(it)
                } ?: Result.Error("Empty response when updating student")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error updating student: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when updating student", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when updating student", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when updating student", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun updateStudentStatus(studentId: Int, statusUpdate: StudentStatusUpdateRequest): Result<StudentDetail> {
        return try {
            Log.d(TAG, "Updating status for student with ID: $studentId")
            val response = studentApiService.updateStudentStatus(studentId, statusUpdate)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student status updated successfully")
                    Result.Success(it)
                } ?: Result.Error("Empty response when updating student status")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error updating student status: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when updating student status", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when updating student status", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when updating student status", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getStudentAcademicSummary(studentId: Int): Result<StudentAcademicSummary> {
        return try {
            Log.d(TAG, "Fetching academic summary for student with ID: $studentId")
            val response = studentApiService.getStudentAcademicSummary(studentId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student academic summary fetched successfully")
                    Result.Success(it)
                } ?: Result.Error("Empty response when fetching student academic summary")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching student academic summary: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching student academic summary", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching student academic summary", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching student academic summary", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getStudentFinancialSummary(studentId: Int): Result<StudentFinancialSummary> {
        return try {
            Log.d(TAG, "Fetching financial summary for student with ID: $studentId")
            val response = studentApiService.getStudentFinancialSummary(studentId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student financial summary fetched successfully")
                    Result.Success(it)
                } ?: Result.Error("Empty response when fetching student financial summary")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching student financial summary: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching student financial summary", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching student financial summary", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching student financial summary", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getStudentDocuments(studentId: Int): Result<List<StudentDocument>> {
        return try {
            Log.d(TAG, "Fetching documents for student with ID: $studentId")
            val response = studentApiService.getStudentDocuments(studentId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Student documents fetched successfully: ${it.size} documents")
                    Result.Success(it)
                } ?: Result.Error("Empty response when fetching student documents")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching student documents: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching student documents", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching student documents", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching student documents", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getStudentsByClassSection(classSectionId: Int): Result<List<StudentDetail>> {
        return try {
            Log.d(TAG, "Fetching students for class section with ID: $classSectionId")
            val response = studentApiService.getStudentsByClassSection(classSectionId)

            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d(TAG, "Students fetched successfully: ${it.size} students")
                    Result.Success(it)
                } ?: Result.Error("Empty response when fetching students by class section")
            } else {
                val errorMessage = parseErrorMessage(response.errorBody()?.string(), response.code())
                Log.e(TAG, "Error fetching students by class section: $errorMessage")
                Result.Error(errorMessage)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network error when fetching students by class section", e)
            Result.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error when fetching students by class section", e)
            Result.Error("Server error: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error when fetching students by class section", e)
            Result.Error("An unexpected error occurred: ${e.message}")
        }
    }

    // Helper method for parsing error messages
    private fun parseErrorMessage(errorBody: String?, statusCode: Int): String {
        if (errorBody.isNullOrEmpty()) {
            return "Error: HTTP $statusCode"
        }

        return try {
            val errorJson = Gson().fromJson(errorBody, JsonObject::class.java)
            when {
                errorJson.has("detail") -> errorJson.get("detail").asString
                errorJson.has("message") -> errorJson.get("message").asString
                else -> "Error: HTTP $statusCode"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing error response", e)
            "Error: HTTP $statusCode"
        }
    }
}