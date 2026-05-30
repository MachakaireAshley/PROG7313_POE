package com.example.prog7313_poe

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CategoryRepo(private val categoryDao: CategoryDao, private val userId: String)
{
private val database= FirebaseDatabase.getInstance()
    .getReference("users")
    .child(userId)
    .child("categories")


    fun getCategories(): Flow<List<Category>> {
        return categoryDao.getCategoriesByUser(userId)
    }

    suspend fun saveCategory(category: Category){
        val updatedCategory= category.copy(
            lastUpdated = System.currentTimeMillis()
        )

        val generatedId= categoryDao.insert(updatedCategory)

        val categoryForFirebase= updatedCategory.copy(id= generatedId.toInt())

        database.child(generatedId.toString()).setValue(categoryForFirebase)
    }

    fun listenForCloudChanges(){
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val localAccounts= categoryDao.getAll(userId).associateBy { it.id }

                    for(categorySnapshot in snapshot.children){
                        val firebaseCategories= categorySnapshot.getValue(Category::class.java)
                        firebaseCategories?.let {
                                cloudAccount->
                            val localCategories = localAccounts[cloudAccount.id]

                            if(localCategories==null){
                                categoryDao.insert(cloudAccount)
                                android.util.Log.d("CategoryRepo", "New category inserted: ${cloudAccount.name}")
                            }
                            else if(cloudAccount.lastUpdated>localCategories.lastUpdated){
                                categoryDao.update(cloudAccount)
                                android.util.Log.d("CategoryRepo", "Category Updated: ${cloudAccount.name}")
                            }
                        }
                    }

                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("CategoryRepo", "Firebase listen cancelled", error.toException())
            }

        })
    }

}