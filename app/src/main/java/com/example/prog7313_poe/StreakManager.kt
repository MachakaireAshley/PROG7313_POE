package com.example.prog7313_poe

import java.util.*
import kotlin.math.abs

object StreakManager {
    fun calculateStreak(transactionDates: List<Date>): Int {
        if (transactionDates.isEmpty()) return 0
        val sortedDates = transactionDates.map { getDateWithoutTime(it) }.distinct().sorted()
        var streak = 1
        var currentStreak = 1
        for (i in 1 until sortedDates.size) {
            val diff = abs(sortedDates[i].time - sortedDates[i-1].time) / (24 * 60 * 60 * 1000)
            if (diff == 1L) {
                currentStreak++
                if (currentStreak > streak) streak = currentStreak
            } else if (diff > 1) {
                currentStreak = 1
            }
        }
        return streak
    }

    private fun getDateWithoutTime(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }
}