package com.mihs.schoolsync.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

// Define qualifiers for the different instances
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthOkHttp

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://192.168.100.22:8000/api/v1/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // Step 1: Create a basic OkHttpClient without the AuthInterceptor
    @Provides
    @Singleton
    @BaseOkHttp
    fun provideBaseOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Step 2: Create a basic Retrofit instance using the base OkHttpClient
    @Provides
    @Singleton
    @BaseRetrofit
    fun provideBaseRetrofit(
        @BaseOkHttp okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Step 3: Create AuthApiService using the base Retrofit
    @Provides
    @Singleton
    fun provideAuthApiService(
        @BaseRetrofit retrofit: Retrofit
    ): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    // Step 4: Create AuthInterceptor with AuthApiService
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: com.mihs.schoolsync.utils.TokenManager,
        authApiService: AuthApiService
    ): AuthInterceptor {
        return AuthInterceptor(tokenManager, authApiService)
    }

    // Step 5: Create authenticated OkHttpClient with AuthInterceptor
    @Provides
    @Singleton
    @AuthOkHttp
    fun provideAuthenticatedOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Step 6: Create authenticated Retrofit
    @Provides
    @Singleton
    @AuthRetrofit
    fun provideAuthRetrofit(
        @AuthOkHttp okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Step 7: Create all other API services using authenticated Retrofit
    @Provides
    @Singleton
    fun provideUserApiService(
        @AuthRetrofit retrofit: Retrofit
    ): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStudentApiService(
        @AuthRetrofit retrofit: Retrofit
    ): StudentApiService {
        return retrofit.create(StudentApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideClassApiService(
        @AuthRetrofit retrofit: Retrofit
    ): ClassApiService {
        return retrofit.create(ClassApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCourseApiService(
        @AuthRetrofit retrofit: Retrofit
    ): CourseApiService {
        return retrofit.create(CourseApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAttendanceApiService(
        @AuthRetrofit retrofit: Retrofit
    ): AttendanceApiService {
        return retrofit.create(AttendanceApiService::class.java)
    }
}