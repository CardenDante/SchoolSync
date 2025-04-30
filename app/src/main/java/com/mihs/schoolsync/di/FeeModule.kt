// FeeDIModule.kt
package com.mihs.schoolsync.di

import com.mihs.schoolsync.data.remote.AuthRetrofit
import com.mihs.schoolsync.data.remote.FeeApiService
import com.mihs.schoolsync.data.repository.FeeRepository
import com.mihs.schoolsync.data.repository.FeeRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeeDIModule {
    
    @Provides
    @Singleton
    fun provideFeeRepository(feeApiService: FeeApiService): FeeRepository {
        return FeeRepositoryImpl(feeApiService)
    }
}