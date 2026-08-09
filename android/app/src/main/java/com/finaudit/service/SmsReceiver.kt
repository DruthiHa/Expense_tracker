package com.finaudit.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
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

class SmsReceiver : BroadcastReceiver() {

    private val smsParser = SmsParser()

    override fun onReceive(context: Context, intent: Intent) {
        val db = FinAuditDatabase.getDatabase(context)
        val repository = FinAuditRepositoryImpl(db)
        val categorizer = MlCategorizer(context)
        val scope = CoroutineScope(Dispatchers.IO)

        if (intent.action == "com.finaudit.SIMULATE_SMS") {
            val body = intent.getStringExtra("body") ?: return
            val sender = intent.getStringExtra("sender") ?: "AD-HDFCBK"
            val timestamp = System.currentTimeMillis()

            Log.d("FinAudit", "SmsReceiver custom SIMULATE_SMS received body: $body")
            val parsed = smsParser.parse(body, timestamp)
            if (parsed == null) {
                Log.e("FinAudit", "SmsReceiver: Failed to parse body text: $body")
                return
            }
            Log.d("FinAudit", "SmsReceiver parsed: Amount=${parsed.amount}, Merchant=${parsed.merchantName}, Acct=${parsed.accountLast4}")
            scope.launch {
                val mappings = repository.getAllTransactionsList().groupBy { it.merchantNormalized }
                    .mapValues { entry -> entry.value.first().category }
                val categoryResult = categorizer.categorize(parsed.merchantName, mappings)
                val confidence = categoryResult.confidence
                val isHigh = confidence >= 0.90f
                val transaction = Transaction(
                    amount = parsed.amount,
                    direction = parsed.direction,
                    merchantName = parsed.merchantName,
                    merchantNormalized = parsed.merchantName.lowercase().trim(),
                    category = categoryResult.category,
                    categoryConfidence = confidence,
                    paymentMethod = parsed.paymentMethod,
                    accountLast4 = parsed.accountLast4,
                    sourceType = "SMS",
                    rawMessageHash = "sim_${System.currentTimeMillis()}",
                    timestamp = timestamp,
                    isReviewed = isHigh
                )
                repository.insertTransaction(transaction)
            }
            return
        }

        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        for (sms in messages) {
            val body = sms.messageBody ?: continue
            val sender = sms.originatingAddress ?: continue
            val timestamp = sms.timestampMillis

            // Indian Bank Senders match patterns: eg: AD-HDFCBK, VM-ICICIB, BP-SBIIN
            if (!isIndianBankSender(sender)) continue

            val parsed = smsParser.parse(body, timestamp) ?: continue

            scope.launch {
                // Fetch user mapping overrides
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
                    paymentMethod = parsed.paymentMethod,
                    accountLast4 = parsed.accountLast4,
                    sourceType = "SMS",
                    rawMessageHash = md5(body),
                    timestamp = timestamp,
                    isReviewed = isHighConfidence // Auto confirmed if confidence >= 90%
                )

                repository.insertTransaction(transaction)

                // Dispatch notification update or trigger user alerts
                if (isHighConfidence) {
                    ForegroundCaptureService.updateStatus(context, "Captured ₹${parsed.amount} spend at ${parsed.merchantName}")
                } else {
                    ForegroundCaptureService.updateStatus(context, "New pending transaction at ${parsed.merchantName} needs review.")
                }
            }
        }
    }

    private fun isIndianBankSender(sender: String): Boolean {
        // Senders usually contain 6 characters like HDFCBK, ICICIB, SBIINB, AXISBK, KOTAKB, YESBNK, CANARA, PNBSMS
        val cleanSender = sender.uppercase()
        val bankKeywords = listOf("HDFC", "ICICI", "SBI", "AXIS", "KOTAK", "YESB", "CANARA", "PNB", "IPBB", "BOIND", "BOBAROD", "UNIONB")
        return bankKeywords.any { cleanSender.contains(it) } || cleanSender.matches(".*[A-Z]{2}-[A-Z]{6}.*".toRegex())
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return md.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
