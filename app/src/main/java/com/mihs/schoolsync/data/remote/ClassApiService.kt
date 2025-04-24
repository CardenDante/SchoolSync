// ClassApiService.kt
package com.mihs.schoolsync.data.remote

import com.mihs.schoolsync.data.models.ClassSection
import retrofit2.http.GET
import retrofit2.http.Path

interface ClassApiService {

    @GET("classes")
    suspend fun getClasses(): List<ClassSection>

    @GET("classes/{id}")
    suspend fun getClassSection(@Path("id") classSectionId: Int): ClassSection
}