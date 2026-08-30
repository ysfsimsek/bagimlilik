package com.opendrip.bagimlilik.utils

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import java.util.*

data class AppUsageInfo(
    val name: String,
    val packageName: String,
    val timeMillis: Long,
    val icon: android.graphics.drawable.Drawable? = null
)

object UsageStatsProvider {

    fun getTodayTotalScreenTime(context: Context): Long {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
        return stats.sumOf { it.totalTimeInForeground }
    }

    fun getTopAppsUsage(context: Context, limit: Int = 5): List<AppUsageInfo> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val pm = context.packageManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, calendar.timeInMillis, System.currentTimeMillis())
        
        return stats
            .filter { it.totalTimeInForeground > 0 }
            .sortedByDescending { it.totalTimeInForeground }
            .take(limit)
            .map {
                val appName = try {
                    pm.getApplicationLabel(pm.getApplicationInfo(it.packageName, 0)).toString()
                } catch (e: Exception) {
                    it.packageName
                }
                AppUsageInfo(appName, it.packageName, it.totalTimeInForeground)
            }
    }

    fun getWeeklyUsageData(context: Context): List<Pair<String, Float>> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val result = mutableListOf<Pair<String, Float>>()
        val days = listOf("Paz", "Pzt", "Sal", "Çar", "Per", "Cum", "Cmt")
        
        for (i in 6 downTo 0) {
            val dayCalendar = Calendar.getInstance()
            dayCalendar.add(Calendar.DAY_OF_YEAR, -i)
            dayCalendar.set(Calendar.HOUR_OF_DAY, 0)
            dayCalendar.set(Calendar.MINUTE, 0)
            dayCalendar.set(Calendar.SECOND, 0)
            val start = dayCalendar.timeInMillis
            
            dayCalendar.set(Calendar.HOUR_OF_DAY, 23)
            dayCalendar.set(Calendar.MINUTE, 59)
            dayCalendar.set(Calendar.SECOND, 59)
            val end = dayCalendar.timeInMillis

            val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end)
            val dayTotal = stats.sumOf { it.totalTimeInForeground }
            
            val dayName = days[dayCalendar.get(Calendar.DAY_OF_WEEK) - 1]
            val hours = dayTotal / (1000f * 60 * 60)
            result.add(dayName to hours)
        }
        return result
    }

    fun formatMillisToText(millis: Long): String {
        val hours = millis / (1000 * 60 * 60)
        val minutes = (millis / (1000 * 60)) % 60
        return if (hours > 0) "${hours}s ${minutes}dk" else "${minutes}dk"
    }
}
