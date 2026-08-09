package com.finaudit.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.finaudit.domain.model.*
import com.finaudit.domain.parser.SubscriptionDetector
import com.finaudit.domain.repository.FinAuditRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FinAuditRepository
) : ViewModel() {

    // Onboarding preferences state
    val isOnboarded = MutableStateFlow(false)
    val whitelistedSenders = MutableStateFlow(setOf("HDFCBK", "ICICIB", "SBIINB", "AXISBK", "KOTAKB", "YESBNK"))
    val blacklistedSenders = MutableStateFlow(setOf<String>())
    val isAutoCaptureEnabled = MutableStateFlow(true)

    // Data streams
    val transactions = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val subscriptions = repository.getAllSubscriptions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val budgets = repository.getBudgetsForMonth(getCurrentMonthKey())
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val goals = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val alerts = repository.getActiveAlerts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val reviewQueue = repository.getPendingReviewTransactions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        // Safe empty database state ready for new users to deploy.
        // Mock data population removed for clean setup.
    }

    // Transaction Actions
    fun confirmTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction.copy(isReviewed = true))
            
            // Learn mapping locally
            repository.insertMerchantMapping(
                MerchantMapping(
                    merchantNormalized = transaction.merchantNormalized,
                    category = transaction.category,
                    source = "USER_CORRECTED"
                )
            )

            // Re-run subscription auto-detector
            triggerSubscriptionScan()
        }
    }

    fun updateTransactionCategory(transaction: Transaction, newCategory: String) {
        viewModelScope.launch {
            val updated = transaction.copy(
                category = newCategory,
                categoryConfidence = 1.0f,
                isReviewed = true
            )
            repository.updateTransaction(updated)
            repository.insertMerchantMapping(
                MerchantMapping(
                    merchantNormalized = transaction.merchantNormalized,
                    category = newCategory,
                    source = "USER_CORRECTED"
                )
            )
            triggerSubscriptionScan()
        }
    }

    fun simulateNotification(context: android.content.Context, fullText: String) {
        viewModelScope.launch {
            val parser = com.finaudit.domain.parser.SmsParser()
            val parsed = parser.parse(fullText)
            if (parsed != null) {
                val categorizer = com.finaudit.domain.parser.MlCategorizer(context)
                val mappings = repository.getAllTransactionsList().groupBy { it.merchantNormalized }
                    .mapValues { entry -> entry.value.first().category }
                val categoryResult = categorizer.categorize(parsed.merchantName, mappings)
                val confidence = categoryResult.confidence
                val isHighConfidence = confidence >= 0.90f

                val transaction = Transaction(
                    amount = parsed.amount,
                    direction = parsed.direction,
                    merchantName = parsed.merchantName,
                    merchantNormalized = parsed.merchantName.lowercase().trim(),
                    category = categoryResult.category,
                    categoryConfidence = confidence,
                    paymentMethod = "UPI",
                    accountLast4 = parsed.accountLast4.ifBlank { "9999" },
                    sourceType = "NOTIFICATION",
                    rawMessageHash = "sim_${System.currentTimeMillis()}",
                    timestamp = System.currentTimeMillis(),
                    isReviewed = isHighConfidence
                )
                repository.insertTransaction(transaction)
                triggerSubscriptionScan()
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun clearAllDatabaseData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun addManualTransaction(
        amount: Double,
        direction: String,
        merchant: String,
        category: String,
        paymentMethod: String
    ) {
        viewModelScope.launch {
            val tx = Transaction(
                amount = amount,
                direction = direction,
                merchantName = merchant,
                merchantNormalized = merchant.lowercase().trim(),
                category = category,
                categoryConfidence = 1.0f,
                paymentMethod = paymentMethod,
                accountLast4 = "MANL",
                sourceType = "MANUAL",
                rawMessageHash = "manual_${System.currentTimeMillis()}",
                timestamp = System.currentTimeMillis(),
                isReviewed = true
            )
            repository.insertTransaction(tx)
            triggerSubscriptionScan()
        }
    }

    // Subscription Actions
    fun cycleSubscriptionStatus(subscription: Subscription) {
        viewModelScope.launch {
            val nextStatus = when (subscription.status) {
                "ACTIVE" -> "UNDER_REVIEW"
                "UNDER_REVIEW" -> "UNUSED"
                else -> "ACTIVE"
            }
            repository.updateSubscription(subscription.copy(status = nextStatus))
        }
    }

    fun addSubscriptionManually(name: String, amount: Double, category: String) {
        viewModelScope.launch {
            val sub = Subscription(
                name = name,
                emoji = SubscriptionDetector.getEmojiForCategory(category),
                amount = amount,
                billingCycle = "MONTHLY",
                category = category,
                renewDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                lastTransactionDate = System.currentTimeMillis(),
                status = "ACTIVE",
                autoDetected = false
            )
            repository.insertSubscription(sub)
        }
    }

    fun deleteSubscription(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }

    // Budget & Goal Actions
    fun setCategoryBudget(category: String, limit: Double) {
        viewModelScope.launch {
            val existing = budgets.value.find { it.category == category }
            if (existing != null) {
                repository.updateBudget(existing.copy(monthlyLimit = limit))
            } else {
                repository.insertBudget(Budget(category = category, monthlyLimit = limit, month = getCurrentMonthKey()))
            }
        }
    }

    fun addSavingsGoal(name: String, emoji: String, target: Double) {
        viewModelScope.launch {
            repository.insertGoal(SavingsGoal(name = name, emoji = emoji, targetAmount = target, savedAmount = 0.0))
        }
    }

    fun contributeToGoal(goal: SavingsGoal, amount: Double) {
        viewModelScope.launch {
            repository.updateGoal(goal.copy(savedAmount = goal.savedAmount + amount))
        }
    }

    fun deleteGoal(goal: SavingsGoal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    // Alert Actions
    fun dismissAlert(alert: Alert) {
        viewModelScope.launch {
            repository.updateAlert(alert.copy(isDismissed = true))
        }
    }

    private suspend fun triggerSubscriptionScan() {
        val txs = repository.getAllTransactionsList()
        val subs = repository.getAllSubscriptionsList()
        val candidates = SubscriptionDetector.detectSubscriptions(txs, subs)
        for (candidate in candidates) {
            repository.insertSubscription(candidate)
            
            // Insert corresponding smart alert
            repository.insertAlert(
                Alert(
                    type = "WARNING",
                    message = "Subscription detected: ${candidate.name} — ₹${candidate.amount}/mo.",
                    actionScreen = "SUBSCRIPTIONS",
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun getCurrentMonthKey(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-${String.format("%02d", cal.get(Calendar.MONTH) + 1)}"
    }

    private suspend fun populateSampleData() {
        // Populate standard initial transaction set
        val txList = listOf(
            Transaction(amount = 450.0, direction = "DEBIT", merchantName = "Swiggy", merchantNormalized = "swiggy", category = "Food and Dining", categoryConfidence = 0.95f, paymentMethod = "UPI", accountLast4 = "4321", sourceType = "SMS", rawMessageHash = "h1", timestamp = System.currentTimeMillis() - 3600000, isReviewed = true),
            Transaction(amount = 250.0, direction = "DEBIT", merchantName = "Uber", merchantNormalized = "uber", category = "Transport", categoryConfidence = 0.96f, paymentMethod = "UPI", accountLast4 = "4321", sourceType = "SMS", rawMessageHash = "h2", timestamp = System.currentTimeMillis() - 86400000, isReviewed = true),
            Transaction(amount = 649.0, direction = "DEBIT", merchantName = "Netflix", merchantNormalized = "netflix", category = "Subscriptions", categoryConfidence = 0.98f, paymentMethod = "CARD", accountLast4 = "9876", sourceType = "SMS", rawMessageHash = "h3", timestamp = System.currentTimeMillis() - 1728000000, isReviewed = true),
            Transaction(amount = 649.0, direction = "DEBIT", merchantName = "Netflix", merchantNormalized = "netflix", category = "Subscriptions", categoryConfidence = 0.98f, paymentMethod = "CARD", accountLast4 = "9876", sourceType = "SMS", rawMessageHash = "h3b", timestamp = System.currentTimeMillis() - (1728000000 + (30L * 24 * 60 * 60 * 1000)), isReviewed = true),
            Transaction(amount = 120.0, direction = "DEBIT", merchantName = "Zomato", merchantNormalized = "zomato", category = "Food and Dining", categoryConfidence = 0.88f, paymentMethod = "UPI", accountLast4 = "4321", sourceType = "NOTIFICATION", rawMessageHash = "h4", timestamp = System.currentTimeMillis() - 1200000, isReviewed = false), // Pending review
            Transaction(amount = 55000.0, direction = "CREDIT", merchantName = "TCS Salary", merchantNormalized = "tcs salary", category = "Other", categoryConfidence = 0.95f, paymentMethod = "NETBANKING", accountLast4 = "4321", sourceType = "SMS", rawMessageHash = "h5", timestamp = System.currentTimeMillis() - 259200000, isReviewed = true),
            Transaction(amount = 1500.0, direction = "DEBIT", merchantName = "Zara", merchantNormalized = "zara", category = "Shopping", categoryConfidence = 0.92f, paymentMethod = "UPI", accountLast4 = "4321", sourceType = "SMS", rawMessageHash = "h6", timestamp = System.currentTimeMillis() - 518400000, isReviewed = true)
        )

        for (tx in txList) {
            repository.insertTransaction(tx)
        }

        // Subscriptions
        repository.insertSubscription(
            Subscription(name = "Netflix Premium", emoji = "🎬", amount = 649.0, billingCycle = "MONTHLY", category = "Subscriptions", renewDate = System.currentTimeMillis() + 86400000 * 3, lastTransactionDate = System.currentTimeMillis() - 1728000000, status = "ACTIVE", autoDetected = true)
        )
        repository.insertSubscription(
            Subscription(name = "Spotify Individual", emoji = "🎵", amount = 119.0, billingCycle = "MONTHLY", category = "Subscriptions", renewDate = System.currentTimeMillis() + 86400000 * 15, lastTransactionDate = System.currentTimeMillis() - 86400000 * 15, status = "UNUSED", autoDetected = false)
        )

        // Budgets
        repository.insertBudget(Budget(category = "Food and Dining", monthlyLimit = 8000.0, month = getCurrentMonthKey()))
        repository.insertBudget(Budget(category = "Transport", monthlyLimit = 3000.0, month = getCurrentMonthKey()))

        // Savings Goals
        repository.insertGoal(SavingsGoal(name = "MacBook Pro", emoji = "💻", targetAmount = 150000.0, savedAmount = 45000.0))

        // Alerts
        repository.insertAlert(
            Alert(type = "CRITICAL", message = "Unused subscription 'Spotify' is about to renew in 15 days.", actionScreen = "SUBSCRIPTIONS", createdAt = System.currentTimeMillis())
        )
        repository.insertAlert(
            Alert(type = "WARNING", message = "Food and Dining budget is at 74% limit.", actionScreen = "BUDGETS", createdAt = System.currentTimeMillis() - 3600000)
        )
    }
}
