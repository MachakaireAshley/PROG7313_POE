package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Query("SELECT * FROM transaction_table WHERE userId = :userId")
    suspend fun getAll(userId: String): List<Transaction>

    @Query("SELECT * FROM transaction_table WHERE userId = :userId AND date BETWEEN :start AND :end")
    suspend fun getBetweenDates(userId: String, start: Date, end: Date): List<Transaction>

    @Query("SELECT * FROM transaction_table WHERE userId = :userId AND transactionType = :type AND date BETWEEN :start AND :end")
    suspend fun getByTypeBetweenDates(userId: String, type: String, start: Date, end: Date): List<Transaction>

    @Update
    suspend fun update(transaction: Transaction)

    @Update
    fun updateSync(transaction: Transaction)

    @Query("SELECT * FROM transaction_table WHERE userId = :userId ORDER BY date DESC")
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table WHERE id = :id AND userId = :userId")
    suspend fun getById(id: Int, userId: String): Transaction?

    @Query("SELECT * FROM transaction_table WHERE id = :id AND userId = :userId")
    fun getByIdSync(id: Int, userId: String): Transaction?

    @Query("DELETE FROM transaction_table WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Int, userId: String)
}