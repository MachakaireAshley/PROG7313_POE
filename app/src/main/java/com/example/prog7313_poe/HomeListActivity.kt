package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeListActivity : AppCompatActivity() {

    private lateinit var incomeAmount: TextView
    private lateinit var expenseAmount: TextView
    private lateinit var totalAmount: TextView
    private lateinit var budgetPercentage: TextView
    private lateinit var budgetExpense: TextView
    private lateinit var budgetLeft: TextView
    private lateinit var budgetProgress: android.widget.ProgressBar
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_list)

        initViews()
        setupBottomNav()
        setupToggleButtons()
        loadData()
    }

    private fun initViews() {
        incomeAmount = findViewById(R.id.incomeAmount)
        expenseAmount = findViewById(R.id.expenseAmount)
        totalAmount = findViewById(R.id.totalAmount)
        budgetPercentage = findViewById(R.id.budgetPercentage)
        budgetExpense = findViewById(R.id.budgetExpense)
        budgetLeft = findViewById(R.id.budgetLeft)
        budgetProgress = findViewById(R.id.budgetProgress)
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)

        transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_accounts -> {
                    startActivity(Intent(this, AccountsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupToggleButtons() {
        findViewById<ImageButton>(R.id.home_calendar_listButton).setOnClickListener {
            // Already on list view
        }

        findViewById<ImageButton>(R.id.home_calendar_calendarButton).setOnClickListener {
            startActivity(Intent(this, HomeCalendarActivity::class.java))
            finish()
        }
    }

    private fun loadData() {
        incomeAmount.text = "R 1,234"
        expenseAmount.text = "R 456"
        totalAmount.text = "R 778"

        val budget = 5000.0
        val expense = 456.0
        val percentage = ((expense / budget) * 100).toInt()

        budgetPercentage.text = "$percentage%"
        budgetExpense.text = "R $expense"
        budgetLeft.text = "R ${budget - expense}"
        budgetProgress.progress = percentage

        val transactions = listOf(
            Transaction("Mar 8, 2026", "Food", "R 45.00", "Cash", R.color.expense_red),
            Transaction("Mar 7, 2026", "Salary", "R 500.00", "Bank", R.color.income_green),
            Transaction("Mar 6, 2026", "Transport", "R 30.00", "Cash", R.color.expense_red)
        )

        val adapter = TransactionAdapter(transactions)
        transactionsRecyclerView.adapter = adapter
    }

    data class Transaction(val date: String, val category: String, val amount: String, val source: String, val colorRes: Int)
}