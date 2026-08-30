package com.opendrip.bagimlilik.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.opendrip.bagimlilik.data.repository.AchievementRepository
import com.opendrip.bagimlilik.data.repository.AuthRepository
import com.opendrip.bagimlilik.data.repository.JournalRepository
import com.opendrip.bagimlilik.data.repository.FirebaseManager
import com.opendrip.bagimlilik.utils.UsageStatsProvider

data class Achievement(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val description: String,
    val xpValue: Int = 100
)

@Composable
fun AchievementsScreen() {
    val context = LocalContext.current
    val authRepo = remember { AuthRepository(context) }
    val achRepo = remember { AchievementRepository(context) }
    val journalRepo = remember { JournalRepository(context) }
    
    val journalCount = journalRepo.getAllEntries().size
    val todayTime = UsageStatsProvider.getTodayTotalScreenTime(context)
    val targetTime = authRepo.getTargetTime()
    
    val achievements = listOf(
        Achievement("1", "Merhaba", Icons.Default.Handshake, "Uygulamaya ilk adımını attın."),
        Achievement("2", "Yazar", Icons.Default.Edit, "İlk günlüğünü tamamladın."),
        Achievement("3", "İstikrar", Icons.Default.History, "3 adet günlük kaydına ulaştın."),
        Achievement("4", "Odaklanmış", Icons.Default.Timer, "Bugünkü hedefini aşmadın."),
        Achievement("5", "Erken Kuş", Icons.Default.WbSunny, "Sabah vaktinde giriş yaptın."),
        Achievement("6", "Sporcu", Icons.Default.FitnessCenter, "İlk spor görevini bitirdin."),
        Achievement("7", "Gece Kuşu", Icons.Default.DarkMode, "Koyu temayı denedin."),
        Achievement("8", "Bilge", Icons.Default.AutoStories, "Günün sözünü okudun."),
        Achievement("9", "Kararlı", Icons.Default.Psychology, "Meditasyon modunu başlattın."),
        Achievement("10", "Dengeli", Icons.Default.Balance, "Ekran süresini %20 azalttın."),
        Achievement("11", "Usta Yazar", Icons.Default.RateReview, "10 adet günlük kaydın var."),
        Achievement("12", "Hızlı Karar", Icons.Default.FlashOn, "Acil durum butonunu kullandın."),
        Achievement("13", "Profil", Icons.Default.AccountCircle, "Profil bilgilerini tamamladın."),
        Achievement("14", "Dost", Icons.Default.Groups, "Baksı'yı paylaştın."),
        Achievement("15", "Zaman Avcısı", Icons.Default.Savings, "Toplam 10 saat tasarruf ettin."),
        Achievement("16", "Efsane", Icons.Default.WorkspacePremium, "Tüm görevlerde 1 hafta istikrar sağladın.")
    )

    LaunchedEffect(Unit) {
        achRepo.unlock("1")
        if (journalCount >= 1) achRepo.unlock("2")
        if (journalCount >= 3) achRepo.unlock("3")
        if (todayTime > 0 && todayTime < targetTime) achRepo.unlock("4")
        achRepo.unlock("5") 
        if (authRepo.isSportTaskDoneToday()) achRepo.unlock("6")
        
        // Unlock durumlarını buluta yedekle (Opsiyonel periyodik yedekleme)
        val unlockedList = achievements.filter { achRepo.isUnlocked(it.id) }.map { it.id }
        FirebaseManager.saveAchievementsToCloud(unlockedList, authRepo.getXP(), authRepo.getLevel())
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(20.dp)) {
        Text(text = "Başarımlar", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        Text(text = "Rozetlerini topla ve XP kazan!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(achievements) { ach ->
                val isUnlocked = achRepo.isUnlocked(ach.id)
                val isCollected = achRepo.isCollected(ach.id)
                
                AchievementCard(
                    achievement = ach,
                    isUnlocked = isUnlocked,
                    isCollected = isCollected,
                    onCollect = {
                        achRepo.collect(ach.id)
                        authRepo.addXP(ach.xpValue)
                        
                        // Buluta anında yedekle
                        val allUnlocked = achievements.filter { achRepo.isUnlocked(it.id) }.map { it.id }
                        FirebaseManager.saveAchievementsToCloud(allUnlocked, authRepo.getXP(), authRepo.getLevel())
                    }
                )
            }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement, isUnlocked: Boolean, isCollected: Boolean, onCollect: () -> Unit) {
    var collected by remember { mutableStateOf(isCollected) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                tint = if (isUnlocked) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = achievement.title, fontWeight = FontWeight.Bold, color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else Color.Gray)
            Text(text = achievement.description, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (isUnlocked && !collected) {
                Button(onClick = { 
                    onCollect()
                    collected = true
                }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp)) {
                    Text("TOPLA", style = MaterialTheme.typography.labelSmall)
                }
            } else if (collected) {
                Text("ALINDI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            } else {
                Text("KİLİTLİ", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}
