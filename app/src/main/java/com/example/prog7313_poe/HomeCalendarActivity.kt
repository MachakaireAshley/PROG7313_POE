package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeCalendarActivity : AppCompatActivity() {


    private lateinit var calendarIncome: TextView
    private lateinit var calendarExpense: TextView
    private lateinit var calendarTotal: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_calendar)
        db = AppDatabase.getDatabase(this)

        initViews()
        setupBottomNav()
        setupToggleButtons()
        setupCalendar()
    }

    private fun initViews() {
        calendarIncome = findViewById(R.id.calendarIncome)
        calendarExpense = findViewById(R.id.calendarExpense)
        calendarTotal = findViewById(R.id.calendarTotal)
        calendarView = findViewById(R.id.calendarView)
        bottomNav = findViewById(R.id.bottomNavigationView)
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
            startActivity(Intent(this, HomeListActivity::class.java))
            finish()
        }

        findViewById<ImageButton>(R.id.home_calendar_calendarButton).setOnClickListener {
            // Already on calendar view
        }
    }

    private fun setupCalendar() {
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            updateDataForDate(year, month, dayOfMonth)
        }

        val today = java.util.Calendar.getInstance()
        updateDataForDate(
            today.get(java.util.Calendar.YEAR),
            today.get(java.util.Calendar.MONTH),
            today.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }

    private fun updateDataForDate(year: Int, month: Int, day: Int) {


        Thread {
            val cal = java.util.Calendar.getInstance()

            // Start of selected day
            cal.set(year, month, day, 0, 0, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            val startOfDay = cal.time

            // End of selected day
            cal.set(year, month, day, 23, 59, 59)
            cal.set(java.util.Calendar.MILLISECOND, 999)
            val endOfDay = cal.time

            val incomes = db.transactionDao().getByTypeBetweenDates("income", startOfDay, endOfDay)
            val expenses = db.transactionDao().getByTypeBetweenDates("expense", startOfDay, endOfDay)

            val totalIncome = incomes.sumOf { it.amount }
            val totalExpense = expenses.sumOf { it.amount }
            val net = totalIncome - totalExpense

            runOnUiThread {
                calendarIncome.text = "R %.2f".format(totalIncome)
                calendarExpense.text = "R %.2f".format(totalExpense)
                calendarTotal.text = "R %.2f".format(net)
            }
        }.start()
    }


}