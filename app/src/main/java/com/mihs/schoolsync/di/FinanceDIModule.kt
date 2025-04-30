// FinanceDIModule.kt
package com.mihs.schoolsync.di

import android.content.Context
import com.mihs.schoolsync.data.remote.*
import com.mihs.schoolsync.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FinanceDIModule {

    // API Services
    @Provides
    @Singleton
    fun provideFeeStructureApiService(@AuthRetrofit retrofit: Retrofit): FeeStructureApiService {
        return retrofit.create(FeeStructureApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReceiptApiService(@AuthRetrofit retrofit: Retrofit): ReceiptApiService {
        return retrofit.create(ReceiptApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFinanceReportApiService(@AuthRetrofit retrofit: Retrofit): FinanceReportApiService {
        return retrofit.create(FinanceReportApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApiService(@AuthRetrofit retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

    // Repositories
    // Note: Removed provideFeeRepository since it already exists in FeeDIModule

    @Provides
    @Singleton
    fun provideFeeStructureRepository(feeStructureApiService: FeeStructureApiService): FeeStructureRepository {
        return FeeStructureRepositoryImpl(feeStructureApiService)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(paymentApiService: PaymentApiService, @ApplicationContext context: Context): PaymentRepository {
        return PaymentRepositoryImpl(paymentApiService, context)
    }

    @Provides
    @Singleton
    fun provideReceiptRepository(receiptApiService: ReceiptApiService, @ApplicationContext context: Context): ReceiptRepository {
        return ReceiptRepositoryImpl(receiptApiService, context)
    }

    @Provides
    @Singleton
    fun provideFinanceReportRepository(financeReportApiService: FinanceReportApiService): FinanceReportRepository {
        return FinanceReportRepositoryImpl(financeReportApiService)
    }
}