package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ReportsActivity : AppCompatActivity() {

    private lateinit var monthYearText: TextView
    private lateinit var categoryFilterText: TextView
    private lateinit var totalExpenses: TextView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        initViews()
        setupBottomNav()
        setupFilters()
        loadReportData()
    }

    private fun initViews() {
        monthYearText = findViewById(R.id.monthYearText)
        categoryFilterText = findViewById(R.id.categoryFilterText)
        totalExpenses = findViewById(R.id.totalExpenses)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)

        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_reports
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_accounts -> {
                    startActivity(Intent(this, AccountsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddTransactionActivity::class.java))
                    true
                }
                R.id.nav_reports -> true
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupFilters() {
        findViewById<android.view.View>(R.id.monthSelectorCard).setOnClickListener {
            Toast.makeText(this, "Select month", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.categoryFilterCard).setOnClickListener {
            Toast.makeText(this, "Filter by category", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadReportData() {
        totalExpenses.text = "R 456.00"

        val categories = listOf(
            CategoryStat("1", "Food", "R 150.00", "32.9%"),
            CategoryStat("2", "Transport", "R 100.00", "21.9%"),
            CategoryStat("3", "Entertainment", "R 80.00", "17.5%"),
            CategoryStat("4", "Utilities", "R 70.00", "15.4%"),
            CategoryStat("5", "Shopping", "R 56.00", "12.3%")
        )

        val adapter = CategoryStatAdapter(categories)
        categoriesRecyclerView.adapter = adapter
    }

    data class CategoryStat(val rank: String, val name: String, val amount: String, val ratio: String)
}