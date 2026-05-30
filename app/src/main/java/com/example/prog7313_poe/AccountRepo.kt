package com.example.prog7313_poe

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AccountRepo(private val accountDao: AccountDao, private val userId: String)
{
  private val database= FirebaseDatabase.getInstance("https://prog7313-poe-329cf-default-rtdb.firebaseio.com/")
      .getReference("users")
      .child(userId)
      .child("accounts")

    fun getAccounts(): Flow<List<Account>>
    {
    return accountDao.getAccountsByUser(userId)
    }


    suspend fun saveAccount(account: Account) {
        try {
            android.util.Log.d("AccountRepo", "Save started for: ${account.accountName}")

            val updatedAccount = account.copy(
                lastUpdated = System.currentTimeMillis()
            )

            val generatedId = accountDao.insert(updatedAccount)
            android.util.Log.d("AccountRepo", "Room insert success, ID: $generatedId")

            val accountForFirebase = updatedAccount.copy(id = generatedId.toInt())

            database.child(generatedId.toString()).setValue(accountForFirebase)
                .addOnSuccessListener {
                    android.util.Log.d("AccountRepo", "Firebase save success")
                }
                .addOnFailureListener { e ->
                    android.util.Log.e("AccountRepo", "Firebase save failed", e)
                }

        } catch (e: Exception) {
            android.util.Log.e("AccountRepo", "CRASH in saveAccount", e)
            throw e
        }
    }




    fun listenForCloudChanges(){
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val localAccounts= accountDao.getAll(userId).associateBy { it.id }

                    for(accountSnapshot in snapshot.children){
                        val firebaseAccount= accountSnapshot.getValue(Account::class.java)
                        firebaseAccount?.let {
                            cloudAccount->
                            val localAccount = localAccounts[cloudAccount.id]

                            if(localAccount==null){
                                accountDao.insert(cloudAccount)
                                android.util.Log.d("AccountRepo", "New account inserted: ${cloudAccount.accountName}")
                            }
                            else if(cloudAccount.lastUpdated>localAccount.lastUpdated){
                                accountDao.update(cloudAccount)
                                android.util.Log.d("AccountRepo", "Account Updated: ${cloudAccount.accountName}")
                            }
                        }
                    }

                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("AccountRepo", "Firebase listen cancelled", error.toException())
            }
        })
    }
}