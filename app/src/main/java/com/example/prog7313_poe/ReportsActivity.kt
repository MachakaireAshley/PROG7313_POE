package com.example.prog7313_poe

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var monthYearText: TextView
    private lateinit var categoryFilterText: TextView
    private lateinit var totalExpenses: TextView
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var monthSelectorCard: MaterialCardView
    private lateinit var categoryFilterCard: MaterialCardView

    private var currentMonth = Calendar.getInstance()
    private var currentFilter = "EXP. By Category"
    private lateinit var categoriesAdapter: CategoriesAdapter

    // Sample transaction data
    private val transactions = listOf(
        Transaction("2026-03-02", "Food", 200.0, "expense"),
        Transaction("2026-03-03", "Groceries", 256.0, "expense"),
        Transaction("2026-03-05", "Transport", 50.0, "expense"),
        Transaction("2026-03-10", "Shopping", 120.0, "expense"),
        Transaction("2026-03-15", "Food", 80.0, "expense"),
        Transaction("2026-03-20", "Groceries", 100.0, "expense"),
        Transaction("2026-03-25", "Entertainment", 75.0, "expense"),
        Transaction("2026-03-28", "Bills", 200.0, "expense"),
        Transaction("2026-03-30", "Food", 45.0, "expense"),
        // Income transactions (not shown in expense report)
        Transaction("2026-03-01", "Salary", 5000.0, "income"),
        Transaction("2026-03-15", "Freelance", 1000.0, "income")
    )

    data class Transaction(
        val date: String,
        val category: String,
        val amount: Double,
        val type: String
    )

    data class CategoryReport(
        val name: String,
        val amount: Double,
        val percentage: Double
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        initializeViews()
        setupBottomNavigation()
        setupFilters()
        loadReport()
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        monthYearText = findViewById(R.id.monthYearText)
        categoryFilterText = findViewById(R.id.categoryFilterText)
        totalExpenses = findViewById(R.id.totalExpenses)
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        monthSelectorCard = findViewById(R.id.monthSelectorCard)
        categoryFilterCard = findViewById(R.id.categoryFilterCard)
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.labelVisibilityMode = com.google.android.material.bottomnavigation.BottomNavigationView.LABEL_VISIBILITY_LABELED

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show()
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
                    // Already on Reports page
                    Toast.makeText(this, "Reports", Toast.LENGTH_SHORT).show()
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

    private fun setupFilters() {
        // Month selector
        updateMonthYearText()
        monthSelectorCard.setOnClickListener {
            showMonthYearPicker()
        }

        // Category filter
        categoryFilterCard.setOnClickListener {
            showFilterOptions()
        }
    }

    private fun updateMonthYearText() {
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = monthFormat.format(currentMonth.time)
    }

    private fun showMonthYearPicker() {
        val year = currentMonth.get(Calendar.YEAR)
        val month = currentMonth.get(Calendar.MONTH)

        val datePickerDialog = android.app.DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, _ ->
                currentMonth.set(selectedYear, selectedMonth, 1)
                updateMonthYearText()
                loadReport()
            },
            year, month, 1
        )
        datePickerDialog.show()
    }

    private fun showFilterOptions() {
        val options = arrayOf("EXP. By Category", "INC. By Category", "Monthly Summary", "Yearly Summary")

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Report Type")
            .setItems(options) { _, which ->
                currentFilter = options[which]
                categoryFilterText.text = currentFilter
                loadReport()
            }
            .show()
    }

    private fun loadReport() {
        when (currentFilter) {
            "EXP. By Category" -> loadExpenseByCategory()
            "INC. By Category" -> loadIncomeByCategory()
            "Monthly Summary" -> loadMonthlySummary()
            "Yearly Summary" -> loadYearlySummary()
            else -> loadExpenseByCategory()
        }
    }

    private fun loadExpenseByCategory() {
        val year = currentMonth.get(Calendar.YEAR)
        val month = currentMonth.get(Calendar.MONTH)

        // Filter transactions for selected month and expense type
        val monthTransactions = transactions.filter { transaction ->
            val dateParts = transaction.date.split("-")
            val transactionYear = dateParts[0].toInt()
            val transactionMonth = dateParts[1].toInt() - 1
            transactionYear == year && transactionMonth == month && transaction.type == "expense"
        }

        // Group by category and sum amounts
        val categoryMap = mutableMapOf<String, Double>()
        monthTransactions.forEach { transaction ->
            categoryMap[transaction.category] = categoryMap.getOrDefault(transaction.category, 0.0) + transaction.amount
        }

        val total = categoryMap.values.sum()
        totalExpenses.text = String.format("%.0f", total)

        // Create report list sorted by amount (highest first)
        val reports = categoryMap.map { (category, amount) ->
            CategoryReport(category, amount, if (total > 0) (amount / total * 100) else 0.0)
        }.sortedByDescending { it.amount }

        setupRecyclerView(reports)
    }

    private fun loadIncomeByCategory() {
        val year = currentMonth.get(Calendar.YEAR)
        val month = currentMonth.get(Calendar.MONTH)

        val monthTransactions = transactions.filter { transaction ->
            val dateParts = transaction.date.split("-")
            val transactionYear = dateParts[0].toInt()
            val transactionMonth = dateParts[1].toInt() - 1
            transactionYear == year && transactionMonth == month && transaction.type == "income"
        }

        val categoryMap = mutableMapOf<String, Double>()
        monthTransactions.forEach { transaction ->
            categoryMap[transaction.category] = categoryMap.getOrDefault(transaction.category, 0.0) + transaction.amount
        }

        val total = categoryMap.values.sum()
        totalExpenses.text = String.format("%.0f", total)

        val reports = categoryMap.map { (category, amount) ->
            CategoryReport(category, amount, if (total > 0) (amount / total * 100) else 0.0)
        }.sortedByDescending { it.amount }

        setupRecyclerView(reports)
    }

    private fun loadMonthlySummary() {
        val year = currentMonth.get(Calendar.YEAR)
        val month = currentMonth.get(Calendar.MONTH)

        val monthTransactions = transactions.filter { transaction ->
            val dateParts = transaction.date.split("-")
            val transactionYear = dateParts[0].toInt()
            val transactionMonth = dateParts[1].toInt() - 1
            transactionYear == year && transactionMonth == month
        }

        val incomeTotal = monthTransactions.filter { it.type == "income" }.sumOf { it.amount }
        val expenseTotal = monthTransactions.filter { it.type == "expense" }.sumOf { it.amount }

        val reports = listOf(
            CategoryReport("Income", incomeTotal, 0.0),
            CategoryReport("Expense", expenseTotal, 0.0),
            CategoryReport("Net", incomeTotal - expenseTotal, 0.0)
        )

        totalExpenses.text = String.format("%.0f", expenseTotal)
        setupRecyclerView(reports)
    }

    private fun loadYearlySummary() {
        val year = currentMonth.get(Calendar.YEAR)

        val yearTransactions = transactions.filter { transaction ->
            val dateParts = transaction.date.split("-")
            val transactionYear = dateParts[0].toInt()
            transactionYear == year
        }

        val monthlyMap = mutableMapOf<String, Double>()

        for (month in 0..11) {
            val monthTransactions = yearTransactions.filter { transaction ->
                val dateParts = transaction.date.split("-")
                val transactionMonth = dateParts[1].toInt() - 1
                transactionMonth == month && transaction.type == "expense"
            }
            val monthTotal = monthTransactions.sumOf { it.amount }
            if (monthTotal > 0) {
                val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(
                    Calendar.getInstance().apply { set(year, month, 1) }.time
                )
                monthlyMap[monthName] = monthTotal
            }
        }

        val total = monthlyMap.values.sum()
        totalExpenses.text = String.format("%.0f", total)

        val reports = monthlyMap.map { (month, amount) ->
            CategoryReport(month, amount, if (total > 0) (amount / total * 100) else 0.0)
        }.sortedByDescending { it.amount }

        setupRecyclerView(reports)
    }

    private fun setupRecyclerView(reports: List<CategoryReport>) {
        categoriesAdapter = CategoriesAdapter(reports)
        categoriesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ReportsActivity)
            adapter = categoriesAdapter
        }
    }

    // Category Adapter
    inner class CategoriesAdapter(
        private val categories: List<CategoryReport>
    ) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_report_category, parent, false)
            return CategoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            holder.bind(categories[position], position + 1)
        }

        override fun getItemCount(): Int = categories.size

        inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val categoryNumber: TextView = itemView.findViewById(R.id.categoryNumber)
            private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
            private val categoryAmount: TextView = itemView.findViewById(R.id.categoryAmount)
            private val categoryRatio: TextView = itemView.findViewById(R.id.categoryRatio)

            fun bind(category: CategoryReport, index: Int) {
                categoryNumber.text = index.toString()
                categoryName.text = category.name

                if (currentFilter == "Monthly Summary") {
                    when (category.name) {
                        "Income" -> {
                            categoryAmount.setTextColor(Color.parseColor("#43A047"))
                            categoryAmount.text = String.format("R %.2f", category.amount)
                            categoryRatio.text = ""
                        }
                        "Expense" -> {
                            categoryAmount.setTextColor(Color.parseColor("#E53935"))
                            categoryAmount.text = String.format("R %.2f", category.amount)
                            categoryRatio.text = ""
                        }
                        "Net" -> {
                            categoryAmount.setTextColor(Color.parseColor("#759ED3"))
                            categoryAmount.text = String.format("R %.2f", category.amount)
                            categoryRatio.text = ""
                        }
                    }
                } else {
                    categoryAmount.text = String.format("R %.0f", category.amount)
                    categoryRatio.text = String.format("%.1f%%", category.percentage)
                }

                itemView.setOnClickListener {
                    Toast.makeText(
                        itemView.context,
                        "${category.name}: R${String.format("%.2f", category.amount)}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}