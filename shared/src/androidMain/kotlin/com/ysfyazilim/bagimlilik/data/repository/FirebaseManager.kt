package com.opendrip.bagimlilik.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.opendrip.bagimlilik.utils.AppUsageInfo
import com.opendrip.bagimlilik.utils.UsageStatsProvider

object FirebaseManager {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore get() = FirebaseFirestore.getInstance()

    fun registerUser(
        email: String,
        pass: String,
        userName: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid ?: return@addOnSuccessListener
                val userData = hashMapOf(
                    "userName" to userName,
                    "dailyTime" to "0s 0dk",
                    "level" to 1,
                    "xp" to 0,
                    "topApps" to emptyList<Map<String, String>>(),
                    "unlockedAchievements" to emptyList<String>()
                )
                db.collection("users").document(userId)
                    .set(userData)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Firestore error") }
            }
            .addOnFailureListener { e -> onError(e.message ?: "Auth error") }
    }

    fun loginUser(
        email: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Login error") }
    }

    fun syncBaksiData(
        dailyTimeStr: String,
        userLevel: Int,
        userXp: Int,
        topAppsList: List<AppUsageInfo>
    ) {
        val userId = auth.currentUser?.uid ?: return
        
        // Uygulama listesini Firestore'a gönderirken açık Map yapısına dönüştür:
        // Web panelinin verileri doğru görebilmesi için anahtarları sabitleştiriyoruz.
        val formattedTopApps = topAppsList.map { app ->
            hashMapOf(
                "name" to app.name,
                "duration" to UsageStatsProvider.formatMillisToText(app.timeMillis)
            )
        }
        
        val updateData = hashMapOf(
            "dailyTime" to dailyTimeStr,
            "level" to userLevel,
            "xp" to userXp,
            "topApps" to formattedTopApps
        )
        
        db.collection("users").document(userId)
            .update(updateData as Map<String, Any>)
    }

    fun saveAchievementsToCloud(
        unlockedAchievements: List<String>,
        totalXp: Int,
        userLevel: Int
    ) {
        val userId = auth.currentUser?.uid ?: return
        val updateData = hashMapOf(
            "unlockedAchievements" to unlockedAchievements,
            "xp" to totalXp,
            "level" to userLevel
        )
        db.collection("users").document(userId)
            .update(updateData as Map<String, Any>)
    }

    fun loadUserDataFromCloud(
        onSuccess: (achievements: List<String>, xp: Int, level: Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val achievements = doc.get("unlockedAchievements") as? List<String> ?: emptyList()
                    val xp = (doc.getLong("xp") ?: 0L).toInt()
                    val level = (doc.getLong("level") ?: 1L).toInt()
                    onSuccess(achievements, xp, level)
                }
            }
            .addOnFailureListener { e -> onError(e.message ?: "Load error") }
    }
}
