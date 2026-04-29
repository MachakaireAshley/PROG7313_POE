package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "recurring_item_table")
data class RecurringItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val title: String,
    val amount: Double,
    val categoryId: Int,
    val frequency: String,
    val startDate: Date,
    val endDate: Date? = null,
    val notes: String? = null
)