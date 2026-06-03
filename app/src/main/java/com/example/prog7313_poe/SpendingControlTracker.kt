package com.example.prog7313_poe

import java.util.*

object SpendingControlTracker {

    fun getNoSpendDays(expenseDates: List<Date>): Int {
        val expenseDateSet = expenseDates.map { getDateWithoutTime(it) }.toSet()
        val cal = Calendar.getInstance()
        val end = Date()
        cal.time = end
        cal.add(Calendar.DAY_OF_YEAR, -30)
        val start = cal.time

        var count = 0
        var current = start
        while (current <= end) {
            if (!expenseDateSet.contains(getDateWithoutTime(current))) {
                count++
            }
            cal.time = current
            cal.add(Calendar.DAY_OF_YEAR, 1)
            current = cal.time
        }
        return count
    }

    fun getConsecutiveNoSpendDays(expenseDates: List<Date>): Int {
        val expenseDateSet = expenseDates.map { getDateWithoutTime(it) }.toSet()
        var maxStreak = 0
        var currentStreak = 0
        val cal = Calendar.getInstance()
        var current = Date()
        // check last 60 days
        for (i in 0..60) {
            if (!expenseDateSet.contains(getDateWithoutTime(current))) {
                currentStreak++
                if (currentStreak > maxStreak) maxStreak = currentStreak
            } else {
                currentStreak = 0
            }
            cal.time = current
            cal.add(Calendar.DAY_OF_YEAR, -1)
            current = cal.time
        }
        return maxStreak
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