package com.example.prog7313_poe


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account): Long

    @Query("SELECT * FROM account_table WHERE userId = :userId")
         fun getAll(userId: String): List<Account>

    @Query("SELECT * FROM account_table WHERE userId = :userId ORDER BY accountName ASC")
    fun getAccountsByUser(userId: String): Flow<List<Account>>

    @Query("SELECT * FROM account_table WHERE id = :id AND userId = :userId LIMIT 1")
   suspend fun getById(id: Int, userId: String): Account?

    @Query("SELECT * FROM account_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getByIdSync(id: Int, userId: String): Account?

    @Query("DELETE FROM account_table WHERE id = :id AND userId = :userId")
   suspend fun deleteById(id: Int, userId: String)

    @Update
    suspend fun update(account: Account)

    @Update
    fun updateSync(account: Account)
}