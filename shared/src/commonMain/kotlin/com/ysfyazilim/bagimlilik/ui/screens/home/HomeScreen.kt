package com.opendrip.bagimlilik.ui.screens.home

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.opendrip.bagimlilik.data.repository.AuthRepository
import com.opendrip.bagimlilik.data.repository.FirebaseManager
import com.opendrip.bagimlilik.ui.components.ModernProgressBar
import com.opendrip.bagimlilik.ui.screens.emergency.EmergencyScreen
import com.opendrip.bagimlilik.ui.screens.focus.FocusScreen
import com.opendrip.bagimlilik.utils.UsageStatsProvider
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scrollState = rememberScrollState()
    
    var showEmergency by remember { mutableStateOf(false) }
    var showMeditation by remember { mutableStateOf(false) }
    var showFocus by remember { mutableStateOf(false) }
    var hasUsagePermission by remember { mutableStateOf(checkUsageStatsPermission(context)) }
    var todayScreenTime by remember { mutableLongStateOf(0L) }
    
    var userLevel by remember { mutableIntStateOf(authRepository.getLevel()) }
    var userXP by remember { mutableIntStateOf(authRepository.getXP()) }
    var targetTimeMillis by remember { mutableLongStateOf(authRepository.getTargetTime()) }
    var isSportDone by remember { mutableStateOf(authRepository.isSportTaskDoneToday()) }

    val userName = authRepository.getUserName()

    LaunchedEffect(Unit) {
        while(true) {
            hasUsagePermission = checkUsageStatsPermission(context)
            if (hasUsagePermission) {
                val time = UsageStatsProvider.getTodayTotalScreenTime(context)
                todayScreenTime = time
                
                // Firebase Sync
                val timeStr = UsageStatsProvider.formatMillisToText(time)
                val topApps = UsageStatsProvider.getTopAppsUsage(context)
                FirebaseManager.syncBaksiData(timeStr, userLevel, userXP, topApps)
            }
            targetTimeMillis = authRepository.getTargetTime()
            userLevel = authRepository.getLevel()
            userXP = authRepository.getXP()
            delay(5.seconds)
        }
    }

    if (showEmergency) {
        EmergencyScreen(onClose = { showEmergency = false })
    } else if (showMeditation) {
        MeditationScreen(onClose = { showMeditation = false })
    } else if (showFocus) {
        FocusScreen(onClose = { showFocus = false })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Hoş Geldin, $userName",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            )
            
            LevelMiniCard(level = userLevel, xp = userXP)

            Spacer(modifier = Modifier.height(24.dp))

            if (!hasUsagePermission) {
                PermissionWarningCard {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Bugünkü Ekran Süren", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val progress = (todayScreenTime.toFloat() / targetTimeMillis).coerceIn(0f, 1f)
                    ModernProgressBar(
                        progress = progress, 
                        displayText = UsageStatsProvider.formatMillisToText(todayScreenTime)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    val remainingTime = targetTimeMillis - todayScreenTime
                    Text(
                        text = if (remainingTime > 0) "Hedefine ulaşmana ${UsageStatsProvider.formatMillisToText(remainingTime)} kaldı." else "Hedef süreni aştın!", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = if (remainingTime > 0) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            val pushups = 10 + (userLevel * 2)
            val situps = 15 + (userLevel * 3)
            SportTaskCard(
                task = "$pushups Şınav ve $situps Mekik", 
                isCompleted = isSportDone,
                onComplete = {
                    authRepository.completeSportTask()
                    isSportDone = true
                    userXP = authRepository.getXP()
                    userLevel = authRepository.getLevel()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
            DailyQuoteCard(quote = "Dijital dünya bir araçtır, efendiniz değil.", author = "Baksı Ekibi")
            
            Spacer(modifier = Modifier.height(24.dp))
            WeeklyChallengeCard()

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Hızlı Erişim", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickActionButton(title = "Odaklan", icon = Icons.Default.Timer, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f), onClick = { showFocus = true })
                QuickActionButton(title = "Keşfet", icon = Icons.Default.AutoAwesome, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f), onClick = { showMeditation = true })
                QuickActionButton(title = "Acil", icon = Icons.Default.NotificationsActive, color = Color(0xFFD00036), modifier = Modifier.weight(1f), onClick = { showEmergency = true })
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun LevelMiniCard(level: Int, xp: Int) {
    val progress = (xp % 100).toFloat() / 100f
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("⚡ SEVİYE $level", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("${xp % 100}/100 XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun WeeklyChallengeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Haftalık Meydan Okuma", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("Pazara kadar 3 gün 2 saatin altında kal!", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun SportTaskCard(task: String, isCompleted: Boolean, onComplete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
        )
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Günün Spor Görevi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                Text(text = task, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
            if (!isCompleted) {
                Button(onClick = onComplete, shape = RoundedCornerShape(12.dp)) {
                    Text("BİTİR")
                }
            } else {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
fun MeditationScreen(onClose: () -> Unit) {
    val phrases = listOf(
        "Her şey kontrolünde...",
        "Zihnin bir gökyüzü, düşünceler ise bulutlar.",
        "Sadece şu ana odaklan.",
        "Ekranın ötesinde koca bir dünya var.",
        "Kendine vakit ayırdığın için teşekkür et.",
        "Sen bu arzudan daha güçlüsün.",
        "Derin bir nefes al ve bırak."
    )
    var currentPhraseIndex by remember { mutableIntStateOf(0) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while(true) {
            delay(4000)
            visible = false
            delay(500)
            currentPhraseIndex = (currentPhraseIndex + 1) % phrases.size
            visible = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Zihin Keşfi", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(100.dp))
        
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(1000)) + shrinkVertically()
        ) {
            Text(
                text = phrases[currentPhraseIndex],
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    textAlign = TextAlign.Center
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Spacer(modifier = Modifier.height(100.dp))
        Button(onClick = onClose, shape = RoundedCornerShape(16.dp)) { Text("Farkındalığı Tamamla") }
    }
}

private fun checkUsageStatsPermission(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
    } else {
        appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

@Composable
fun PermissionWarningCard(onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Erişim İzni Eksik", style = MaterialTheme.typography.titleMedium)
            Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("İzin Ver") }
        }
    }
}

@Composable
fun DailyQuoteCard(quote: String, author: String) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
        Box(modifier = Modifier.background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)))).padding(24.dp)) {
            Column {
                Text(text = "“$quote”", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                Text(text = "- $author", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
fun QuickActionButton(title: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(90.dp), shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f))) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}
