package com.example.prog7313_poe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class HomeListActivity : AppCompatActivity() {

    private lateinit var incomeAmount: TextView
    private lateinit var expenseAmount: TextView
    private lateinit var totalAmount: TextView
    private lateinit var budgetPercentage: TextView
    private lateinit var budgetExpense: TextView
    private lateinit var budgetLeft: TextView
    private lateinit var budgetProgress: ProgressBar
    private lateinit var transactionsRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var db: AppDatabase
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_list)
        db = AppDatabase.getDatabase(this)
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        initViews()
        setupBottomNav()
        setupToggleButtons()
    }

    override fun onResume() {
        super.onResume()
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
            // already list view
        }
        findViewById<ImageButton>(R.id.home_calendar_calendarButton).setOnClickListener {
            startActivity(Intent(this, HomeCalendarActivity::class.java))
            finish()
        }
    }

    private fun loadData() {
        Thread {
            val now = Date()
            val startOfMonth = getStartOfMonth(now)
            val endOfMonth = getEndOfMonth(now)

            val incomes = db.transactionDao().getByTypeBetweenDates("income", startOfMonth, endOfMonth)
            val expenses = db.transactionDao().getByTypeBetweenDates("expense", startOfMonth, endOfMonth)
            val totalIncome = incomes.sumOf { it.amount }
            val totalExpense = expenses.sumOf { it.amount }
            val net = totalIncome - totalExpense

            val all = db.transactionDao().getBetweenDates(startOfMonth, endOfMonth)
            val recent = all.sortedByDescending { it.date }.take(10)

            val budgetLimit = prefs.getFloat("monthly_budget", 5000f).toDouble()
            val percentage = if (budgetLimit > 0) ((totalExpense / budgetLimit) * 100).toInt() else 0

            runOnUiThread {
                incomeAmount.text = "R %.2f".format(totalIncome)
                expenseAmount.text = "R %.2f".format(totalExpense)
                totalAmount.text = "R %.2f".format(net)
                budgetPercentage.text = "$percentage%"
                budgetExpense.text = "R %.2f".format(totalExpense)
                budgetLeft.text = "R %.2f".format(budgetLimit - totalExpense)
                budgetProgress.progress = percentage.coerceIn(0, 100)

                val adapter = TransactionAdapter(recent)
                transactionsRecyclerView.adapter = adapter
            }
        }.start()
    }

    private fun getStartOfMonth(date: Date): Date {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    private fun getEndOfMonth(date: Date): Date {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.time
    }
}