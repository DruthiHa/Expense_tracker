package com.finaudit.data.local.dao

import androidx.room.*
import com.finaudit.domain.model.Transaction
import com.finaudit.domain.model.Subscription
import com.finaudit.domain.model.Budget
import com.finaudit.domain.model.SavingsGoal
import com.finaudit.domain.model.Alert
import com.finaudit.domain.model.MerchantMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM transactions WHERE isReviewed = 0 ORDER BY timestamp DESC")
    fun getPendingReviewTransactionsFlow(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
}

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY renewDate ASC")
    fun getAllSubscriptionsFlow(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions ORDER BY renewDate ASC")
    suspend fun getAllSubscriptions(): List<Subscription>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Delete
    suspend fun deleteSubscription(subscription: Subscription)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAll()
}

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE month = :month")
    fun getBudgetsForMonthFlow(month: String): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE month = :month")
    suspend fun getBudgetsForMonth(month: String): List<Budget>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget): Long

    @Update
    suspend fun updateBudget(budget: Budget)

    @Query("DELETE FROM budgets")
    suspend fun deleteAll()
}

@Dao
interface SavingsGoalDao {
    @Query("SELECT * FROM savings_goals")
    fun getAllGoalsFlow(): Flow<List<SavingsGoal>>

    @Query("SELECT * FROM savings_goals")
    suspend fun getAllGoals(): List<SavingsGoal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingsGoal): Long

    @Update
    suspend fun updateGoal(goal: SavingsGoal)

    @Delete
    suspend fun deleteGoal(goal: SavingsGoal)

    @Query("DELETE FROM savings_goals")
    suspend fun deleteAll()
}

@Dao
interface AlertDao {
    @Query("SELECT * FROM alerts WHERE isDismissed = 0 ORDER BY createdAt DESC")
    fun getActiveAlertsFlow(): Flow<List<Alert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert): Long

    @Update
    suspend fun updateAlert(alert: Alert)

    @Query("DELETE FROM alerts")
    suspend fun deleteAll()
}

@Dao
interface MerchantMappingDao {
    @Query("SELECT * FROM merchant_mappings WHERE merchantNormalized = :normalized")
    suspend fun getMapping(normalized: String): MerchantMapping?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapping(mapping: MerchantMapping)
}
