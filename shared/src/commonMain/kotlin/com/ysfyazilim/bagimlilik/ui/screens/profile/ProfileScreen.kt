package com.opendrip.bagimlilik.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.opendrip.bagimlilik.data.repository.AuthRepository
import com.opendrip.bagimlilik.ui.theme.LocalThemeIsDark
import android.app.Activity
import android.content.Context
import androidx.compose.ui.window.Dialog

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val scrollState = rememberScrollState()
    val isDark = LocalThemeIsDark.current
    
    var showTargetDialog by remember { mutableStateOf(false) }
    var showApiDialog by remember { mutableStateOf(false) }
    var currentTarget by remember { mutableLongStateOf(authRepository.getTargetTime()) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    if (showTargetDialog) {
        TargetTimeDialog(
            currentHours = (currentTarget / (1000 * 60 * 60)).toInt(),
            onDismiss = { showTargetDialog = false },
            onSave = { newHours ->
                val newMillis = newHours * 60 * 60 * 1000L
                authRepository.setTargetTime(newMillis)
                currentTarget = newMillis
                showTargetDialog = false
            }
        )
    }

    if (showApiDialog) {
        ApiKeyDialog(
            currentKey = authRepository.getApiKey(),
            onDismiss = { showApiDialog = false },
            onSave = { newKey ->
                authRepository.setApiKey(newKey)
                showApiDialog = false
            }
        )
    }

    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = authRepository.getUserName(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Baksı Üyesi",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(30.dp))

        SettingSwitch(
            title = "Koyu Tema", 
            icon = Icons.Default.DarkMode, 
            checked = isDark.value,
            onCheckedChange = { 
                isDark.value = it
                context.getSharedPreferences("baksi_settings", Context.MODE_PRIVATE).edit().putBoolean("is_dark", it).apply()
            }
        )
        
        SettingItem(
            title = "Günlük Hedef Süre", 
            icon = Icons.Default.Timer, 
            value = "${currentTarget / (1000 * 60 * 60)} Saat",
            onClick = { showTargetDialog = true }
        )

        SettingItem(
            title = "Yapay Zeka API Anahtarı", 
            icon = Icons.Default.Key, 
            onClick = { showApiDialog = true }
        )

        SettingItem(
            title = "Gizlilik Politikası", 
            icon = Icons.Default.Security,
            onClick = { showPrivacyDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))
        
        Button(
            onClick = { 
                authRepository.logout()
                (context as? Activity)?.recreate() 
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(text = "Verileri Sıfırla ve İsim Değiştir", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ApiKeyDialog(currentKey: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var key by remember { mutableStateOf(currentKey) }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Gemini API Anahtarı", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("API Key") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onSave(key) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Kaydet")
                }
            }
        }
    }
}

@Composable
fun TargetTimeDialog(currentHours: Int, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var sliderValue by remember { mutableFloatStateOf(currentHours.toFloat()) }
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hedef Süre Belirle", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("${sliderValue.toInt()} Saat", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 1f..12f,
                    steps = 10
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { onSave(sliderValue.toInt()) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Kaydet")
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxHeight(0.8f)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Gizlilik Politikası", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f)) {
                    Text(
                        text = """
                            Baksı Uygulaması Gizlilik Politikası
                            
                            1. Veri Toplama: Baksı, sadece ekran süresi istatistiklerinizi ve uygulama içi günlüklerinizi yerel olarak telefonunuzda saklar.
                            
                            2. Kullanım Erişimi: Uygulama, ekran sürenizi hesaplamak için Android 'Kullanım Erişimi' iznine ihtiyaç duyar. Bu veriler hiçbir sunucuya gönderilmez.
                            
                            3. Kişisel Bilgiler: İsim belirleme sırasında alınan bilgi sadece cihazda profilinizi kişiselleştirmek için kullanılır.
                            
                            4. Veri Güvenliği: Tüm verileriniz cihazınızın güvenli alanında saklanmaktadır. "Çıkış Yap" seçeneği ile tüm verilerinizi cihazdan silebilirsiniz.
                            
                            5. İletişim: Sorularınız için simsekay06@gmail.com üzerinden bize ulaşabilirsiniz.
                        """.trimIndent(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Kapat")
                }
            }
        }
    }
}

@Composable
fun SettingSwitch(title: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 12.dp))
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun SettingItem(title: String, icon: ImageVector, value: String? = null, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 12.dp))
            }
            if (value != null) {
                Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}
