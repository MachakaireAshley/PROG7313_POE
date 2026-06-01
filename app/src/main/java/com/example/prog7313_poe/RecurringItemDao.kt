package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringItemDao {
    @Insert
   suspend fun insert(item: RecurringItem): Long

    @Update
    suspend fun update(item: RecurringItem)

    @Update
    fun updateSync(item: RecurringItem)

    @Query("SELECT * FROM recurring_item_table WHERE userId = :userId ORDER BY lastUpdated DESC")
    fun getRecurringItemsByUser(userId: String): Flow<List<RecurringItem>>

    @Query("SELECT * FROM recurring_item_table WHERE userId = :userId")
    suspend fun getRecurringItemsList(userId: String): List<RecurringItem>


    @Query("SELECT * FROM recurring_item_table WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getById(id: Int, userId: String): RecurringItem?

    @Query("SELECT * FROM recurring_item_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getByIdSync(id: Int, userId: String): RecurringItem?

    @Query("SELECT * FROM recurring_item_table WHERE userId = :userId")
    fun getAll(userId: String): List<RecurringItem>

    @Query("DELETE FROM recurring_item_table WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Int, userId: String)
}