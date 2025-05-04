package com.mihs.schoolsync.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import javax.net.SocketFactory
import kotlin.math.pow

// Keep existing qualifiers
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

/**
 * Custom socket factory to test if a host is reachable before trying to connect,
 * which helps detect non-responsive hosts early.
 */
class ConnectionCheckSocketFactory : SocketFactory() {
    private val delegate = SocketFactory.getDefault()

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        return delegate.createSocket()
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int): Socket {
        // Try to determine if the host is reachable before attempting to connect
        if (!isHostReachable(host)) {
            throw IOException("Host $host is unreachable")
        }
        return delegate.createSocket(host, port)
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket {
        if (!isHostReachable(host)) {
            throw IOException("Host $host is unreachable")
        }
        return delegate.createSocket(host, port, localHost, localPort)
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        if (!host.isReachable(2000)) { // 2 second timeout
            throw IOException("Host ${host.hostAddress} is unreachable")
        }
        return delegate.createSocket(host, port)
    }

    @Throws(IOException::class)
    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket {
        if (!address.isReachable(2000)) { // 2 second timeout
            throw IOException("Host ${address.hostAddress} is unreachable")
        }
        return delegate.createSocket(address, port, localAddress, localPort)
    }

    private fun isHostReachable(host: String): Boolean {
        return try {
            val inetAddress = InetAddress.getByName(host)
            inetAddress.isReachable(2000) // 2 second timeout
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * A custom interceptor for retrying failed requests due to network issues
 */
class RetryInterceptor(private val maxRetries: Int = 3) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request: Request = chain.request()
        var response: okhttp3.Response? = null
        var exception: IOException? = null

        var retryCount = 0
        var shouldRetry: Boolean

        do {
            shouldRetry = false
            try {
                // If there was a previous response that wasn't successful, close it
                response?.close()

                // Try the request
                response = chain.proceed(request)

                // If we get a server error (5xx), we should retry
                if (response.code in 500..599) {
                    shouldRetry = true
                    response.close() // Close the response before retry
                }
            } catch (e: IOException) {
                // Network related exceptions we'll retry
                exception = e
                shouldRetry = e is SocketTimeoutException ||
                        e.message?.contains("Host is unreachable") == true ||
                        e.message?.contains("Connection reset") == true ||
                        e.message?.contains("Socket closed") == true
            }

            // If we should retry, and we haven't exceeded max retries
            if (shouldRetry && retryCount < maxRetries) {
                retryCount++
                // Exponential backoff: wait longer between each retry
                val sleepMillis = (2.0.pow(retryCount.toDouble()) * 1000).toLong()
                Thread.sleep(sleepMillis)
            } else {
                // Either we shouldn't retry, or we've exceeded max retries
                shouldRetry = false
            }
        } while (shouldRetry)

        // If we never got a response, throw the last exception
        return response ?: throw exception ?: IOException("Unknown network error")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "http://192.168.100.22:8000/api/v1/"

    // Reducing timeout values to prevent long waits when server is unresponsive
    private const val CONNECT_TIMEOUT = 10L // seconds
    private const val READ_TIMEOUT = 10L // seconds
    private const val WRITE_TIMEOUT = 10L // seconds
    private const val MAX_RETRIES = 3

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideRetryInterceptor(): RetryInterceptor {
        return RetryInterceptor(MAX_RETRIES)
    }

    @Provides
    @Singleton
    fun provideConnectionPool(): ConnectionPool {
        return ConnectionPool(5, 30, TimeUnit.SECONDS)
    }

    // Step 1: Create a basic OkHttpClient without the AuthInterceptor
    @Provides
    @Singleton
    @BaseOkHttp
    fun provideBaseOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        retryInterceptor: RetryInterceptor,
        connectionPool: ConnectionPool
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(retryInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .connectionPool(connectionPool)
            .retryOnConnectionFailure(true)
            .socketFactory(ConnectionCheckSocketFactory())
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
        retryInterceptor: RetryInterceptor,
        authInterceptor: AuthInterceptor,
        connectionPool: ConnectionPool
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .connectionPool(connectionPool)
            .retryOnConnectionFailure(true)
            .socketFactory(ConnectionCheckSocketFactory())
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

    // NEW FEE MODULE API SERVICES
    @Provides
    @Singleton
    fun provideFeeApiService(
        @AuthRetrofit retrofit: Retrofit
    ): FeeApiService {
        return retrofit.create(FeeApiService::class.java)
    }
//
//    @Provides
//    @Singleton
//    fun providePaymentApiService(
//        @AuthRetrofit retrofit: Retrofit
//    ): PaymentApiService {
//        return retrofit.create(PaymentApiService::class.java)
//    }
}