package com.finaudit.domain.parser

import java.util.regex.Pattern

data class ParsedTransaction(
    val amount: Double,
    val direction: String, // "DEBIT" or "CREDIT"
    val merchantName: String,
    val accountLast4: String,
    val paymentMethod: String,
    val timestamp: Long
)

class SmsParser {

    private val patterns = listOf(
        // General Debit patterns
        Pattern.compile("(?i)(?:debited|sent|spent|paid|tx)*\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s*(?:to|at|for)*\\s*([^\\n]*?)\\s*(?:on|at|using|via)*\\s*a/c\\s*(?:ending|xx|x)*(\\d{4})"),
        Pattern.compile("(?i)(?:tx|txn).*?(?:debited|spent|sent|paid).*?(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*).*?to\\s+([^\\n]*?)\\s+.*?a/c\\s*.*?(\\d{4})"),
        Pattern.compile("(?i)(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s*(?:debited|spent).*?a/c\\s*.*?(\\d{4})\\s*(?:to|at)\\s*([^\\n]*?)"),
        
        // HDFC Specific
        Pattern.compile("(?i)alert:.*?(?:debited|spent).*?(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*).*?at\\s+([^\\n]*?)\\s+.*?card\\s*ending\\s*(\\d{4})"),
        Pattern.compile("(?i)spends\\s+on\\s+your\\s+hdfc\\s+bank\\s+card\\s*.*?ending\\s*(\\d{4})\\s+of\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+at\\s+([^\\n]*?)"),
        Pattern.compile("(?i)rs\\.?\\s*([\\d,]+\\.?\\d*)\\s+debited\\s+from\\s+hdfc\\s+bank\\s+a/c\\s*.*?(\\d{4})\\s+to\\s+([^\\n]*?)"),
        
        // ICICI Specific
        Pattern.compile("(?i)your\\s+a/c\\s*.*?(\\d{4})\\s+has\\s+been\\s+debited\\s+with\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+on.*?info:\\s*([^\\n]*?)"),
        Pattern.compile("(?i)transaction\\s+on\\s+icici\\s+bank\\s+card\\s*.*?(\\d{4})\\s+for\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+at\\s+([^\\n]*?)"),
        
        // SBI Specific
        Pattern.compile("(?i)txn\\s+of\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+debited\\s+from\\s+sbi\\s+a/c\\s*.*?(\\d{4})\\s+to\\s+([^\\n]*?)"),
        Pattern.compile("(?i)your\\s+sbi\\s+a/c\\s*.*?(\\d{4})\\s+debited\\s+by\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+to\\s+([^\\n]*?)"),
        
        // Axis Specific
        Pattern.compile("(?i)your\\s+axis\\s+bank\\s+a/c\\s*.*?(\\d{4})\\s+is\\s+debited\\s+for\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+at\\s+([^\\n]*?)"),
        Pattern.compile("(?i)axis\\s+bank.*?debited.*?([\\d,]+\\.?\\d*).*?at\\s+([^\\n]*?).*?a/c.*?(\\d{4})"),
        
        // Kotak Specific
        Pattern.compile("(?i)debited\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+from\\s+kotak\\s+a/c\\s*.*?(\\d{4})\\s+to\\s+([^\\n]*?)"),
        
        // Credit patterns
        Pattern.compile("(?i)(?:credited|received|deposited|refunded)\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s*(?:to|in|at)*\\s*a/c\\s*(?:ending|xx|x)*(\\d{4})\\s*(?:from|by)*\\s*([^\\n]*?)"),
        Pattern.compile("(?i)(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s*(?:credited|deposited)\\s+to\\s+a/c\\s*.*?(\\d{4})\\s*(?:from|by)*\\s*([^\\n]*?)"),
        // Friend-to-Friend UPI Credit Notification formats (GPay, PhonePe, Paytm)
        Pattern.compile("(?i)(?:received|received\\s+payment\\s+of)\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)\\s+from\\s+([^\\n]*?)"),
        Pattern.compile("(?i)([^\\n]*?)\\s+(?:sent|transferred|paid)\\s+you\\s*(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)")
    )

    fun parse(message: String, timestamp: Long = System.currentTimeMillis()): ParsedTransaction? {
        // Clean double spaces or newlines for uniform matching
        val text = message.replace("\\s+".toRegex(), " ").trim()

        for (pattern in patterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                try {
                    val isCredit = text.contains("credited", ignoreCase = true) || 
                                   text.contains("received", ignoreCase = true) || 
                                   text.contains("deposited", ignoreCase = true) ||
                                   text.contains("refunded", ignoreCase = true)
                    
                    val direction = if (isCredit) "CREDIT" else "DEBIT"
                    
                    // Simple logic to extract groups dynamically depending on pattern structure
                    var amountStr = ""
                    var merchant = ""
                    var account = ""

                    if (pattern.pattern().contains("a/c\\s*.*?(\\d{4})\\s+has\\s+been\\s+debited") || 
                        pattern.pattern().contains("a/c\\s*.*?(\\d{4})\\s+of") || 
                        pattern.pattern().contains("a/c\\s*.*?(\\d{4})\\s+is\\s+debited")) {
                        // Pattern structure where account is first group
                        account = matcher.group(1).orEmpty()
                        amountStr = matcher.group(2).orEmpty()
                        merchant = matcher.group(3).orEmpty()
                    } else if (pattern.pattern().contains("credited") && pattern.pattern().contains("from|by")) {
                        // Credit pattern checks
                        amountStr = matcher.group(1).orEmpty()
                        account = matcher.group(2).orEmpty()
                        merchant = matcher.group(3).orEmpty()
                    } else if (pattern.pattern().contains("transferred") || pattern.pattern().contains("sent") || pattern.pattern().contains("paid")) {
                        // Pattern: (Merchant/Sender) sent you (Amount)
                        merchant = matcher.group(1).orEmpty()
                        amountStr = matcher.group(2).orEmpty()
                        account = "UPI"
                    } else if (pattern.pattern().contains("received") && !pattern.pattern().contains("a/c")) {
                        // Pattern: Received (Amount) from (Merchant/Sender)
                        amountStr = matcher.group(1).orEmpty()
                        merchant = matcher.group(2).orEmpty()
                        account = "UPI"
                    } else {
                        // Standard matching: amount (1), merchant (2), account (3)
                        if (matcher.groupCount() >= 3) {
                            amountStr = matcher.group(1).orEmpty()
                            merchant = matcher.group(2).orEmpty()
                            account = matcher.group(3).orEmpty()
                        } else if (matcher.groupCount() == 2) {
                            amountStr = matcher.group(1).orEmpty()
                            account = matcher.group(2).orEmpty()
                            merchant = "Unknown Merchant"
                        }
                    }

                    val amount = amountStr.replace(",", "").toDoubleOrNull() ?: continue
                    val normalizedMerchant = cleanMerchantName(merchant)
                    
                    return ParsedTransaction(
                        amount = amount,
                        direction = direction,
                        merchantName = normalizedMerchant,
                        accountLast4 = account.takeLast(4),
                        paymentMethod = if (text.contains("upi", ignoreCase = true)) "UPI" else "CARD",
                        timestamp = timestamp
                    )
                } catch (e: Exception) {
                    // Ignore errors during regex matching parsing
                }
            }
        }
        
        // Emergency Fallback: If no match but contains cash amount indicators, do a simple regex extraction
        val fallbackAmountPattern = Pattern.compile("(?i)(?:rs\\.?|inr|₹)\\s*([\\d,]+\\.?\\d*)")
        val fallbackMatcher = fallbackAmountPattern.matcher(text)
        if (fallbackMatcher.find()) {
            val amountStr = fallbackMatcher.group(1)?.replace(",", "")
            val amount = amountStr?.toDoubleOrNull()
            if (amount != null && amount > 0.0) {
                val isCredit = text.contains("credited", ignoreCase = true) || text.contains("received", ignoreCase = true)
                val direction = if (isCredit) "CREDIT" else "DEBIT"
                
                // Try to find any sequence containing 'to ' or 'at '
                var merchant = "Merchant"
                val merchantMatcher = Pattern.compile("(?i)(?:to|at|info:)\\s+([^\\s]+(?:\\s+[^\\s]+){0,2})").matcher(text)
                if (merchantMatcher.find()) {
                    merchant = merchantMatcher.group(1).orEmpty()
                }

                // Check for account ending
                var ac = "XXXX"
                val acMatcher = Pattern.compile("(?i)(?:a/c|card)\\s*(?:ending|xx|x)*\\s*(\\d{4})").matcher(text)
                if (acMatcher.find()) {
                    ac = acMatcher.group(1).orEmpty()
                }

                return ParsedTransaction(
                    amount = amount,
                    direction = direction,
                    merchantName = cleanMerchantName(merchant),
                    accountLast4 = ac,
                    paymentMethod = if (text.contains("upi", ignoreCase = true)) "UPI" else "CARD",
                    timestamp = timestamp
                )
            }
        }

        return null
    }

    private fun cleanMerchantName(name: String): String {
        var clean = name.trim()
        
        // Remove trailing timestamps, transaction IDs, status labels
        clean = clean.split("(?i)\\s+(?:on|at|ref|txn|info|using|via|dated)\\s".toRegex())[0]
        
        // Strip common punctuation prefixes or suffixes
        clean = clean.trim { it == '*' || it == '#' || it == '-' || it == '_' || it.isWhitespace() }
        
        // Limit to 30 chars
        if (clean.length > 30) {
            clean = clean.substring(0, 30)
        }
        
        return clean.trim()
    }
}
