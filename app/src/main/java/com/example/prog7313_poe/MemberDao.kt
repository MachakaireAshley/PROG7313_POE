package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface MemberDao {
    @Insert
    suspend fun insert(member: Member): Long

    @Query("SELECT * FROM member_table WHERE userId = :userId")
    fun getAll(userId: String): List<Member>

    @Query("SELECT * FROM member_table WHERE userId = :userId ORDER BY name ASC")
    fun getMembersByUser(userId: String): Flow<List<Member>>

    @Query("SELECT * FROM member_table WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getById(id: Int, userId: String): Member?

    @Query("SELECT * FROM member_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getByIdSync(id: Int, userId: String): Member?

    @Query("SELECT name FROM member_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getNameById(id: Int, userId: String): String?
    @Query("SELECT id FROM member_table WHERE userId = :userId")
    fun getMemberIdsByUserId(userId: String): List<Int>

    @Query("DELETE FROM member_table WHERE id = :id AND userId = :userId")
    fun deleteById(id: Int, userId: String)

    @Update
    suspend fun update(member: Member)

    @Update
    fun updateSync(member: Member)
}