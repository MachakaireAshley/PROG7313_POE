package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface CategoryDao {
    @Insert
    fun insert(category: Category)

    @Query("SELECT * FROM category_table WHERE userId = :userId")
    fun getAll(userId: String): List<Category>

    @Query("SELECT * FROM category_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getById(id: Int, userId: String): Category?

    @Query("SELECT name FROM category_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getNameById(id: Int, userId: String): String?

    @Query("DELETE FROM category_table WHERE id = :id AND userId = :userId")
    fun deleteById(id: Int, userId: String)
}