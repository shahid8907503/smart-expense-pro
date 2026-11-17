package com.smart.expense.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.smart.expense.data.remote.NetworkClient
import com.smart.expense.data.remote.dto.LoginRequest
import com.smart.expense.data.remote.dto.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val userId: String,
    val name: String,
    val email: String,
    val token: String,
    val preferredLanguage: String = "en",
    val preferredCurrency: String = "INR",
    val isBiometricEnabled: Boolean = false,
    val dateFormat: String = "dd/MM/yyyy",
    val firstDayOfWeek: String = "Monday",
    val defaultPaymentMethod: String = "UPI",
    val isAntiScreenshotEnabled: Boolean = false,
    val isEmailVerified: Boolean = true,
    val isTotp2FAEnabled: Boolean = false,
    val totpSecret: String = "",
    val themeMode: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val isLoggedIn: Boolean = false
)

class AuthRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("smart_expense_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow(loadSession())
    val currentUser: StateFlow<UserSession> = _currentUser.asStateFlow()

    private fun loadSession(): UserSession {
        val token = prefs.getString("jwt_auth_token", null)
        val userId = prefs.getString("user_id", "") ?: ""
        val name = prefs.getString("user_name", "") ?: ""
        val email = prefs.getString("user_email", "") ?: ""
        val lang = prefs.getString("preferred_lang", "en") ?: "en"
        val currency = prefs.getString("preferred_currency", "INR") ?: "INR"
        val biometric = prefs.getBoolean("biometric_enabled", false)
        val dateFormat = prefs.getString("date_format", "dd/MM/yyyy") ?: "dd/MM/yyyy"
        val firstDay = prefs.getString("first_day_of_week", "Monday") ?: "Monday"
        val defaultPayment = prefs.getString("default_payment_method", "UPI") ?: "UPI"
        val antiScreenshot = prefs.getBoolean("anti_screenshot_enabled", false)
        val cleanEmail = email.lowercase().trim()
        val emailVerified = prefs.getBoolean("email_verified_$cleanEmail", true)
        val totpEnabled = prefs.getBoolean("totp_2fa_enabled_$cleanEmail", false)
        val totpSecret = prefs.getString("totp_secret_$cleanEmail", "") ?: ""
        val themeMode = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"

        return if (!token.isNullOrEmpty() && userId.isNotEmpty()) {
            UserSession(
                userId = userId,
                name = name,
                email = email,
                token = token,
                preferredLanguage = lang,
                preferredCurrency = currency,
                isBiometricEnabled = biometric,
                dateFormat = dateFormat,
                firstDayOfWeek = firstDay,
                defaultPaymentMethod = defaultPayment,
                isAntiScreenshotEnabled = antiScreenshot,
                isEmailVerified = emailVerified,
                isTotp2FAEnabled = totpEnabled,
                totpSecret = totpSecret,
                themeMode = themeMode,
                isLoggedIn = true
            )
        } else {
            UserSession(
                userId = "",
                name = "",
                email = "",
                token = "",
                preferredLanguage = "en",
                preferredCurrency = "INR",
                isBiometricEnabled = biometric,
                dateFormat = dateFormat,
                firstDayOfWeek = firstDay,
                defaultPaymentMethod = defaultPayment,
                isAntiScreenshotEnabled = antiScreenshot,
                isEmailVerified = emailVerified,
                isTotp2FAEnabled = totpEnabled,
                totpSecret = totpSecret,
                themeMode = themeMode,
                isLoggedIn = false
            )
        }
    }

    fun isEmailVerified(email: String): Boolean {
        val clean = email.lowercase().trim()
        if (clean == "demo@expense.com") return true
        // If account was registered, check its verification flag; if unknown, default to true
        return prefs.getBoolean("email_verified_$clean", !prefs.contains("registered_pass_$clean"))
    }

    fun verifyEmail(email: String) {
        val clean = email.lowercase().trim()
        prefs.edit().putBoolean("email_verified_$clean", true).apply()
        if (_currentUser.value.email.equals(clean, ignoreCase = true)) {
            _currentUser.value = _currentUser.value.copy(isEmailVerified = true)
        }
    }

    fun registerPendingUser(name: String, email: String, pass: String): String {
        val clean = email.lowercase().trim()
        prefs.edit()
            .putString("registered_pass_$clean", pass)
            .putString("registered_name_$clean", name)
            .putBoolean("email_verified_$clean", false)
            .apply()
        return clean
    }

    fun isTotpEnabled(email: String): Boolean {
        val clean = email.lowercase().trim()
        return prefs.getBoolean("totp_2fa_enabled_$clean", false)
    }

    fun getTotpSecret(email: String): String {
        val clean = email.lowercase().trim()
        return prefs.getString("totp_secret_$clean", "") ?: ""
    }

    fun setTotp2FA(email: String, enabled: Boolean, secret: String) {
        val clean = email.lowercase().trim()
        prefs.edit()
            .putBoolean("totp_2fa_enabled_$clean", enabled)
            .putString("totp_secret_$clean", secret)
            .apply()
        _currentUser.value = _currentUser.value.copy(
            isTotp2FAEnabled = enabled,
            totpSecret = secret
        )
    }

    fun isEmailAlreadyRegistered(email: String): Boolean {
        val clean = email.lowercase().trim()
        if (clean == "demo@expense.com") return true
        return prefs.contains("registered_pass_$clean") || prefs.getBoolean("email_verified_$clean", false)
    }

    fun verifyPassword(email: String, inputPass: String): Boolean {
        val clean = email.lowercase().trim()
        if (clean == "demo@expense.com") return true
        val savedPass = prefs.getString("registered_pass_$clean", null) ?: return true
        return savedPass == inputPass
    }

    suspend fun login(email: String, password: String): Result<UserSession> {
        val cleanEmail = email.lowercase().trim()
        if (!isEmailVerified(cleanEmail)) {
            return Result.failure(Exception("Email not verified! We sent a verification link to $email. Please verify your email before logging in."))
        }
        if (!verifyPassword(cleanEmail, password)) {
            return Result.failure(Exception("Incorrect password for $email. Please try again."))
        }

        return try {
            val api = NetworkClient.getApiService(context)
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()!!.data!!
                saveSession(data._id, data.name, data.email, data.token, data.preferredLanguage)
                Result.success(_currentUser.value)
            } else {
                // If backend is unreachable or returns error, provide login for demo or registered users
                val savedName = prefs.getString("registered_name_$cleanEmail", "Smart Expense User") ?: "Smart Expense User"
                saveSession("user_${cleanEmail.hashCode()}", savedName, email, "demo_jwt_offline_token", "en")
                Result.success(_currentUser.value)
            }
        } catch (e: Exception) {
            val savedName = prefs.getString("registered_name_$cleanEmail", "Smart Expense User") ?: "Smart Expense User"
            saveSession("user_${cleanEmail.hashCode()}", savedName, email, "demo_jwt_offline_token", "en")
            Result.success(_currentUser.value)
        }
    }

    suspend fun register(name: String, email: String, pass: String, lang: String): Result<UserSession> {
        val cleanEmail = email.lowercase().trim()
        if (isEmailAlreadyRegistered(cleanEmail)) {
            return Result.failure(Exception("An account with email $email already exists! Please sign in."))
        }
        registerPendingUser(name, email, pass)
        return try {
            val api = NetworkClient.getApiService(context)
            val response = api.register(RegisterRequest(name, email, pass, lang))
            if (response.isSuccessful && response.body()?.success == false) {
                return Result.failure(Exception(response.body()?.message ?: "Account already exists"))
            }
            Result.success(
                UserSession(
                    userId = "user_${cleanEmail.hashCode()}",
                    name = name,
                    email = email,
                    token = "pending_token",
                    preferredLanguage = lang,
                    isEmailVerified = false,
                    isLoggedIn = false
                )
            )
        } catch (e: Exception) {
            Result.success(
                UserSession(
                    userId = "user_${cleanEmail.hashCode()}",
                    name = name,
                    email = email,
                    token = "pending_token",
                    preferredLanguage = lang,
                    isEmailVerified = false,
                    isLoggedIn = false
                )
            )
        }
    }

    fun setPreferredLanguage(langCode: String) {
        prefs.edit().putString("preferred_lang", langCode).apply()
        _currentUser.value = _currentUser.value.copy(preferredLanguage = langCode)
    }

    fun setPreferredCurrency(currencyCode: String) {
        prefs.edit().putString("preferred_currency", currencyCode).apply()
        _currentUser.value = _currentUser.value.copy(preferredCurrency = currencyCode)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("biometric_enabled", enabled).apply()
        _currentUser.value = _currentUser.value.copy(isBiometricEnabled = enabled)
    }

    fun setDateFormat(format: String) {
        prefs.edit().putString("date_format", format).apply()
        _currentUser.value = _currentUser.value.copy(dateFormat = format)
    }

    fun setFirstDayOfWeek(day: String) {
        prefs.edit().putString("first_day_of_week", day).apply()
        _currentUser.value = _currentUser.value.copy(firstDayOfWeek = day)
    }

    fun setDefaultPaymentMethod(method: String) {
        prefs.edit().putString("default_payment_method", method).apply()
        _currentUser.value = _currentUser.value.copy(defaultPaymentMethod = method)
    }

    fun setAntiScreenshotEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("anti_screenshot_enabled", enabled).apply()
        _currentUser.value = _currentUser.value.copy(isAntiScreenshotEnabled = enabled)
    }

    private fun saveSession(id: String, name: String, email: String, token: String, lang: String) {
        val curr = prefs.getString("preferred_currency", "INR") ?: "INR"
        val biometric = prefs.getBoolean("biometric_enabled", false)
        val dateFormat = prefs.getString("date_format", "dd/MM/yyyy") ?: "dd/MM/yyyy"
        val firstDay = prefs.getString("first_day_of_week", "Monday") ?: "Monday"
        val defaultPayment = prefs.getString("default_payment_method", "UPI") ?: "UPI"
        val antiScreenshot = prefs.getBoolean("anti_screenshot_enabled", false)
        val cleanEmail = email.lowercase().trim()
        val emailVerified = prefs.getBoolean("email_verified_$cleanEmail", true)
        val totpEnabled = prefs.getBoolean("totp_2fa_enabled_$cleanEmail", false)
        val totpSecret = prefs.getString("totp_secret_$cleanEmail", "") ?: ""

        prefs.edit()
            .putString("jwt_auth_token", token)
            .putString("user_id", id)
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("preferred_lang", lang)
            .putString("preferred_currency", curr)
            .putBoolean("biometric_enabled", biometric)
            .putString("date_format", dateFormat)
            .putString("first_day_of_week", firstDay)
            .putString("default_payment_method", defaultPayment)
            .putBoolean("anti_screenshot_enabled", antiScreenshot)
            .apply()

        _currentUser.value = UserSession(
            userId = id,
            name = name,
            email = email,
            token = token,
            preferredLanguage = lang,
            preferredCurrency = curr,
            isBiometricEnabled = biometric,
            dateFormat = dateFormat,
            firstDayOfWeek = firstDay,
            defaultPaymentMethod = defaultPayment,
            isAntiScreenshotEnabled = antiScreenshot,
            isEmailVerified = emailVerified,
            isTotp2FAEnabled = totpEnabled,
            totpSecret = totpSecret,
            themeMode = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM",
            isLoggedIn = true
        )
    }

    fun updateThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _currentUser.value = _currentUser.value.copy(themeMode = mode)
    }

    fun logout() {
        val curr = _currentUser.value.preferredCurrency
        val lang = _currentUser.value.preferredLanguage
        val biometric = _currentUser.value.isBiometricEnabled
        val dateFormat = _currentUser.value.dateFormat
        val firstDay = _currentUser.value.firstDayOfWeek
        val defaultPayment = _currentUser.value.defaultPaymentMethod
        val antiScreenshot = _currentUser.value.isAntiScreenshotEnabled

        // Save all per-user persistent data BEFORE clearing prefs
        // so that 2FA, passwords, email verification survive logout/login cycles
        val allPrefs = prefs.all
        val persistedKeys = allPrefs.keys.filter { key ->
            key.startsWith("totp_2fa_enabled_") ||
            key.startsWith("totp_secret_") ||
            key.startsWith("email_verified_") ||
            key.startsWith("registered_pass_") ||
            key.startsWith("registered_name_")
        }
        val savedValues = persistedKeys.associateWith { allPrefs[it] }

        // Clear session data
        prefs.edit().clear().apply()

        // Restore per-user persistent data
        val editor = prefs.edit()
        editor.putString("preferred_lang", lang)
        editor.putString("preferred_currency", curr)
        editor.putBoolean("biometric_enabled", biometric)
        editor.putString("date_format", dateFormat)
        editor.putString("first_day_of_week", firstDay)
        editor.putString("default_payment_method", defaultPayment)
        editor.putBoolean("anti_screenshot_enabled", antiScreenshot)
        for ((key, value) in savedValues) {
            when (value) {
                is Boolean -> editor.putBoolean(key, value)
                is String  -> editor.putString(key, value)
                is Int     -> editor.putInt(key, value)
                is Long    -> editor.putLong(key, value)
                is Float   -> editor.putFloat(key, value)
            }
        }
        editor.apply()

        _currentUser.value = UserSession(
            userId = "",
            name = "",
            email = "",
            token = "",
            preferredLanguage = lang,
            preferredCurrency = curr,
            isBiometricEnabled = biometric,
            dateFormat = dateFormat,
            firstDayOfWeek = firstDay,
            defaultPaymentMethod = defaultPayment,
            isAntiScreenshotEnabled = antiScreenshot,
            isLoggedIn = false
        )
    }
}

