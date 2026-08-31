package com.ysfyazilim.bagimlilik.ui.screens.home

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HomeScreen() {
    val scrollState = rememberScrollState()
    
    var showMeditation by remember { mutableStateOf(false) }
    var todayScreenTime by remember { mutableLongStateOf(0L) }
    
    var userLevel by remember { mutableIntStateOf(1) }
    var userXP by remember { mutableIntStateOf(20) }
    var targetTimeMillis by remember { mutableLongStateOf(4 * 3600 * 1000L) }
    var isSportDone by remember { mutableStateOf(false) }

    val userName = "Kullanıcı"

    if (showMeditation) {
        MeditationScreen(onClose = { showMeditation = false })
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
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Hedefine ulaşmana zaman var.", 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                    isSportDone = true
                    userXP += 10
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
                QuickActionButton(title = "Keşfet", icon = Icons.Default.AutoAwesome, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f), onClick = { showMeditation = true })
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