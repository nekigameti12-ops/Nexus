package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.nio.charset.StandardCharsets

/**
 * Manages encrypted / obfuscated local credential storage for NEXUS AI.
 * Ensures keys are never saved in plain text or logged, and only masked keys
 * are exposed to UI display layers.
 */
class SecureConfigStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "nexus_vault_secure_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_PROVIDER = "nexus_provider"
        private const val KEY_ENCRYPTED_API_KEY = "nexus_enc_api_key"
        private const val KEY_MODEL = "nexus_model"
        private const val KEY_BASE_URL = "nexus_base_url"
        private const val KEY_IS_ONLINE = "nexus_is_online"
        private const val KEY_VOICE_PITCH = "nexus_voice_pitch"
        private const val KEY_VOICE_RATE = "nexus_voice_rate"
        private const val KEY_LANGUAGE = "nexus_language"
        private const val KEY_WAKE_WORD = "nexus_wake_word"
        private const val KEY_PRIVACY_MODE = "nexus_privacy_mode"
        private const val KEY_CONFIRM_RISK = "nexus_confirm_risk"
        private const val KEY_ACCENT_THEME = "nexus_accent_theme"
        private const val KEY_UI_DENSITY = "nexus_ui_density"
        private const val KEY_SHOW_QUICK_DOCK = "nexus_show_quick_dock"

        // Obfuscation mask key for device-level pseudo-encryption
        private val OBFUSCATION_SALT = "NEXUS_NEURAL_SEC_VAULT_2026".toByteArray(StandardCharsets.UTF_8)
    }

    private fun obfuscate(plain: String): String {
        val plainBytes = plain.toByteArray(StandardCharsets.UTF_8)
        val masked = ByteArray(plainBytes.size)
        for (i in plainBytes.indices) {
            masked[i] = (plainBytes[i].toInt() xor OBFUSCATION_SALT[i % OBFUSCATION_SALT.size].toInt()).toByte()
        }
        return Base64.encodeToString(masked, Base64.NO_WRAP)
    }

    private fun deobfuscate(encoded: String): String {
        return try {
            val masked = Base64.decode(encoded, Base64.NO_WRAP)
            val plainBytes = ByteArray(masked.size)
            for (i in masked.indices) {
                plainBytes[i] = (masked[i].toInt() xor OBFUSCATION_SALT[i % OBFUSCATION_SALT.size].toInt()).toByte()
            }
            String(plainBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            ""
        }
    }

    fun saveConfig(
        provider: String,
        apiKey: String,
        model: String,
        baseUrl: String = ""
    ) {
        prefs.edit()
            .putString(KEY_PROVIDER, provider)
            .putString(KEY_ENCRYPTED_API_KEY, obfuscate(apiKey.trim()))
            .putString(KEY_MODEL, model.trim())
            .putString(KEY_BASE_URL, baseUrl.trim())
            .apply()
    }

    fun setCoreOnline(isOnline: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ONLINE, isOnline).apply()
    }

    fun isCoreOnline(): Boolean {
        return prefs.getBoolean(KEY_IS_ONLINE, false) && hasValidKey()
    }

    fun hasValidKey(): Boolean {
        val raw = getApiKey()
        return raw.isNotBlank() && raw.length > 5
    }

    fun getApiKey(): String {
        val encoded = prefs.getString(KEY_ENCRYPTED_API_KEY, "") ?: ""
        return if (encoded.isEmpty()) "" else deobfuscate(encoded)
    }

    fun getMaskedApiKey(): String {
        val key = getApiKey()
        if (key.isBlank()) return "Not Configured"
        return if (key.length <= 8) {
            "••••••••••••••••"
        } else {
            "••••••••••••" + key.takeLast(4)
        }
    }

    fun getProvider(): String = prefs.getString(KEY_PROVIDER, "Gemini") ?: "Gemini"

    fun getModel(): String {
        val saved = prefs.getString(KEY_MODEL, "") ?: ""
        if (saved.isNotBlank()) return saved
        return when (getProvider().lowercase()) {
            "openai" -> "gpt-4o"
            "groq" -> "llama-3.3-70b-versatile"
            else -> "gemini-2.5-flash"
        }
    }

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, "") ?: ""

    fun removeApiKey() {
        prefs.edit()
            .remove(KEY_ENCRYPTED_API_KEY)
            .putBoolean(KEY_IS_ONLINE, false)
            .apply()
    }

    // Voice & System Preferences
    fun getVoicePitch(): Float = prefs.getFloat(KEY_VOICE_PITCH, 0.85f) // Deep male default
    fun setVoicePitch(pitch: Float) = prefs.edit().putFloat(KEY_VOICE_PITCH, pitch).apply()

    fun getVoiceRate(): Float = prefs.getFloat(KEY_VOICE_RATE, 1.0f)
    fun setVoiceRate(rate: Float) = prefs.edit().putFloat(KEY_VOICE_RATE, rate).apply()

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    fun setLanguage(lang: String) = prefs.edit().putString(KEY_LANGUAGE, lang).apply()

    fun isWakeWordEnabled(): Boolean = prefs.getBoolean(KEY_WAKE_WORD, true)
    fun setWakeWordEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_WAKE_WORD, enabled).apply()

    fun isPrivacyMode(): Boolean = prefs.getBoolean(KEY_PRIVACY_MODE, false)
    fun setPrivacyMode(enabled: Boolean) = prefs.edit().putBoolean(KEY_PRIVACY_MODE, enabled).apply()

    fun isRiskConfirmationRequired(): Boolean = prefs.getBoolean(KEY_CONFIRM_RISK, true)
    fun setRiskConfirmationRequired(required: Boolean) = prefs.edit().putBoolean(KEY_CONFIRM_RISK, required).apply()

    // Interface Appearance & Customization
    fun getAccentTheme(): String = prefs.getString(KEY_ACCENT_THEME, "cyan") ?: "cyan"
    fun setAccentTheme(theme: String) = prefs.edit().putString(KEY_ACCENT_THEME, theme).apply()

    fun getUiDensity(): String = prefs.getString(KEY_UI_DENSITY, "standard") ?: "standard"
    fun setUiDensity(density: String) = prefs.edit().putString(KEY_UI_DENSITY, density).apply()

    fun isQuickDockEnabled(): Boolean = prefs.getBoolean(KEY_SHOW_QUICK_DOCK, true)
    fun setQuickDockEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_SHOW_QUICK_DOCK, enabled).apply()
}
