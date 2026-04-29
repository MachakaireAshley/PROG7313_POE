package com.example.prog7313_poe


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AccountDao {
    @Insert
    fun insert(account: Account)

    @Query("SELECT * FROM account_table WHERE userId = :userId")
    fun getAll(userId: String): List<Account>

    @Query("SELECT * FROM account_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getById(id: Int, userId: String): Account?

    @Query("DELETE FROM account_table WHERE id = :id AND userId = :userId")
    fun deleteById(id: Int, userId: String)

    @Update
    fun update(account: Account)
}