// CourseRepository.kt
package com.mihs.schoolsync.data.repository

import com.mihs.schoolsync.data.models.CourseOffering
import com.mihs.schoolsync.data.remote.CourseApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val courseApiService: CourseApiService
) {

    suspend fun getCourses(): List<CourseOffering> {
        return courseApiService.getCourses()
    }

    suspend fun getCoursesForClass(classSectionId: Int): List<CourseOffering> {
        return courseApiService.getCoursesForClass(classSectionId)
    }

    suspend fun getCourseOffering(courseOfferingId: Int): CourseOffering {
        return courseApiService.getCourseOffering(courseOfferingId)
    }
}