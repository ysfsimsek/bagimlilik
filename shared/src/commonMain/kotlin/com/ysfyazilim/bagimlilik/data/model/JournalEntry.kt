package com.opendrip.bagimlilik.data.model

import java.util.Date

data class JournalEntry(
    val id: String = "",
    val date: Long = System.currentTimeMillis(),
    val content: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val mood: String = "Mutlu" // Mutlu, Nötr, Zorlanıyor, Başarılı
)
