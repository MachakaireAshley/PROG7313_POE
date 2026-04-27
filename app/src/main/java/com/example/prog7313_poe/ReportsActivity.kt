package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*
import kotlin.math.roundToInt

class ReportsActivity : AppCompatActivity() {

    private lateinit var monthYearText: TextView
    private lateinit var totalExpenses: TextView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var db: AppDatabase
    private var currentDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        db = AppDatabase.getDatabase(this)

        monthYearText = findViewById(R.id.monthYearText)
        totalExpenses = findViewById(R.id.totalExpenses)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)

        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        setupBottomNav()
        setupFilters()
        loadReportData()
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
            showMonthPicker()
        }
        findViewById<android.view.View>(R.id.categoryFilterCard).setOnClickListener {
            Toast.makeText(this, "Category filter (full version)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMonthPicker() {
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        android.app.DatePickerDialog(this, { _, y, m, _ ->
            currentDate.set(y, m, 1)
            loadReportData()
        }, year, month, 1).show()
    }

    private fun loadReportData() {
        Thread {
            val start = getStartOfMonth(currentDate.time)
            val end = getEndOfMonth(currentDate.time)
            val expenses = db.transactionDao().getByTypeBetweenDates("expense", start, end)
            val total = expenses.sumOf { it.amount }

            // group by categoryId
            val grouped = expenses.groupBy { it.categoryId }
            val categoryTotals = mutableListOf<CategoryStat>()
            for ((catId, list) in grouped) {
                val sum = list.sumOf { it.amount }
                val percentage = if (total > 0) (sum / total * 100).roundToInt() else 0
                categoryTotals.add(CategoryStat(catId.toString(), "Category $catId", "R %.2f".format(sum), "$percentage%"))
            }
            categoryTotals.sortByDescending { it.amount }

            runOnUiThread {
                val sdf = java.text.SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                monthYearText.text = sdf.format(currentDate.time)
                totalExpenses.text = "R %.2f".format(total)
                val adapter = CategoryStatAdapter(categoryTotals)
                categoriesRecyclerView.adapter = adapter
            }
        }.start()
    }

    private fun getStartOfMonth(date: Date): Date {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.time
    }

    private fun getEndOfMonth(date: Date): Date {
        val cal = Calendar.getInstance().apply { time = date }
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        return cal.time
    }

    data class CategoryStat(val rank: String, val name: String, val amount: String, val ratio: String)
}