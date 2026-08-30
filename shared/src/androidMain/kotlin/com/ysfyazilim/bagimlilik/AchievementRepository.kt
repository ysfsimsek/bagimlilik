package com.opendrip.bagimlilik.data.repository

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray

class AchievementRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("baksi_achievements", Context.MODE_PRIVATE)

    fun isUnlocked(id: String): Boolean = prefs.getBoolean("unlocked_$id", false)
    fun isCollected(id: String): Boolean = prefs.getBoolean("collected_$id", false)

    fun unlock(id: String) {
        if (!isUnlocked(id)) {
            prefs.edit().putBoolean("unlocked_$id", true).apply()
        }
    }

    fun collect(id: String) {
        prefs.edit().putBoolean("collected_$id", true).apply()
    }
}
