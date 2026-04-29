package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MemberDao {
    @Insert
    fun insert(member: Member)


    @Query("SELECT * FROM member_table WHERE userId = :userId")
    fun getAll(userId: String): List<Member>


    @Query("SELECT id FROM member_table WHERE userId = :userId")
    fun getMemberIdsByUserId(userId: String): List<Int>


    @Query("DELETE FROM member_table WHERE id = :id AND userId = :userId")
    fun deleteById(id: Int, userId: String)
}