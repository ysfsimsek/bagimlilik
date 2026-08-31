package com.opendrip.bagimlilik.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.opendrip.bagimlilik.data.model.JournalEntry
import org.json.JSONArray
import org.json.JSONObject

class JournalRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("baksi_prefs", Context.MODE_PRIVATE)

    fun saveEntry(entry: JournalEntry) {
        val entries = getAllEntries().toMutableList()
        entries.add(0, entry)
        val jsonArray = JSONArray()
        entries.forEach {
            val json = JSONObject().apply {
                put("id", it.id)
                put("content", it.content)
                put("date", it.date)
                put("mood", it.mood)
            }
            jsonArray.put(json)
        }
        prefs.edit().putString("entries", jsonArray.toString()).apply()
    }

    fun getAllEntries(): List<JournalEntry> {
        val jsonString = prefs.getString("entries", null) ?: return emptyList()
        val jsonArray = JSONArray(jsonString)
        val list = mutableListOf<JournalEntry>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            list.add(
                JournalEntry(
                    id = json.getString("id"),
                    content = json.getString("content"),
                    date = json.getLong("date"),
                    mood = json.getString("mood")
                )
            )
        }
        return list
    }
}
