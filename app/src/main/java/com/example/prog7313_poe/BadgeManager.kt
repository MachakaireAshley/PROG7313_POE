package com.example.prog7313_poe

class BadgeManager {

    fun getBadges(
        totalSavings: Double,
        budgetCount: Int,
        monthsWithinBudget: Int,
        streakDays: Int,
        totalTransactions: Int,
        noSpendDays: Int,
        noSpendConsecutive: Int,
        goalsReached: Int
    ): List<Badge> {

        return listOf(

            // === Savings ===
            Badge("First R100 Saved", "Save R100", "💰", BadgeType.BRONZE, BadgeCategory.SAVINGS, totalSavings >= 100),
            Badge("First R1000 Saved", "Save R1000", "💵", BadgeType.SILVER, BadgeCategory.SAVINGS, totalSavings >= 1000),
            Badge("Savings Champion", "Save continuously for 3 months", "🏆", BadgeType.GOLD, BadgeCategory.SAVINGS, monthsWithinBudget >= 3 && totalSavings > 0),

            // === Budgeting ===
            Badge("First Budget", "Create first budget", "📊", BadgeType.BRONZE, BadgeCategory.BUDGETING, budgetCount > 0),
            Badge("Budget Hero", "Stay within budget for 1 month", "🦸", BadgeType.SILVER, BadgeCategory.BUDGETING, monthsWithinBudget >= 1),
            Badge("Budget Master", "Stay within budget for 6 months", "👑", BadgeType.GOLD, BadgeCategory.BUDGETING, monthsWithinBudget >= 6),

            // === Consistency ===
            Badge("7-Day Streak", "Record expenses for 7 consecutive days", "📅", BadgeType.BRONZE, BadgeCategory.CONSISTENCY, streakDays >= 7),
            Badge("30-Day Streak", "Record expenses for 30 consecutive days", "📆", BadgeType.SILVER, BadgeCategory.CONSISTENCY, streakDays >= 30),
            Badge("100-Day Streak", "Record expenses for 100 consecutive days", "🔥", BadgeType.GOLD, BadgeCategory.CONSISTENCY, streakDays >= 100),

            // === Spending Control ===
            Badge("No-Spend Day", "Complete one day without unnecessary spending", "🚫", BadgeType.BRONZE, BadgeCategory.SPENDING_CONTROL, noSpendDays >= 1),
            Badge("No-Spend Week", "Complete one week without unnecessary spending", "✨", BadgeType.SILVER, BadgeCategory.SPENDING_CONTROL, noSpendConsecutive >= 7),
            Badge("Impulse Buyer Slayer", "Reduce unnecessary spending for one month", "⚔️", BadgeType.GOLD, BadgeCategory.SPENDING_CONTROL, noSpendDays >= 30),

            // === Goal Achievement ===
            Badge("Goal Getter", "Reach first savings goal", "🎯", BadgeType.BRONZE, BadgeCategory.GOAL_ACHIEVEMENT, goalsReached >= 1),
            Badge("Goal Crusher", "Reach 3 savings goals", "💪", BadgeType.SILVER, BadgeCategory.GOAL_ACHIEVEMENT, goalsReached >= 3),
            Badge("Dream Achiever", "Reach largest savings goal", "🌟", BadgeType.GOLD, BadgeCategory.GOAL_ACHIEVEMENT, goalsReached >= 5)
        )
    }
}