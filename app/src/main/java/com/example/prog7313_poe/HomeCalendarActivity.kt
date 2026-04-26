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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_calendar)

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

        updateDataForDate(2026, 2, 8)
    }

    private fun updateDataForDate(year: Int, month: Int, day: Int) {
        calendarIncome.text = "R 123"
        calendarExpense.text = "R 456"
        calendarTotal.text = "R -333"
    }
}