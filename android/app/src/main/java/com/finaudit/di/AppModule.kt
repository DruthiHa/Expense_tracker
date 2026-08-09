package com.finaudit.di

import android.content.Context
import com.finaudit.data.local.FinAuditDatabase
import com.finaudit.data.repository.FinAuditRepositoryImpl
import com.finaudit.domain.repository.FinAuditRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinAuditDatabase {
        return FinAuditDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideRepository(db: FinAuditDatabase): FinAuditRepository {
        return FinAuditRepositoryImpl(db)
    }
}
