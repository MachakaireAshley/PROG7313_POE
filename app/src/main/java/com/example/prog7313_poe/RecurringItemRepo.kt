package com.example.prog7313_poe

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecurringItemRepo(private val recurringItemDao: RecurringItemDao,
                        private val categoryDao: CategoryDao,
                        private val userId: String)
{
    private val database= FirebaseDatabase.getInstance()
        .getReference("users")
        .child(userId)
        .child("recurringItems")

    fun getRecurringItems(): Flow<List<RecurringItem>> {
        return recurringItemDao.getRecurringItemsByUser(userId)
    }

    suspend fun saveRecurringItem(recurringItem: RecurringItem)
    {
        try{
        android.util.Log.d("RecurringItemRepo", "Save started for: ${recurringItem.title}")
        val updatedRecurringItem = recurringItem.copy(
            userId=userId,
            lastUpdated = System.currentTimeMillis()
        )

        // Verify category exists
        val category = categoryDao.getById(recurringItem.categoryId, userId)
            ?: throw IllegalArgumentException("Category ${recurringItem.categoryId} does not exist")

        val generatedId = recurringItemDao.insert(updatedRecurringItem)
        val recurringItemForFirebase = updatedRecurringItem.copy(id = generatedId.toInt())
        database.child(generatedId.toString()).setValue(recurringItemForFirebase)


        android.util.Log.d("RecurringItemRepo", "Save successful, ID: $generatedId")

    } catch (e: Exception) {
    android.util.Log.e("RecurringItemRepo", "Save failed", e)
}

    }


      fun listenForCloudChanges() {
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val localItems = recurringItemDao.getRecurringItemsList(userId).associateBy { it.id }

                    for (itemSnapshot in snapshot.children) {
                        val firebaseItem = itemSnapshot.getValue(RecurringItem::class.java)
                        firebaseItem?.let { cloudItem ->
                            val localItem = localItems[cloudItem.id]

                            if (localItem == null) {
                                // New recurring item - insert
                                recurringItemDao.insert(cloudItem)
                                android.util.Log.d("RecurringItemRepo", "New recurring item inserted: ${cloudItem.title}")
                            } else if (cloudItem.lastUpdated > localItem.lastUpdated) {
                                // Existing item with newer data - update
                                recurringItemDao.update(cloudItem)
                                android.util.Log.d("RecurringItemRepo", "Recurring item updated: ${cloudItem.title}")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("RecurringItemRepo", "Firebase listen cancelled", error.toException())
            }
        })
    }

    suspend fun deleteRecurringItem(itemId: Int) {
        recurringItemDao.deleteById(itemId, userId)
        database.child(itemId.toString()).removeValue()
    }
}