package com.example.prog7313_poe

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
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

    // Date range views
    private lateinit var fromDateText: TextView
    private lateinit var toDateText: TextView
    private lateinit var fromDateCard: CardView
    private lateinit var toDateCard: CardView

    // Date variables
    private var startDate: Date = Date()
    private var endDate: Date = Date()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_list)
        db = AppDatabase.getDatabase(this)
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        initViews()
        setupBottomNav()
        setupToggleButtons()
        setupDateRangeSelector()

        // Initialize with current month
        initializeDefaultDateRange()
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

        // Date range views
        fromDateText = findViewById(R.id.fromDateText)
        toDateText = findViewById(R.id.toDateText)
        fromDateCard = findViewById(R.id.fromDateCard)
        toDateCard = findViewById(R.id.toDateCard)

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

    private fun setupDateRangeSelector() {
        fromDateCard.setOnClickListener {
            showDatePickerDialog(true)
        }

        toDateCard.setOnClickListener {
            showDatePickerDialog(false)
        }
    }

    private fun initializeDefaultDateRange() {
        val now = Date()
        startDate = getStartOfMonth(now)
        endDate = getEndOfMonth(now)
        updateDateRangeDisplay()
    }

    private fun showDatePickerDialog(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        val currentDate = if (isFromDate) startDate else endDate
        calendar.time = currentDate

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)

                if (isFromDate) {
                    val newStartDate = selectedCalendar.time
                    if (newStartDate.after(endDate)) {
                        Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
                    } else {
                        startDate = newStartDate
                        updateDateRangeDisplay()
                        loadData()
                    }
                } else {
                    val newEndDate = selectedCalendar.time
                    if (newEndDate.before(startDate)) {
                        Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show()
                    } else {
                        endDate = newEndDate
                        updateDateRangeDisplay()
                        loadData()
                    }
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateRangeDisplay() {
        fromDateText.text = dateFormat.format(startDate)
        toDateText.text = dateFormat.format(endDate)
    }

    private fun loadData() {
        val userId= UserSession.userId
        Thread {
            // Use the selected date range instead of current month
            val incomes = db.transactionDao().getByTypeBetweenDates(userId,"income", startDate, endDate)
            val expenses = db.transactionDao().getByTypeBetweenDates(userId,"expense", startDate, endDate)
            val totalIncome = incomes.sumOf { it.amount }
            val totalExpense = expenses.sumOf { it.amount }
            val net = totalIncome - totalExpense

            val all = db.transactionDao().getBetweenDates(userId,startDate, endDate)
            val recent = all.sortedByDescending { it.date }.take(10)

            val budgetLimit = prefs.getFloat("monthly_budget", 5000f).toDouble()
            val percentage = if (budgetLimit > 0) ((totalExpense / budgetLimit) * 100).toInt() else 0

            // Calculate remaining budget (budgetLimit - expense for selected period)
            val remainingBudget = budgetLimit - totalExpense

            runOnUiThread {
                incomeAmount.text = "R %.2f".format(totalIncome)
                expenseAmount.text = "R %.2f".format(totalExpense)
                totalAmount.text = "R %.2f".format(net)
                budgetPercentage.text = "$percentage%"
                budgetExpense.text = "R %.2f".format(totalExpense)
                budgetLeft.text = "R %.2f".format(remainingBudget)
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