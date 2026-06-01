package com.example.prog7313_poe

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Date

class TransactionRepo(private val transactionDao: TransactionDao, private val accountDao: AccountDao,
                      private val categoryDao: CategoryDao,
                      private val memberDao: MemberDao, private val userId: String)
{
    private val database= FirebaseDatabase.getInstance()
        .getReference("users")
        .child(userId)
        .child("transactions")


    fun getTransactions(): Flow<List<Transaction>>{
        return transactionDao.getTransactionsByUser(userId)
    }

    suspend fun saveTransaction(transaction: Transaction) {
        try {
            android.util.Log.d("TransactionRepo", "Save started for: ${transaction.name}")

            val updatedTransaction = transaction.copy(
                lastUpdated = System.currentTimeMillis()
            )

            // Verify account exists
            val account = accountDao.getById(transaction.accountId, userId)
                ?: throw IllegalArgumentException("Account ${transaction.accountId} does not exist")

            // Verify category exists
            val category = categoryDao.getById(transaction.categoryId, userId)
                ?: throw IllegalArgumentException("Category ${transaction.categoryId} does not exist")

            // Verify member exists
            val member = memberDao.getById(transaction.memberId, userId)
                ?: throw IllegalArgumentException("Member ${transaction.memberId} does not exist")

            // Save transaction
            val generatedId = transactionDao.insert(updatedTransaction)
            val transactionForFirebase = updatedTransaction.copy(id = generatedId.toInt())
            database.child(generatedId.toString()).setValue(transactionForFirebase)

            // Update account balance
            val amountChange = if (transaction.transactionType == "expense") {
                -transaction.amount
            } else {
                transaction.amount
            }
            accountDao.updateAccountAmount(
                accountId = transaction.accountId,
                amountChange = amountChange,
                timestamp = System.currentTimeMillis(),
                userId = userId
            )

            android.util.Log.d("TransactionRepo", "Save successful, ID: $generatedId")

        } catch (e: Exception) {
            android.util.Log.e("TransactionRepo", "Save failed", e)
        }

    }


    fun listenForCloudChanges() {
        database.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    val localTransactions = transactionDao.getAll(userId).associateBy { it.id }

                    for (transactionSnapshot in snapshot.children) {
                        val firebaseTransaction = transactionSnapshot.getValue(Transaction::class.java)
                        firebaseTransaction?.let { cloudTransaction ->
                            val localTransaction = localTransactions[cloudTransaction.id]

                            if (localTransaction == null) {
                                // New transaction - insert
                                transactionDao.insert(cloudTransaction)
                                android.util.Log.d("TransactionRepo", "New transaction inserted: ${cloudTransaction.name}")

                                // Update account balance for the new transaction
                                val amountChange = if (cloudTransaction.transactionType == "expense") {
                                    -cloudTransaction.amount
                                } else {
                                    cloudTransaction.amount
                                }
                                accountDao.updateAccountAmount(
                                    accountId = cloudTransaction.accountId,
                                    amountChange = amountChange,
                                    timestamp = System.currentTimeMillis(),
                                    userId = userId
                                )

                            } else if (cloudTransaction.lastUpdated > localTransaction.lastUpdated) {
                                // Existing transaction with newer data - update
                                transactionDao.update(cloudTransaction)
                                android.util.Log.d("TransactionRepo", "Transaction updated: ${cloudTransaction.name}")
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                android.util.Log.e("TransactionRepo", "Firebase listen cancelled", error.toException())
            }
        })
    }
    suspend fun getTransactionsBetweenDates(userId: String, start: Date, end: Date, type: String): List<Transaction> {
        return transactionDao.getByTypeBetweenDates(userId, type, start, end)
    }

    suspend fun getAllTransactions(userId: String, start: Date, end: Date): List<Transaction> {
        return transactionDao.getBetweenDates(userId, start, end)
    }
}