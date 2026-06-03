package com.example.prog7313_poe

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers

class BadgeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val badges = liveData(Dispatchers.IO) {
        // 1. Savings and transaction count
        val totalSavings = db.transactionDao().getTotalSavings(userId) ?: 0.0
        val totalTransactions = db.transactionDao().getExpenseCount(userId)

        // 2. Budget data from SharedPreferences
        val prefs = getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val budgetCount = prefs.getInt("budget_count", 0)
        val monthsWithinBudget = prefs.getInt("months_within_budget", 0)

        // 3. Streak
        val expenseDates = db.transactionDao().getExpenseDates(userId)
        val streakDays = StreakManager.calculateStreak(expenseDates)

        // 4. Spending control (no‑spend days)
        val noSpendDays = SpendingControlTracker.getNoSpendDays(expenseDates)
        val noSpendConsecutive = SpendingControlTracker.getConsecutiveNoSpendDays(expenseDates)

        // 5. Goals reached (from SharedPreferences)
        val goalsReached = prefs.getInt("goals_reached", 0)

        // Generate badges
        val badgesList = BadgeManager().getBadges(
            totalSavings = totalSavings,
            budgetCount = budgetCount,
            monthsWithinBudget = monthsWithinBudget,
            streakDays = streakDays,
            totalTransactions = totalTransactions,
            noSpendDays = noSpendDays,
            noSpendConsecutive = noSpendConsecutive,
            goalsReached = goalsReached
        )

        emit(badgesList)
    }
}