package com.finaudit.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.finaudit.data.local.FinAuditDatabase
import com.finaudit.data.repository.FinAuditRepositoryImpl
import com.finaudit.domain.model.Transaction
import com.finaudit.domain.parser.MlCategorizer
import com.finaudit.domain.parser.SmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import android.app.Notification
class TransactionNotificationListener : NotificationListenerService() {

    private val smsParser = SmsParser()


    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.d("NotifListener", "Package: ${sbn.packageName}")
        Log.d("NotifListener", "Text: ${sbn.notification.extras.getString(Notification.EXTRA_TEXT)}")
        Log.d("NotifListener", "BigText: ${sbn.notification.extras.getString(Notification.EXTRA_BIG_TEXT)}")
        val packageName = sbn.packageName ?: return
        val extras = sbn.notification.extras ?: return
        
        // Listen for UPI/payment apps: GPay, PhonePe, Paytm, BHIM, Amazon Pay, CRED
        val paymentApps = listOf(
            "com.google.android.apps.nbu.paisa.user", // Google Pay
            "com.phonepe.app",                       // PhonePe
            "net.one97.paytm",                       // Paytm
            "in.org.npci.upiapp",                    // BHIM
            "in.amazon.mShop.android.shopping",      // Amazon Pay
            "co.brainbehind.cred",                   // CRED
            "com.android.shell"                      // System Shell for mock adb testing
        )

        Log.d("FinAudit", "Notification intercepted from package: $packageName")
        if (!paymentApps.contains(packageName)) return

        val title = extras.getString("android.title") ?: ""
        val text = extras.getString("android.text") ?: ""
        val fullContent = "$title $text"

        val db = FinAuditDatabase.getDatabase(applicationContext)
        val repository = FinAuditRepositoryImpl(db)
        val categorizer = MlCategorizer(applicationContext)
        val scope = CoroutineScope(Dispatchers.IO)

        Log.d("FinAudit", "Processing notification content: $fullContent")
        // Run general parser matching logic against notification text
        val parsed = smsParser.parse(fullContent, sbn.postTime)
        if (parsed == null) {
            Log.e("FinAudit", "Failed to parse transaction from text: $fullContent")
            return
        }
        Log.d("FinAudit", "Parsed transaction details successfully: Amount=${parsed.amount}, Merchant=${parsed.merchantName}")

        scope.launch {
            val mappings = repository.getAllTransactionsList().groupBy { it.merchantNormalized }
                .mapValues { entry -> entry.value.first().category }
            
            val categoryResult = categorizer.categorize(parsed.merchantName, mappings)
            val confidencePercentage = categoryResult.confidence
            val isHighConfidence = confidencePercentage >= 0.90f

            val transaction = Transaction(
                amount = parsed.amount,
                direction = parsed.direction,
                merchantName = parsed.merchantName,
                merchantNormalized = parsed.merchantName.lowercase().trim(),
                category = categoryResult.category,
                categoryConfidence = confidencePercentage,
                paymentMethod = "UPI",
                accountLast4 = parsed.accountLast4.ifBlank { "0000" },
                sourceType = "NOTIFICATION",
                rawMessageHash = md5(fullContent),
                timestamp = sbn.postTime,
                isReviewed = isHighConfidence
            )

            repository.insertTransaction(transaction)

            if (isHighConfidence) {
                ForegroundCaptureService.updateStatus(
                    applicationContext,
                    "Captured ₹${parsed.amount} push alert from ${parsed.merchantName}"
                )
            } else {
                ForegroundCaptureService.updateStatus(
                    applicationContext,
                    "Captured transaction at ${parsed.merchantName} needs review"
                )
            }
        }
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return md.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
