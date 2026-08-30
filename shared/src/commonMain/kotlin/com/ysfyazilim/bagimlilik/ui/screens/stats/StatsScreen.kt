package com.opendrip.bagimlilik.ui.screens.stats

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.opendrip.bagimlilik.ui.components.SimpleBarChart
import com.opendrip.bagimlilik.utils.AppUsageInfo
import com.opendrip.bagimlilik.utils.UsageStatsProvider
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun StatsScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    var weeklyData by remember { mutableStateOf(emptyList<Pair<String, Float>>()) }
    var topApps by remember { mutableStateOf(emptyList<AppUsageInfo>()) }
    var totalWeeklyMillis by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while(true) {
            val data = UsageStatsProvider.getWeeklyUsageData(context)
            weeklyData = data
            topApps = UsageStatsProvider.getTopAppsUsage(context)
            totalWeeklyMillis = (data.sumOf { it.second.toDouble() } * 1000 * 60 * 60).toLong()
            delay(10.seconds)
        }
    }

    // Paylaşma fonksiyonu
    val shareStats = {
        val weeklyHours = totalWeeklyMillis / (1000 * 60 * 60)
        val coachMessage = when {
            weeklyHours > 40 -> "Dijital dünyada çok vakit geçiriyorsun. Bu hafta odağını dış dünyaya çevirmelisin."
            weeklyHours > 20 -> "Dengeli bir ilerleme sergiliyorsun. Sosyal medya süreni %10 azaltarak seviye atlayabilirsin."
            else -> "Mükemmel! Dijital disiplinin harika görünüyor. Bu istikrar seni zirveye taşır."
        }

        val reportText = """
            📊 BAKSI - Dijital Denge Raporu
            ------------------------------
            📅 Bu Haftaki Toplam Kullanım: ${UsageStatsProvider.formatMillisToText(totalWeeklyMillis)}
            📈 Günlük Ortalama: ${UsageStatsProvider.formatMillisToText(totalWeeklyMillis / 7)}
            
            🚀 En Çok Kullanılan Uygulamalar:
            ${topApps.joinToString("\n") { "• ${it.name}: ${UsageStatsProvider.formatMillisToText(it.timeMillis)}" }}
            
            🤖 Akıllı Koç Notu:
            "$coachMessage"
            
            🌿 Sen de Baksı ile dijital özgürlüğüne kavuş!
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Baksı Haftalık Rapor")
            putExtra(Intent.EXTRA_TEXT, reportText)
        }
        context.startActivity(Intent.createChooser(intent, "Raporu Paylaş"))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "İstatistikler",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = shareStats) {
                Icon(Icons.Default.Share, contentDescription = "Paylaş", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        DigitalCoachCard(totalWeeklyMillis)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatSummaryCard(
                title = "Bu Hafta",
                value = UsageStatsProvider.formatMillisToText(totalWeeklyMillis),
                subValue = "Toplam Kullanım",
                modifier = Modifier.weight(1f)
            )
            StatSummaryCard(
                title = "Ortalama",
                value = UsageStatsProvider.formatMillisToText(totalWeeklyMillis / 7),
                subValue = "Günlük Ortalama",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Haftalık Kullanım (Saat)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (weeklyData.isNotEmpty()) {
                    SimpleBarChart(data = weeklyData)
                } else {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "En Çok Kullanılanlar",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (topApps.isEmpty()) {
            Text("Veri bulunamadı.", color = Color.Gray)
        } else {
            val maxTime = topApps.firstOrNull()?.timeMillis ?: 1L
            topApps.forEach { app ->
                AppUsageItem(
                    name = app.name,
                    time = UsageStatsProvider.formatMillisToText(app.timeMillis),
                    progress = app.timeMillis.toFloat() / maxTime,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
