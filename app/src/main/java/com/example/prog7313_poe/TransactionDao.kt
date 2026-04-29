package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface TransactionDao {
    @Insert
    fun insert(transaction: Transaction)

    // Get all transactions for a user via their members
    @Query("""
        SELECT t.* FROM transaction_table t
        INNER JOIN member_table m ON t.memberId = m.id
        WHERE m.userId = :userId
    """)
    fun getAll(userId: String): List<Transaction>

    // Filter by date range
    @Query("""
        SELECT t.* FROM transaction_table t
        INNER JOIN member_table m ON t.memberId = m.id
        WHERE m.userId = :userId AND t.date BETWEEN :start AND :end
    """)
    fun getBetweenDates(userId: String, start: Date, end: Date): List<Transaction>

    // Filter by type and date range
    @Query("""
        SELECT t.* FROM transaction_table t
        INNER JOIN member_table m ON t.memberId = m.id
        WHERE m.userId = :userId AND t.transactionType = :type AND t.date BETWEEN :start AND :end
    """)
    fun getByTypeBetweenDates(userId: String, type: String, start: Date, end: Date): List<Transaction>
}