package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecurringItemDao {
    @Insert
    fun insert(item: RecurringItem)

    @Query("SELECT * FROM recurring_item_table")
    fun getAll(): List<RecurringItem>

    @Query("DELETE FROM recurring_item_table WHERE id = :id")
    fun deleteById(id: Int)
}