package com.finaudit.data.repository

import com.finaudit.data.local.FinAuditDatabase
import com.finaudit.domain.model.*
import com.finaudit.domain.repository.FinAuditRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinAuditRepositoryImpl @Inject constructor(
    private val db: FinAuditDatabase
) : FinAuditRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> = db.transactionDao().getAllTransactionsFlow()

    override suspend fun getAllTransactionsList(): List<Transaction> = db.transactionDao().getAllTransactions()

    override fun getPendingReviewTransactions(): Flow<List<Transaction>> = db.transactionDao().getPendingReviewTransactionsFlow()

    override suspend fun insertTransaction(transaction: Transaction): Long = db.transactionDao().insertTransaction(transaction)

    override suspend fun updateTransaction(transaction: Transaction) = db.transactionDao().updateTransaction(transaction)

    override suspend fun deleteTransaction(transaction: Transaction) = db.transactionDao().deleteTransaction(transaction)

    override fun getAllSubscriptions(): Flow<List<Subscription>> = db.subscriptionDao().getAllSubscriptionsFlow()

    override suspend fun getAllSubscriptionsList(): List<Subscription> = db.subscriptionDao().getAllSubscriptions()

    override suspend fun insertSubscription(subscription: Subscription): Long = db.subscriptionDao().insertSubscription(subscription)

    override suspend fun updateSubscription(subscription: Subscription) = db.subscriptionDao().updateSubscription(subscription)

    override suspend fun deleteSubscription(subscription: Subscription) = db.subscriptionDao().deleteSubscription(subscription)

    override fun getBudgetsForMonth(month: String): Flow<List<Budget>> = db.budgetDao().getBudgetsForMonthFlow(month)

    override suspend fun getBudgetsForMonthList(month: String): List<Budget> = db.budgetDao().getBudgetsForMonth(month)

    override suspend fun insertBudget(budget: Budget): Long = db.budgetDao().insertBudget(budget)

    override suspend fun updateBudget(budget: Budget) = db.budgetDao().updateBudget(budget)

    override fun getAllGoals(): Flow<List<SavingsGoal>> = db.savingsGoalDao().getAllGoalsFlow()

    override suspend fun getAllGoalsList(): List<SavingsGoal> = db.savingsGoalDao().getAllGoals()

    override suspend fun insertGoal(goal: SavingsGoal): Long = db.savingsGoalDao().insertGoal(goal)

    override suspend fun updateGoal(goal: SavingsGoal) = db.savingsGoalDao().updateGoal(goal)

    override suspend fun deleteGoal(goal: SavingsGoal) = db.savingsGoalDao().deleteGoal(goal)

    override fun getActiveAlerts(): Flow<List<Alert>> = db.alertDao().getActiveAlertsFlow()

    override suspend fun insertAlert(alert: Alert): Long = db.alertDao().insertAlert(alert)

    override suspend fun updateAlert(alert: Alert) = db.alertDao().updateAlert(alert)

    override suspend fun getMerchantMapping(normalizedName: String): MerchantMapping? = db.merchantMappingDao().getMapping(normalizedName)

    override suspend fun insertMerchantMapping(mapping: MerchantMapping) = db.merchantMappingDao().insertMapping(mapping)

    override suspend fun clearAllData() {
        db.transactionDao().deleteAll()
        db.subscriptionDao().deleteAll()
        db.budgetDao().deleteAll()
        db.savingsGoalDao().deleteAll()
        db.alertDao().deleteAll()
    }
}
