package com.ysfyazilim.bagimlilik.ui.screens.focus

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun FocusScreen() {
    var timeLeft by remember { mutableIntStateOf(25 * 60) } // 25 Dakika
    var isRunning by remember { mutableStateOf(false) }
    val totalTime = 25 * 60

    LaunchedEffect(isRunning) {
        while (isRunning && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0) isRunning = false
    }

    val progress = timeLeft.toFloat() / totalTime

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Derin Odaklanma",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Telefonu bir kenara bırak ve ana odaklan.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.weight(1f))

        // Geri Sayım Görseli
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(250.dp)) {
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.1f),
                    style = Stroke(width = 12.dp.toPx())
                )
                drawArc(
                    color = if (timeLeft < 60) Color.Red else Color(0xFF52B788),
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val minutes = timeLeft / 60
                val seconds = timeLeft % 60
                val minStr = if (minutes < 10) "0$minutes" else "$minutes"
                val secStr = if (seconds < 10) "0$seconds" else "$seconds"
                
                Text(
                    text = "$minStr:$secStr",
                    style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(text = "KALAN SÜRE", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { isRunning = !isRunning },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) Color.Red.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = if (isRunning) "Durdur" else "BAŞLAT",
                color = if (isRunning) Color.Red else Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}