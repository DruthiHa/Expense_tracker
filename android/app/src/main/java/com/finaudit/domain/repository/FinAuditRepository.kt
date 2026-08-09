package com.finaudit.domain.repository

import com.finaudit.domain.model.*
import kotlinx.coroutines.flow.Flow

interface FinAuditRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    suspend fun getAllTransactionsList(): List<Transaction>
    fun getPendingReviewTransactions(): Flow<List<Transaction>>
    suspend fun insertTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)

    fun getAllSubscriptions(): Flow<List<Subscription>>
    suspend fun getAllSubscriptionsList(): List<Subscription>
    suspend fun insertSubscription(subscription: Subscription): Long
    suspend fun updateSubscription(subscription: Subscription)
    suspend fun deleteSubscription(subscription: Subscription)

    fun getBudgetsForMonth(month: String): Flow<List<Budget>>
    suspend fun getBudgetsForMonthList(month: String): List<Budget>
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)

    fun getAllGoals(): Flow<List<SavingsGoal>>
    suspend fun getAllGoalsList(): List<SavingsGoal>
    suspend fun insertGoal(goal: SavingsGoal): Long
    suspend fun updateGoal(goal: SavingsGoal)
    suspend fun deleteGoal(goal: SavingsGoal)

    fun getActiveAlerts(): Flow<List<Alert>>
    suspend fun insertAlert(alert: Alert): Long
    suspend fun updateAlert(alert: Alert)

    suspend fun getMerchantMapping(normalizedName: String): MerchantMapping?
    suspend fun insertMerchantMapping(mapping: MerchantMapping)
    
    suspend fun clearAllData()
}
