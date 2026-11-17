package com.smart.expense.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID
import java.util.regex.Pattern
import kotlin.math.abs

data class ParsedBankAlert(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val type: String, // "expense" or "income"
    val category: String,
    val merchant: String,
    val sourceApp: String,
    val rawText: String,
    val timestamp: Long = System.currentTimeMillis()
)

object BankSmsParser {

    private const val PREFS_NAME = "smart_expense_auto_track"
    private const val KEY_PENDING = "pending_alerts"
    private const val KEY_RECENT_SIGS = "recent_signatures"
    private const val KEY_AUTO_LOG = "auto_log_enabled"
    private const val DEDUP_WINDOW_MS = 3 * 60 * 1000L // 3 minutes

    private val IGNORE_KEYWORDS = listOf(
        "otp", "one time password", "verification code", "verify your",
        "cvv", "pin is", "do not share", "offer", "win ", "won ",
        "cashback up to", "pre-approved", "apply now", "loan approved",
        "emi due", "reminder", "bill generated", "statement generated",
        "failed", "declined", "unsuccessful", "insufficient"
    )

    private val DEBIT_KEYWORDS = listOf(
        "debited", "spent", "paid", "sent", "withdrawn", "deducted",
        "purchase", "purchased", "charged", "transferred to", "dr ", "dr."
    )

    private val CREDIT_KEYWORDS = listOf(
        "credited", "received", "deposited", "refund", "refunded",
        "cashback", "added to", "cr ", "cr."
    )

    private val AMOUNT_PATTERN: Pattern = Pattern.compile(
        "(?i)(?:rs\\.?|inr|₹|\\$|usd|eur|aed)\\s*([0-9][0-9,]*(?:\\.[0-9]{1,2})?)|([0-9][0-9,]*(?:\\.[0-9]{1,2})?)\\s*(?:rs\\.?|inr|₹)"
    )

    private val MERCHANT_PATTERN: Pattern = Pattern.compile(
        "(?i)(?:to|at|via|towards|from|info[:\\s]+)\\s+([A-Za-z0-9 &._-]{2,28})(?:\\s+on|\\s+ref|\\s+upi|\\s+avl|\\.|,|$)"
    )

    fun parse(text: String, packageName: String = ""): ParsedBankAlert? {
        val clean = text.trim()
        if (clean.length < 12) return null

        val lower = clean.lowercase()

        // Skip OTPs, promos, failed transactions
        if (IGNORE_KEYWORDS.any { lower.contains(it) }) return null

        val isDebit = DEBIT_KEYWORDS.any { lower.contains(it) }
        val isCredit = CREDIT_KEYWORDS.any { lower.contains(it) }
        if (!isDebit && !isCredit) return null

        // Extract amount
        val matcher = AMOUNT_PATTERN.matcher(clean)
        var amount: Double? = null
        while (matcher.find()) {
            val rawNum = (matcher.group(1) ?: matcher.group(2))?.replace(",", "")
            val parsed = rawNum?.toDoubleOrNull()
            if (parsed != null && parsed > 0 && parsed < 10_000_000) {
                amount = parsed
                break
            }
        }
        if (amount == null) return null

        // If both appear (e.g. "debited... credited to merchant"), prioritize first occurrence
        val type = when {
            isDebit && !isCredit -> "expense"
            isCredit && !isDebit -> "income"
            else -> {
                val firstDebit = DEBIT_KEYWORDS.mapNotNull { kw -> lower.indexOf(kw).takeIf { it >= 0 } }.minOrNull() ?: Int.MAX_VALUE
                val firstCredit = CREDIT_KEYWORDS.mapNotNull { kw -> lower.indexOf(kw).takeIf { it >= 0 } }.minOrNull() ?: Int.MAX_VALUE
                if (firstDebit <= firstCredit) "expense" else "income"
            }
        }

        val merchant = extractMerchant(clean)
        val sourceApp = detectSourceApp(lower, packageName)
        val category = guessCategory(lower, merchant.lowercase(), type)

        return ParsedBankAlert(
            amount = amount,
            type = type,
            category = category,
            merchant = merchant,
            sourceApp = sourceApp,
            rawText = clean
        )
    }

    private fun extractMerchant(text: String): String {
        val matcher = MERCHANT_PATTERN.matcher(text)
        if (matcher.find()) {
            val found = matcher.group(1)?.trim()
            if (!found.isNullOrBlank() && !found.all { it.isDigit() }) {
                return found
            }
        }
        return "Bank / UPI Transfer"
    }

    private fun detectSourceApp(lower: String, packageName: String): String {
        return when {
            packageName.contains("nbu.paisa") || lower.contains("google pay") || lower.contains("gpay") -> "Google Pay"
            packageName.contains("phonepe") || lower.contains("phonepe") -> "PhonePe"
            packageName.contains("paytm") || lower.contains("paytm") -> "Paytm"
            lower.contains("hdfc") -> "HDFC Bank"
            lower.contains("sbi") -> "SBI"
            lower.contains("icici") -> "ICICI Bank"
            lower.contains("axis") -> "Axis Bank"
            lower.contains("kotak") -> "Kotak Bank"
            lower.contains("upi") -> "UPI"
            else -> "Bank Alert"
        }
    }

    private fun guessCategory(lower: String, merchantLower: String, type: String): String {
        if (type == "income") {
            return if (lower.contains("salary")) "Salary"
            else if (lower.contains("refund") || lower.contains("cashback")) "Refund"
            else "Income"
        }
        val combined = "$lower $merchantLower"
        return when {
            combined.containsAny("swiggy", "zomato", "dominos", "pizza", "mcdonald", "kfc", "cafe", "restaurant", "food", "starbucks", "bakery") -> "Food & Dining"
            combined.containsAny("uber", "ola", "rapido", "metro", "irctc", "petrol", "fuel", "shell", "hpcl", "bpcl", "toll", "fastag") -> "Transport"
            combined.containsAny("amazon", "flipkart", "myntra", "ajio", "meesho", "mart", "blinkit", "zepto", "instamart", "bigbasket", "dmart", "mall") -> "Shopping"
            combined.containsAny("electricity", "water", "gas", "jio", "airtel", "vi ", "bsnl", "broadband", "wifi", "recharge", "bill") -> "Utilities"
            combined.containsAny("netflix", "spotify", "prime", "hotstar", "bookmyshow", "pvr", "inox", "movie", "game") -> "Entertainment"
            combined.containsAny("pharmacy", "apollo", "medplus", "1mg", "pharmeasy", "hospital", "clinic", "doctor", "health") -> "Healthcare"
            else -> "Others"
        }
    }

    private fun String.containsAny(vararg words: String): Boolean = words.any { this.contains(it) }

    // ─── Local Queue & Deduplication ─────────────────────────────────────────

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isAutoLogEnabled(context: Context): Boolean =
        prefs(context).getBoolean(KEY_AUTO_LOG, false)

    fun setAutoLogEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_AUTO_LOG, enabled).apply()
    }

    fun getPendingAlerts(context: Context): List<ParsedBankAlert> {
        val json = prefs(context).getString(KEY_PENDING, null) ?: return emptyList()
        val type = object : TypeToken<List<ParsedBankAlert>>() {}.type
        return try {
            Gson().fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun savePendingAlerts(context: Context, list: List<ParsedBankAlert>) {
        prefs(context).edit().putString(KEY_PENDING, Gson().toJson(list)).apply()
    }

    fun removePendingAlert(context: Context, id: String) {
        val updated = getPendingAlerts(context).filterNot { it.id == id }
        savePendingAlerts(context, updated)
    }

    fun clearPendingAlerts(context: Context) {
        savePendingAlerts(context, emptyList())
    }

    /**
     * Enqueues a parsed alert if it is not a duplicate within the 3-minute window.
     * Returns true if added, false if deduplicated.
     */
    fun enqueueIfNotDuplicate(context: Context, alert: ParsedBankAlert): Boolean {
        val gson = Gson()
        val now = System.currentTimeMillis()
        val sigType = object : TypeToken<MutableList<Pair<String, Long>>>() {}.type
        val sigJson = prefs(context).getString(KEY_RECENT_SIGS, null)
        val recent: MutableList<Pair<String, Long>> = try {
            if (sigJson != null) gson.fromJson(sigJson, sigType) ?: mutableListOf() else mutableListOf()
        } catch (_: Exception) {
            mutableListOf()
        }

        // Prune old signatures
        val active = recent.filter { now - it.second <= DEDUP_WINDOW_MS }.toMutableList()
        val sigKey = "${alert.type}_${String.format("%.2f", alert.amount)}"
        if (active.any { it.first == sigKey }) {
            return false
        }

        active.add(sigKey to now)
        prefs(context).edit().putString(KEY_RECENT_SIGS, gson.toJson(active)).apply()

        val current = getPendingAlerts(context).toMutableList()
        // Also check pending list for exact amount+type within 3 mins
        if (current.any { it.type == alert.type && abs(it.amount - alert.amount) < 0.01 && now - it.timestamp <= DEDUP_WINDOW_MS }) {
            return false
        }
        current.add(0, alert)
        savePendingAlerts(context, current.take(30))
        return true
    }
}

