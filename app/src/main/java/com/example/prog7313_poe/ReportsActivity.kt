package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    // These will hold references to the graph views
    private lateinit var barChart: BarChartView
    private lateinit var graphGoalsLabel: TextView
    private lateinit var goalProgressBar: ProgressBar

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

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            finish()
            return
        }

        // Find all standard views
        monthYearText = findViewById(R.id.monthYearText)
        totalExpenses = findViewById(R.id.totalExpenses)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        bottomNav = findViewById(R.id.bottomNavigationView)
        fromDateText = findViewById(R.id.fromDateText)
        toDateText = findViewById(R.id.toDateText)
        minGoalText = findViewById(R.id.minGoalText)
        maxGoalText = findViewById(R.id.maxGoalText)
        goalStatusText = findViewById(R.id.goalStatusText)
        graphGoalsLabel = findViewById(R.id.graphGoalsLabel)
        goalProgressBar = findViewById(R.id.goalProgressBar)

        // +++ NEW CODE: Add BarChartView programmatically +++
        val chartContainer = findViewById<FrameLayout>(R.id.chartContainer)
        barChart = BarChartView(this)
        barChart.layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        chartContainer.addView(barChart)
        // +++ END OF NEW CODE +++

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
        findViewById<android.view.View>(R.id.fromDateCard).setOnClickListener {
            android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    val tempStart = Calendar.getInstance().apply {
                        set(year, month, day)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    if (tempStart.after(endDate)) {
                        Toast.makeText(this, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(this, "End date cannot be before start date", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Category filter coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadReportData() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        lifecycleScope.launch {
            val start = startDate.time
            val end = endDate.time

            val expenses = withContext(Dispatchers.IO) {
                db.transactionDao().getByTypeBetweenDates(currentUserId, "expense", start, end)
            }
            val total = expenses.sumOf { it.amount }

            if (expenses.isEmpty()) {
                monthYearText.text = "${dateFormat.format(start)} - ${dateFormat.format(end)}"
                totalExpenses.text = "R 0.00"
                goalStatusText.text = "No expenses in this period"
                categoriesRecyclerView.adapter = CategoryStatAdapter(emptyList())
                minGoalText.text = "Min: R %.2f".format(min)
                maxGoalText.text = "Max: R %.2f".format(max)

                barChart.setData(emptyList(), min, max)
                graphGoalsLabel.text = "Min: R${"%.2f".format(min)}   Max: R${"%.2f".format(max)}"
                goalProgressBar.progress = 0
                return@launch
            }

            val categories = withContext(Dispatchers.IO) {
                db.categoryDao().getAll(currentUserId)
            }
            val categoryMap = categories.associateBy { it.id }

            val grouped = expenses.groupBy { it.categoryId }
            val categoryTotals = mutableListOf<CategoryStat>()

            for ((catId, list) in grouped) {
                val sum = list.sumOf { it.amount }
                val percentage = if (total > 0) (sum / total * 100).roundToInt() else 0
                var categoryName = categoryMap[catId]?.name
                if (categoryName == null) {
                    categoryName = withContext(Dispatchers.IO) {
                        db.categoryDao().getNameById(catId, currentUserId)
                    }
                }
                categoryTotals.add(CategoryStat("", categoryName ?: "Unknown", sum, "$percentage%"))
            }

            categoryTotals.sortByDescending { it.amount }
            val rankedList = categoryTotals.mapIndexed { index, item ->
                item.copy(rank = (index + 1).toString())
            }

            monthYearText.text = "${dateFormat.format(start)} - ${dateFormat.format(end)}"
            totalExpenses.text = "R %.2f".format(total)
            categoriesRecyclerView.adapter = CategoryStatAdapter(rankedList)

            minGoalText.text = "Min: R %.2f".format(min)
            maxGoalText.text = "Max: R %.2f".format(max)
            goalStatusText.text = when {
                total < min -> "Below minimum spending"
                total > max -> "Above maximum spending"
                else -> "Within budget"
            }

            val progressPercent = if (max > 0) ((total / max) * 100).toInt().coerceIn(0, 100) else 0
            goalProgressBar.progress = progressPercent
            when {
                total < min -> goalProgressBar.progressTintList = ContextCompat.getColorStateList(this@ReportsActivity, R.color.income_green)
                total > max -> goalProgressBar.progressTintList = ContextCompat.getColorStateList(this@ReportsActivity, R.color.expense_red)
                else -> goalProgressBar.progressTintList = ContextCompat.getColorStateList(this@ReportsActivity, R.color.custom_blue)
            }

            val chartData = rankedList.map { BarChartView.BarData(it.name, it.amount.toFloat()) }
            barChart.setData(chartData, min, max)
            graphGoalsLabel.text = "Min: R${"%.2f".format(min)}   Max: R${"%.2f".format(max)}"
        }
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
    val ratio: String)