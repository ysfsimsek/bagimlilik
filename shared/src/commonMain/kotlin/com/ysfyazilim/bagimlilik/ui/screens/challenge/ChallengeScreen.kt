package com.ysfyazilim.bagimlilik.ui.screens.challenge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

data class UIJournalEntry(
    val id: String,
    val content: String,
    val mood: String,
    val dateText: String
)

@Composable
fun ChallengeScreen() {
    var journalEntries by remember { mutableStateOf(listOf<UIJournalEntry>()) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        }
    ) { paddingValues ->
        if (showAddDialog) {
            AddEntryDialog(
                onDismiss = { showAddDialog = false },
                onSave = { content, mood ->
                    val newEntry = UIJournalEntry(
                        id = "entry_${journalEntries.size + 1}",
                        content = content,
                        mood = mood,
                        dateText = "Bugün"
                    )
                    journalEntries = journalEntries + newEntry
                    showAddDialog = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Mücadele Defterim",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            WeeklyCalendarView()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Geçmiş Kayıtlar",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (journalEntries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Henüz kayıt eklenmemiş.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(journalEntries) { entry ->
                        JournalEntryCard(entry)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEntryDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf("Başarılı") }
    val moods = listOf("Başarılı", "Zorlanıyor", "Nötr")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Yeni Kayıt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Bugün neler başardın?") },
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Duygu Durumun", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    moods.forEach { mood ->
                        FilterChip(
                            selected = selectedMood == mood,
                            onClick = { selectedMood = mood },
                            label = { Text(mood) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { if (content.isNotBlank()) onSave(content, selectedMood) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Kaydet")
                }
            }
        }
    }
}

@Composable
fun WeeklyCalendarView() {
    val days = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
    val dates = (20..26).toList()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(days.size) { index ->
            val isSelected = index == 4
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = days[index],
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = dates[index].toString(),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun JournalEntryCard(entry: UIJournalEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = entry.dateText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when(entry.mood) {
                        "Başarılı" -> Color(0xFF2D6A4F).copy(alpha = 0.1f)
                        "Zorlanıyor" -> Color(0xFFFFB703).copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = entry.mood,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = when(entry.mood) {
                            "Başarılı" -> Color(0xFF2D6A4F)
                            "Zorlanıyor" -> Color(0xFFE67E22)
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = entry.content, style = MaterialTheme.typography.bodyLarge)
        }
    }
}