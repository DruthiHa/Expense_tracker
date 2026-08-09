package com.finaudit

import com.finaudit.domain.model.Subscription
import com.finaudit.domain.model.Transaction
import com.finaudit.domain.parser.SubscriptionDetector
import org.junit.Assert.*
import org.junit.Test

class SubscriptionDetectorTest {

    @Test
    fun testDetectSubscriptionFromTransactions() {
        val now = System.currentTimeMillis()
        val oneMonthAgo = now - (30L * 24 * 60 * 60 * 1000)

        // Two debit transactions for the same amount, roughly 30 days apart
        val transactions = listOf(
            Transaction(
                amount = 649.0,
                direction = "DEBIT",
                merchantName = "Netflix",
                merchantNormalized = "netflix",
                category = "Subscriptions",
                categoryConfidence = 1.0f,
                paymentMethod = "CARD",
                accountLast4 = "1234",
                sourceType = "SMS",
                rawMessageHash = "h1",
                timestamp = oneMonthAgo,
                isReviewed = true
            ),
            Transaction(
                amount = 649.0,
                direction = "DEBIT",
                merchantName = "Netflix",
                merchantNormalized = "netflix",
                category = "Subscriptions",
                categoryConfidence = 1.0f,
                paymentMethod = "CARD",
                accountLast4 = "1234",
                sourceType = "SMS",
                rawMessageHash = "h2",
                timestamp = now,
                isReviewed = true
            )
        )

        val candidates = SubscriptionDetector.detectSubscriptions(transactions, emptyList())
        assertEquals(1, candidates.size)
        assertEquals("Netflix", candidates[0].name)
        assertEquals(649.0, candidates[0].amount, 0.0)
        assertEquals("UNDER_REVIEW", candidates[0].status)
    }

    @Test
    fun testIgnoreExistingSubscriptions() {
        val now = System.currentTimeMillis()
        val oneMonthAgo = now - (30L * 24 * 60 * 60 * 1000)

        val transactions = listOf(
            Transaction(amount = 649.0, direction = "DEBIT", merchantName = "Netflix", merchantNormalized = "netflix", category = "Subscriptions", categoryConfidence = 1.0f, paymentMethod = "CARD", accountLast4 = "1234", sourceType = "SMS", rawMessageHash = "h1", timestamp = oneMonthAgo, isReviewed = true),
            Transaction(amount = 649.0, direction = "DEBIT", merchantName = "Netflix", merchantNormalized = "netflix", category = "Subscriptions", categoryConfidence = 1.0f, paymentMethod = "CARD", accountLast4 = "1234", sourceType = "SMS", rawMessageHash = "h2", timestamp = now, isReviewed = true)
        )

        val existing = listOf(
            Subscription(name = "netflix", emoji = "🎬", amount = 649.0, billingCycle = "MONTHLY", category = "Subscriptions", renewDate = now + 100000, lastTransactionDate = now, status = "ACTIVE")
        )

        val candidates = SubscriptionDetector.detectSubscriptions(transactions, existing)
        assertTrue(candidates.isEmpty())
    }
}
