// ClassRepository.kt
package com.mihs.schoolsync.data.repository

import com.mihs.schoolsync.data.models.ClassSection
import com.mihs.schoolsync.data.remote.ClassApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassRepository @Inject constructor(
    private val classApiService: ClassApiService
) {

    suspend fun getClasses(): List<ClassSection> {
        return classApiService.getClasses()
    }

    suspend fun getClassSection(classSectionId: Int): ClassSection {
        return classApiService.getClassSection(classSectionId)
    }
}