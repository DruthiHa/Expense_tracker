package com.finaudit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.finaudit.data.local.dao.*
import com.finaudit.domain.model.*


@Database(
    entities = [
        Transaction::class,
        Subscription::class,
        Budget::class,
        SavingsGoal::class,
        Alert::class,
        MerchantMapping::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FinAuditDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun alertDao(): AlertDao
    abstract fun merchantMappingDao(): MerchantMappingDao

    companion object {
        @Volatile
        private var INSTANCE: FinAuditDatabase? = null

        // In a real application, retrieve this passphrase from Android Keystore securely
        private const val DB_PASSPHRASE = "secure_finaudit_passphrase_key_123"

        fun getDatabase(context: Context): FinAuditDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FinAuditDatabase::class.java,
                    "finaudit_standard.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
