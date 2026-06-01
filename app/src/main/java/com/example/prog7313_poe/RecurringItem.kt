package com.example.prog7313_poe

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.prog7313_poe.Transaction
import java.util.Date

@Entity(tableName = "recurring_item_table")
data class RecurringItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String="",
    val title: String="",
    val amount: Double=0.0,
    val categoryId: Int=0,
    val frequency: String="",
    val startDate: Date?=null,
    val endDate: Date? = null,
    val notes: String? = null,
    val lastUpdated: Long=0
)
{
    constructor() : this(0,"", "", 0.0, 0,"", null, null, null, 0)
}
