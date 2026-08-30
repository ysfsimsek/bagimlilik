package com.opendrip.bagimlilik.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.opendrip.bagimlilik.data.repository.AuthRepository
import com.opendrip.bagimlilik.data.repository.JournalRepository
import com.opendrip.bagimlilik.utils.UsageStatsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun ChatScreen() {
    val context = LocalContext.current
    val authRepo = remember { AuthRepository(context) }
    val journalRepo = remember { JournalRepository(context) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var isLoading by remember { mutableStateOf(false) }

    val apiKey = authRepo.getApiKey()

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        Text("Baksı Bilge Koç", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text("Yapay zeka ile bağımlılık üzerine sohbet et.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        if (apiKey.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Lütfen ayarlardan Gemini API Key giriniz.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg)
                }
                if (isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Bir şey sor...") },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMsg = inputText
                            messages.add(ChatMessage(userMsg, true))
                            inputText = ""
                            isLoading = true

                            scope.launch {
                                listState.animateScrollToItem(messages.size - 1)
                                val response = callGemini(userMsg, authRepo, journalRepo, context)
                                messages.add(ChatMessage(response, false))
                                isLoading = false
                                listState.animateScrollToItem(messages.size - 1)
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Gönder", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart) {
        Surface(
            color = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (msg.isUser) 16.dp else 0.dp,
                bottomEnd = if (msg.isUser) 0.dp else 16.dp
            )
        ) {
            Text(text = msg.text, modifier = Modifier.padding(12.dp), color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

suspend fun callGemini(
    userMessage: String,
    authRepo: AuthRepository,
    journalRepo: JournalRepository,
    context: android.content.Context
): String = withContext(Dispatchers.IO) {
    val apiKey = authRepo.getApiKey()
    val todayStats = UsageStatsProvider.getTodayTotalScreenTime(context)
    val journalCount = journalRepo.getAllEntries().size
    val level = authRepo.getLevel()
    val xp = authRepo.getXP() % 100

    val systemPromptText = "Sen Baksı adlı ekran bağımlılığı ile mücadele uygulamasının bilge koçusun. " +
            "Kullanıcıya motive edici, nazik ve bilimsel temelli tavsiyeler ver. " +
            "Sadece Türkçe konuş."

    val fullUserMessage = "$userMessage\n\n[Kullanıcı Verileri: Bugünkü Ekran Süresi: ${UsageStatsProvider.formatMillisToText(todayStats)}, " +
            "Günlük Not Sayısı: $journalCount, Seviye: $level, XP: $xp/100]"

    try {
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            connectTimeout = 10000
            readTimeout = 10000
        }

        val jsonBody = JSONObject().apply {
            // System instruction API standardına uygun eklendi
            put("system_instruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPromptText) })
                })
            })
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", fullUserMessage) })
                    })
                })
            })
        }

        conn.outputStream.use { it.write(jsonBody.toString().toByteArray(Charsets.UTF_8)) }

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            jsonResponse.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } else {
            val errorResponse = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Bilinmeyen hata"
            "Hata ($responseCode): API yanıt vermedi. Details: $errorResponse"
        }
    } catch (e: Exception) {
        "Hata: ${e.localizedMessage ?: "Bağlantı kurulamadı."}"
    }
}