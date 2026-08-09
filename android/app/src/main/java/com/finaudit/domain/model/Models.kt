package com.finaudit.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val direction: String, // "DEBIT" or "CREDIT"
    val merchantName: String,
    val merchantNormalized: String,
    val category: String,
    val categoryConfidence: Float, // 0.0 - 1.0 (equivalent to 0-100%)
    val paymentMethod: String, // "UPI", "CARD", "NETBANKING", etc.
    val accountLast4: String,
    val sourceType: String, // "SMS", "NOTIFICATION", "MANUAL"
    val rawMessageHash: String,
    val timestamp: Long,
    val notes: String = "",
    val isReviewed: Boolean = false
)

@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val amount: Double,
    val billingCycle: String, // "MONTHLY", "ANNUALLY"
    val category: String,
    val renewDate: Long,
    val lastTransactionDate: Long,
    val status: String, // "ACTIVE", "UNDER_REVIEW", "UNUSED"
    val notes: String = "",
    val autoDetected: Boolean = false
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val monthlyLimit: Double,
    val month: String // "YYYY-MM"
)

@Entity(tableName = "savings_goals")
data class SavingsGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String,
    val targetAmount: Double,
    val savedAmount: Double,
    val deadline: Long? = null
)

@Entity(tableName = "alerts")
data class Alert(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "CRITICAL", "WARNING", "SUCCESS"
    val message: String,
    val actionScreen: String, // "DASHBOARD", "TRANSACTIONS", "SUBSCRIPTIONS", "ANALYTICS", "BUDGETS", "ALERTS"
    val createdAt: Long,
    val isDismissed: Boolean = false
)

@Entity(tableName = "merchant_mappings", primaryKeys = ["merchantNormalized"])
data class MerchantMapping(
    val merchantNormalized: String,
    val category: String,
    val source: String, // "AUTO", "USER_CORRECTED"
    val count: Int = 1
)
