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

    private lateinit var fromDateText: TextView
    private lateinit var toDateText: TextView


    private lateinit var minGoalText: TextView
    private lateinit var maxGoalText: TextView
    private lateinit var goalStatusText: TextView

    private var startDate = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    private var endDate = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }

    private var min = 0f
    private var max = 5000f

    private val dateFormat = java.text.SimpleDateFormat("dd MMM yyyy", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        db = AppDatabase.getDatabase(this)

        monthYearText = findViewById(R.id.monthYearText)
        totalExpenses = findViewById(R.id.totalExpenses)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)



        fromDateText = findViewById(R.id.fromDateText)
        toDateText = findViewById(R.id.toDateText)


        minGoalText = findViewById(R.id.minGoalText)
        maxGoalText = findViewById(R.id.maxGoalText)
        goalStatusText = findViewById(R.id.goalStatusText)


        fromDateText.text = dateFormat.format(startDate.time)
        toDateText.text = dateFormat.format(endDate.time)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        min = prefs.getFloat("min_budget", 0f)
        max = prefs.getFloat("max_budget", 5000f)


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
        // From date card - opens a date picker for the start date
        findViewById<android.view.View>(R.id.fromDateCard).setOnClickListener {
            android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    // User picked a date, update startDate
                    val tempStart = Calendar.getInstance().apply {
                        set(year, month, day)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }

                    if (tempStart.after(endDate)) {
                        Toast.makeText(
                            this,
                            "Start date cannot be after end date",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@DatePickerDialog
                    }
                    startDate = tempStart
                    fromDateText.text = dateFormat.format(startDate.time)

                    loadReportData()
                },
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        findViewById<android.view.View>(R.id.toDateCard).setOnClickListener {
            android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    val tempEnd = Calendar.getInstance().apply {
                        set(year, month, day)
                        set(Calendar.HOUR_OF_DAY, 23)
                        set(Calendar.MINUTE, 59)
                        set(Calendar.SECOND, 59)
                        set(Calendar.MILLISECOND, 999)
                    }


                    if (startDate.after(tempEnd)) {
                        Toast.makeText(
                            this,
                            "End date cannot be before start date",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@DatePickerDialog
                    }
                    endDate = tempEnd
                    toDateText.text = dateFormat.format(endDate.time)

                    loadReportData()
                },
                endDate.get(Calendar.YEAR),
                endDate.get(Calendar.MONTH),
                endDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        findViewById<android.view.View>(R.id.categoryFilterCard).setOnClickListener {
            Toast.makeText(this, "Category filter (full version)", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadReportData() {
        Thread {
            val start = startDate.time
            val end = endDate.time

            val expenses = db.transactionDao().getByTypeBetweenDates("expense", start, end)
            val total = expenses.sumOf { it.amount }

            // Handle empty state early
            if (expenses.isEmpty()) {
                runOnUiThread {
                    monthYearText.text =
                        "${dateFormat.format(start)} - ${dateFormat.format(end)}"

                    totalExpenses.text = "R 0.00"
                    goalStatusText.text = "No expenses in this period"
                    categoriesRecyclerView.adapter = CategoryStatAdapter(emptyList<CategoryStat>())

                    minGoalText.text = "Min: R %.2f".format(min)
                    maxGoalText.text = "Max: R %.2f".format(max)
                }
                return@Thread
            }

            // Get all categories once (efficient)
            val categoryMap = db.categoryDao().getAll().associateBy { it.id }

            val grouped = expenses.groupBy { it.categoryId }
            val categoryTotals = mutableListOf<CategoryStat>()

            for ((catId, list) in grouped) {
                val sum = list.sumOf { it.amount }
                val percentage = if (total > 0) (sum / total * 100).roundToInt() else 0

                val categoryName = categoryMap[catId]?.name ?: "Unknown"

                categoryTotals.add(
                    CategoryStat(
                        "", // temporary rank
                        categoryName,
                        sum,
                        "$percentage%"
                    )
                )
            }

            // Sort by highest spending
            categoryTotals.sortByDescending { it.amount }

            // Add ranking (1, 2, 3...)
            val rankedList = categoryTotals.mapIndexed { index, item ->
                item.copy(rank = (index + 1).toString())
            }

            // Update UI safely
            runOnUiThread {
                monthYearText.text =
                    "${dateFormat.format(start)} - ${dateFormat.format(end)}"

                totalExpenses.text = "R %.2f".format(total)
                categoriesRecyclerView.adapter = CategoryStatAdapter(rankedList)

                minGoalText.text = "Min: R %.2f".format(min)
                maxGoalText.text = "Max: R %.2f".format(max)

                goalStatusText.text = when {
                    total < min -> "Below minimum spending"
                    total > max -> "Above maximum spending"
                    else -> "Within budget"
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        min = prefs.getFloat("min_budget", 0f)
        max = prefs.getFloat("max_budget", 5000f)
        loadReportData()
    }

}


data class CategoryStat(
    val rank: String,
    val name: String,
    val amount: Double,
    val ratio: String
)