package com.opendrip.bagimlilik.data.repository

import android.content.Context
import android.content.SharedPreferences

class AuthRepository(context: Context) {
    private val authPrefs: SharedPreferences = context.getSharedPreferences("baksi_auth", Context.MODE_PRIVATE)
    private val journalPrefs: SharedPreferences = context.getSharedPreferences("baksi_prefs", Context.MODE_PRIVATE)
    private val achievementPrefs: SharedPreferences = context.getSharedPreferences("baksi_achievements", Context.MODE_PRIVATE)
    private val settingsPrefs: SharedPreferences = context.getSharedPreferences("baksi_settings", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = authPrefs.getBoolean("is_logged_in", false)

    fun login(email: String, name: String) {
        authPrefs.edit().apply {
            putBoolean("is_logged_in", true)
            putString("user_email", email)
            putString("user_name", name)
            if (getTargetTime() == 0L) putLong("target_time", 4 * 60 * 60 * 1000L)
            apply()
        }
    }

    fun logout() {
        authPrefs.edit().clear().apply()
        journalPrefs.edit().clear().apply()
        achievementPrefs.edit().clear().apply()
        settingsPrefs.edit().clear().apply()
    }

    fun getUserName(): String = authPrefs.getString("user_name", "Kullanıcı") ?: "Kullanıcı"
    fun getUserEmail(): String = authPrefs.getString("user_email", "") ?: ""

    fun getTargetTime(): Long = authPrefs.getLong("target_time", 4 * 60 * 60 * 1000L)
    fun setTargetTime(millis: Long) = authPrefs.edit().putLong("target_time", millis).apply()

    fun getXP(): Int = authPrefs.getInt("user_xp", 0)
    fun addXP(amount: Int) {
        val currentXP = getXP()
        authPrefs.edit().putInt("user_xp", currentXP + amount).apply()
    }

    fun getLevel(): Int {
        val xp = getXP()
        return (xp / 100) + 1
    }

    fun isSportTaskDoneToday(): Boolean {
        val lastDate = authPrefs.getString("last_sport_date", "")
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        return lastDate == today
    }

    fun completeSportTask() {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        authPrefs.edit().putString("last_sport_date", today).apply()
        addXP(50)
    }

    // Gemini API Key
    fun getApiKey(): String = authPrefs.getString("gemini_api_key", "") ?: ""
    fun setApiKey(key: String) = authPrefs.edit().putString("gemini_api_key", key).apply()
}
