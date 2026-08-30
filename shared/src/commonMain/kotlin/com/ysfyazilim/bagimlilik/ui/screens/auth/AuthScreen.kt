package com.opendrip.bagimlilik.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.opendrip.bagimlilik.data.repository.AuthRepository
import com.opendrip.bagimlilik.data.repository.FirebaseManager
import com.opendrip.bagimlilik.data.repository.AchievementRepository

@Composable
fun AuthScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val achRepo = remember { AchievementRepository(context) }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isSignUp) "Hesap Oluştur" else "Hoş Geldiniz",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (isSignUp) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Ad Soyad") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta Adresi") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Şifre") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        isLoading = true
                        if (isSignUp) {
                            FirebaseManager.registerUser(email, password, name, {
                                authRepository.login(email, name)
                                isLoading = false
                                onLoginSuccess()
                            }, { error ->
                                errorMessage = error
                                isLoading = false
                            })
                        } else {
                            FirebaseManager.loginUser(email, password, {
                                FirebaseManager.loadUserDataFromCloud({ achievements, xp, level ->
                                    // Buluttaki verileri SharedPreferences'a yaz
                                    authRepository.login(email, "Kullanıcı") // Gerçek ismi Firestore'dan çekebiliriz ama şimdilik repo metoduna sadık kalalım
                                    authRepository.addXP(xp - authRepository.getXP()) // XP farkını ekle (repo metodu setXP yoksa)
                                    // Achievement'ları yerel hafızaya kaydet
                                    achievements.forEach { achRepo.unlock(it) }
                                    
                                    isLoading = false
                                    onLoginSuccess()
                                }, { error ->
                                    errorMessage = error
                                    isLoading = false
                                })
                            }, { error ->
                                errorMessage = error
                                isLoading = false
                            })
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = email.isNotBlank() && password.isNotBlank()
            ) {
                Text(if (isSignUp) "Kayıt Ol" else "Giriş Yap")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(if (isSignUp) "Zaten hesabınız var mı? Giriş yapın" else "Hesabınız yok mu? Kayıt olun")
        }
    }
}
