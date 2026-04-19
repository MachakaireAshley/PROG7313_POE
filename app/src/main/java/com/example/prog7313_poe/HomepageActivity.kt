package com.example.prog7313_poe

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomepageActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var transactionsAdapter: TransactionAdapter

    // Sample data class for transactions
    data class Transaction(
        val date: String,
        val category: String,
        val amount: Double,
        val type: String, // "income" or "expense"
        val source: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        initializeViews()
        setupBottomNavigation()
        setupTransactionsList()
        loadSampleTransactions()
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already on Home page
                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_accounts -> {
                    Toast.makeText(this, "Accounts clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_add -> {
                    Toast.makeText(this, "Add new transaction", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_reports -> {
                    Toast.makeText(this, "Reports clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupTransactionsList() {
        transactionsAdapter = TransactionAdapter { transaction ->
            // Handle transaction click
            Toast.makeText(this, "Clicked: ${transaction.category}", Toast.LENGTH_SHORT).show()
        }

        transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomepageActivity)
            adapter = transactionsAdapter
        }
    }

    private fun loadSampleTransactions() {
        val transactions = listOf(
            Transaction("TUE, 03 Mar", "Groceries", 256.00, "expense", "Bank"),
            Transaction("TUE, 03 Mar", "Salary", 123.00, "income", "Bank"),
            Transaction("MON, 02 Mar", "Food", 200.00, "expense", "Bank")
        )

        transactionsAdapter.submitList(transactions)
    }
}