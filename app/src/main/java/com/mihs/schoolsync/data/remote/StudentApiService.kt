package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface StudentApiService {
    @POST("students")
    suspend fun createStudent(@Body student: StudentCreateRequest): Response<Student>

    @GET("students")
    suspend fun getStudents(
        @Query("student_id") studentId: String? = null,
        @Query("status") status: String? = null,
        @Query("is_active") isActive: Boolean? = null,
        @Query("admission_date_start") admissionDateStart: String? = null,
        @Query("admission_date_end") admissionDateEnd: String? = null,
        @Query("class_section_id") classSectionId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<StudentListResponse>

    @GET("students/{studentId}")
    suspend fun getStudent(@Path("studentId") studentId: Int): Response<StudentDetail>

    @GET("students/by-student-id/{studentIdStr}")
    suspend fun getStudentByStudentId(@Path("studentIdStr") studentIdStr: String): Response<StudentDetail>

    @PUT("students/{studentId}")
    suspend fun updateStudent(
        @Path("studentId") studentId: Int,
        @Body student: StudentUpdateRequest
    ): Response<StudentDetail>

    @PUT("students/{studentId}/status")
    suspend fun updateStudentStatus(
        @Path("studentId") studentId: Int,
        @Body statusUpdate: StudentStatusUpdateRequest
    ): Response<StudentDetail>

    @DELETE("students/{studentId}")
    suspend fun deleteStudent(@Path("studentId") studentId: Int): Response<Student>

    // Student detailed information endpoints
    @GET("students/{studentId}/details")
    suspend fun getStudentDetails(@Path("studentId") studentId: Int): Response<Map<String, Any>>

    @GET("students/{studentId}/academic")
    suspend fun getStudentAcademicSummary(@Path("studentId") studentId: Int): Response<StudentAcademicSummary>

    @GET("students/{studentId}/financial")
    suspend fun getStudentFinancialSummary(@Path("studentId") studentId: Int): Response<StudentFinancialSummary>

    // Student document endpoints
    @POST("students/{studentId}/documents")
    suspend fun addStudentDocument(
        @Path("studentId") studentId: Int,
        @Body document: StudentDocumentCreateRequest
    ): Response<StudentDocument>

    @GET("students/{studentId}/documents")
    suspend fun getStudentDocuments(@Path("studentId") studentId: Int): Response<List<StudentDocument>>

    // Class-based student endpoints
    @GET("students/class-section/{classSectionId}")
    suspend fun getStudentsByClassSection(@Path("classSectionId") classSectionId: Int): Response<List<StudentDetail>>
}