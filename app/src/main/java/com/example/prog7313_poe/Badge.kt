package com.example.prog7313_poe

data class Badge(
    val name: String,
    val description: String,
    val icon: String,
    val type: BadgeType,
    val category: BadgeCategory,
    val isUnlocked: Boolean
)