package com.ysfyazilim.bagimlilik.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class AppUsageData(val name: String, val timeText: String, val progress: Float)

@Composable
fun StatsScreen() {
    val scrollState = rememberScrollState()
    val totalWeeklyMillis by remember { mutableLongStateOf(14 * 3600 * 1000L) }
    
    val topApps = remember {
        listOf(
            AppUsageData("Sosyal Medya", "2 Saat 30 Dk", 0.8f),
            AppUsageData("Oyunlar", "1 Saat 15 Dk", 0.4f),
            AppUsageData("Tarayıcı", "45 Dk", 0.25f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text(
            text = "İstatistikler",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        DigitalCoachCard(totalWeeklyMillis)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatSummaryCard(
                title = "Bu Hafta",
                value = "14 Saat",
                subValue = "Toplam Kullanım",
                modifier = Modifier.weight(1f)
            )
            StatSummaryCard(
                title = "Ortalama",
                value = "2 Saat",
                subValue = "Günlük Ortalama",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "En Çok Kullanılanlar",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))

        topApps.forEach { app ->
            AppUsageItem(
                name = app.name,
                time = app.timeText,
                progress = app.progress,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun DigitalCoachCard(weeklyMillis: Long) {
    val weeklyHours = weeklyMillis / (1000 * 60 * 60)
    val coachMessage = when {
        weeklyHours > 40 -> "Dijital dünyada çok vakit geçiriyorsun. Bu hafta odağını dış dünyaya çevirmelisin."
        weeklyHours > 20 -> "Dengeli bir ilerleme sergiliyorsun. Sosyal medya süreni %10 azaltarak seviye atlayabilirsin."
        else -> "Mükemmel! Dijital disiplinin harika görünüyor. Bu istikrar seni zirveye taşır."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Baksı Akıllı Koç", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(coachMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
    }
}

@Composable
fun StatSummaryCard(title: String, value: String, subValue: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = subValue, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun AppUsageItem(name: String, time: String, progress: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(text = time, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}