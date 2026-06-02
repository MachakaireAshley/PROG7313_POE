package com.example.prog7313_poe

object BudgetCalculator {
    fun getPercentageSpent(totalSpent: Double, budgetLimit: Double): Int {
        return if (budgetLimit > 0) ((totalSpent / budgetLimit) * 100).toInt().coerceIn(0, 100)
        else 0
    }

    fun getRemainingBudget(totalSpent: Double, budgetLimit: Double): Double {
        return (budgetLimit - totalSpent).coerceAtLeast(0.0)
    }

    fun getGoalStatus(totalSpent: Double, minGoal: Double, maxGoal: Double): String {
        return when {
            totalSpent < minGoal -> "Below minimum"
            totalSpent > maxGoal -> "Above maximum"
            else -> "Within budget"
        }
    }
}