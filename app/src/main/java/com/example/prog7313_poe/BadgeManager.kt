package com.example.prog7313_poe

class BadgeManager {

    fun getBadges(
        totalSavings: Double,
        transactionCount: Int
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
                "First Transaction",
                "Create first Transaction",
                "📊",
                BadgeType.BRONZE,
                category = BadgeCategory.SAVINGS,
                isUnlocked = transactionCount > 0
            )

        )
    }
}