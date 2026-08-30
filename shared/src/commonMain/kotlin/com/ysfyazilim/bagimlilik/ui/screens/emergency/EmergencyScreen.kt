package com.opendrip.bagimlilik.ui.screens.emergency

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmergencyScreen(onClose: () -> Unit) {
    var isBreathing by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "Breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ScaleAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF081C15), Color(0xFF1B4332))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Kapat", tint = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = if (!isBreathing) "Kriz Anında Buradayız" else "Nefes Al ve Ver",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (!isBreathing) "Sosyal medya isteği geldiğinde sadece bu butona odaklan." 
                   else "Çemberle birlikte derin nefes al...",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Nefes Çemberi
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(if (isBreathing) scale else 1f)
                .background(
                    color = if (isBreathing) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) 
                            else Color(0xFFD00036).copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { isBreathing = !isBreathing },
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isBreathing) MaterialTheme.colorScheme.primary else Color(0xFFD00036)
                )
            ) {
                Text(
                    text = if (isBreathing) "Dur" else "BAŞLAT",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.weight(1.2f))

        if (!isBreathing) {
            Text(
                text = "Unutma, bu sadece geçici bir istek.\nSen bundan daha güçlüsün.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
