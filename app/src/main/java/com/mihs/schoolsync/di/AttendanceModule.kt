package com.mihs.schoolsync.di

import com.mihs.schoolsync.data.remote.AttendanceApiService
import com.mihs.schoolsync.data.repository.AttendanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AttendanceModule {
    @Provides
    @Singleton
    fun provideAttendanceRepository(
        attendanceApiService: AttendanceApiService
    ): AttendanceRepository {
        return AttendanceRepository(attendanceApiService)
    }
}