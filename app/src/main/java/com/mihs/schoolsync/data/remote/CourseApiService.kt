// CourseApiService.kt
package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.data.models.CourseOffering
import retrofit2.http.GET
import retrofit2.http.Path

interface CourseApiService {

    @GET("courses")
    suspend fun getCourses(): List<CourseOffering>

    @GET("classes/{id}/courses")
    suspend fun getCoursesForClass(@Path("id") classSectionId: Int): List<CourseOffering>

    @GET("courses/{id}")
    suspend fun getCourseOffering(@Path("id") courseOfferingId: Int): CourseOffering
}