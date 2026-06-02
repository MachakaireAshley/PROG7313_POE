package com.example.prog7313_poe

class BadgeManager {

    fun getBadges(
        totalSavings: Double,
        budgetCount: Int
    ): List<Badge> {

        return listOf(

            Badge(
                "First R100 Saved",
                "Save R100",
                "💰",
                totalSavings >= 100
            ),

            Badge(
                "First Budget",
                "Create first budget",
                "📊",
                budgetCount > 0
            )

        )
    }
}