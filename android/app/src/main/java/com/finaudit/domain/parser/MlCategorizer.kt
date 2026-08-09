package com.finaudit.domain.parser

import android.content.Context
import com.finaudit.R
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

data class CategorizationResult(
    val category: String,
    val confidence: Float
)

class MlCategorizer(private val context: Context) {

    private val keywordMap = mutableMapOf<String, String>()

    init {
        loadKeywords()
    }

    private fun loadKeywords() {
        try {
            val inputStream = context.resources.openRawResource(R.raw.keywords)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonStr = reader.use { it.readText() }
            val jsonObject = JSONObject(jsonStr)
            
            jsonObject.keys().forEach { key ->
                keywordMap[key.lowercase()] = jsonObject.getString(key)
            }
        } catch (e: Exception) {
            // Fallback hardcoded lookups if JSON read fails
            keywordMap["swiggy"] = "Food and Dining"
            keywordMap["zomato"] = "Food and Dining"
            keywordMap["blinkit"] = "Food and Dining"
            keywordMap["uber"] = "Transport"
            keywordMap["ola"] = "Transport"
            keywordMap["netflix"] = "Subscriptions"
            keywordMap["spotify"] = "Subscriptions"
            keywordMap["apollo"] = "Health"
            keywordMap["amazon"] = "Shopping"
            keywordMap["flipkart"] = "Shopping"
        }
    }

    /**
     * Layer 1: Check user mapping overrides/learnings or Keyword Fallback Map.
     * Layer 2: Simulate fine-tuned TFLite classifier fallback inference.
     */
    fun categorize(merchantName: String, userMappings: Map<String, String> = emptyMap()): CategorizationResult {
        val normalized = merchantName.lowercase().trim()

        // 1. Check user corrections / learned mappings first
        val learnedCategory = userMappings[normalized]
        if (learnedCategory != null) {
            return CategorizationResult(learnedCategory, 1.0f) // 100% confidence for user corrected
        }

        // 2. Check hardcoded keyword map
        for ((keyword, category) in keywordMap) {
            if (normalized.contains(keyword)) {
                return CategorizationResult(category, 0.95f) // 95% confidence for direct match
            }
        }

        // 3. Fallback TFLite classification simulation (Phase 2 integration)
        // Since we ship Phase 1 with a robust keyword mapping and fallback rules:
        val guessedCategory = fallbackCategorizationRules(normalized)
        return CategorizationResult(guessedCategory, 0.85f) // 85% for ML/heuristic guesses
    }

    private fun fallbackCategorizationRules(merchantName: String): String {
        return when {
            merchantName.contains("telecom") || merchantName.contains("recharge") || merchantName.contains("power") || merchantName.contains("gas") -> "Utilities"
            merchantName.contains("cab") || merchantName.contains("auto") || merchantName.contains("metro") || merchantName.contains("rail") -> "Transport"
            merchantName.contains("cafe") || merchantName.contains("resto") || merchantName.contains("dhaba") || merchantName.contains("eats") -> "Food and Dining"
            merchantName.contains("edu") || merchantName.contains("school") || merchantName.contains("class") || merchantName.contains("course") -> "Education"
            merchantName.contains("rent") || merchantName.contains("pg") || merchantName.contains("home") || merchantName.contains("maintenance") -> "Housing"
            merchantName.contains("clinic") || merchantName.contains("med") || merchantName.contains("pharmacy") || merchantName.contains("hospital") -> "Health"
            merchantName.contains("movie") || merchantName.contains("show") || merchantName.contains("game") || merchantName.contains("play") -> "Entertainment"
            else -> "Other"
        }
    }
}
