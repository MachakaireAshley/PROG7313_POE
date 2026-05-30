package com.example.prog7313_poe

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {
    @Insert
    suspend fun insert(category: Category): Long

    @Query("SELECT * FROM category_table WHERE userId = :userId")
    fun getAll(userId: String): List<Category>

    @Query("SELECT * FROM category_table WHERE userId = :userId ORDER BY name ASC")
    fun getCategoriesByUser(userId: String): Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getById(id: Int, userId: String): Category?

    @Query("SELECT * FROM category_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getByIdSync(id: Int, userId: String): Category?

    @Query("SELECT name FROM category_table WHERE id = :id AND userId = :userId LIMIT 1")
    fun getNameById(id: Int, userId: String): String?

    @Query("DELETE FROM category_table WHERE id = :id AND userId = :userId")
    suspend fun deleteById(id: Int, userId: String)

    @Update
    suspend fun update(category: Category)

    @Update
    fun updateSync(category: Category)
}