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
                BadgeType.BRONZE,
                category = BadgeCategory.CONSISTENCY,
                isUnlocked = totalSavings >= 100
            ),

            Badge(
                "First Budget",
                "Create first budget",
                "📊",
                BadgeType.BRONZE,
                category = BadgeCategory.BUDGETING,
                isUnlocked = budgetCount > 0
            )

        )
    }
}