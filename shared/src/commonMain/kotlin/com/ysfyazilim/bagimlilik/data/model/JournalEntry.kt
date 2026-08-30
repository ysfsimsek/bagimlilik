package com.opendrip.bagimlilik.data.model

data class JournalEntry(
    val id: String = "",
    val date: Long = 0L,
    val content: String,
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val mood: String = "Mutlu" // Mutlu, Nötr, Zorlanıyor, Başarılı
)