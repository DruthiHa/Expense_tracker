package com.finaudit.domain.parser

import com.finaudit.domain.model.Subscription
import com.finaudit.domain.model.Transaction
import java.util.Calendar

object SubscriptionDetector {

    /**
     * Scan transactions to find duplicate merchant/amount spends within +/- 5 days of monthly cycle.
     * Returns a list of newly detected Subscription candidates.
     */
    fun detectSubscriptions(
        allTransactions: List<Transaction>,
        existingSubscriptions: List<Subscription>
    ): List<Subscription> {
        val debits = allTransactions.filter { it.direction == "DEBIT" }.sortedBy { it.timestamp }
        val candidates = mutableListOf<Subscription>()
        
        // Group by normalized merchant
        val merchantGroups = debits.groupBy { it.merchantNormalized }

        for ((merchantNormalized, txs) in merchantGroups) {
            if (txs.size < 2) continue

            // Check if already subscribed
            val alreadySubscribed = existingSubscriptions.any { 
                it.name.equals(merchantNormalized, ignoreCase = true) 
            }
            if (alreadySubscribed) continue

            // Compare adjacent transactions to see if they look monthly
            for (i in 0 until txs.size - 1) {
                val tx1 = txs[i]
                val tx2 = txs[i + 1]

                val diffMs = tx2.timestamp - tx1.timestamp
                val diffDays = diffMs / (1000 * 60 * 60 * 24)

                // Monthly subscription usually occurs between 25 and 35 days
                if (diffDays in 25..35) {
                    // Check if amounts are similar (within 5% range)
                    val diffAmount = Math.abs(tx1.amount - tx2.amount)
                    val maxAmount = Math.max(tx1.amount, tx2.amount)
                    if (maxAmount > 0 && (diffAmount / maxAmount) <= 0.05) {
                        
                        // Formulate candidate
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = tx2.timestamp
                            add(Calendar.MONTH, 1)
                        }
                        
                        candidates.add(
                            Subscription(
                                name = tx2.merchantName,
                                emoji = getEmojiForCategory(tx2.category),
                                amount = tx2.amount,
                                billingCycle = "MONTHLY",
                                category = tx2.category,
                                renewDate = calendar.timeInMillis,
                                lastTransactionDate = tx2.timestamp,
                                status = "UNDER_REVIEW", // Status on capture is under review
                                autoDetected = true
                            )
                        )
                        break
                    }
                }
            }
        }

        return candidates
    }

    fun getEmojiForCategory(category: String): String {
        return when (category) {
            "Food and Dining" -> "🍔"
            "Transport" -> "🚗"
            "Subscriptions" -> "🎬"
            "Shopping" -> "🛍️"
            "Entertainment" -> "🍿"
            "Utilities" -> "⚡"
            "Education" -> "📚"
            "Housing" -> "🏠"
            "Health" -> "💊"
            else -> "💰"
        }
    }
}
