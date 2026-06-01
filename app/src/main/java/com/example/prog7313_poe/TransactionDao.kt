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

    // Get all transactions for a user via their members
    @Query(
        """
        SELECT t.* FROM transaction_table t
        INNER JOIN member_table m ON t.memberId = m.id
        WHERE m.userId = :userId
    """
    )
   suspend fun getAll(userId: String): List<Transaction>

    // Filter by date range
    @Query(
        """
        SELECT t.* FROM transaction_table t
        INNER JOIN member_table m ON t.memberId = m.id
        WHERE m.userId = :userId AND t.date BETWEEN :start AND :end
    """
    )
   suspend fun getBetweenDates(userId: String, start: Date, end: Date): List<Transaction>

    // Filter by type and date range
    @Query(
        """
        SELECT t.* FROM transaction_table t
        INNER JOIN member_table m ON t.memberId = m.id
        WHERE m.userId = :userId AND t.transactionType = :type AND t.date BETWEEN :start AND :end
    """
    )
 suspend fun getByTypeBetweenDates(
        userId: String,
        type: String,
        start: Date,
        end: Date
    ): List<Transaction>

    @Update
    suspend fun update(transaction: Transaction)

    @Update
    fun updateSync(transaction: Transaction)


    @Query("""
        SELECT t.* FROM transaction_table t
        INNER JOIN member_table m ON t.memberId = m.id
        WHERE m.userId = :userId ORDER BY t.date DESC
    """)
    fun getTransactionsByUser(userId: String): Flow<List<Transaction>>


    @Query("SELECT * FROM transaction_table WHERE id = :id")
    suspend fun getById(id: Int): Transaction?

    @Query("SELECT * FROM transaction_table WHERE id = :id")
    fun getByIdSync(id: Int): Transaction?

    @Query("DELETE FROM transaction_table WHERE id = :id")
    suspend fun deleteById(id: Int)


}