package com.example.prog7313_poe

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var calendarView: CalendarView
    private lateinit var calendarIncome: TextView
    private lateinit var calendarExpense: TextView
    private lateinit var calendarTotal: TextView

    // Sample transaction data for different dates
    private val transactionData = mutableMapOf<String, List<TransactionItem>>()

    data class TransactionItem(
        val amount: Double,
        val category: String,
        val source: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        initializeViews()
        setupBottomNavigation()
        loadSampleTransactions()
        setupCalendar()
        updateSummaryForMonth(getCurrentMonthDate())
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        calendarView = findViewById(R.id.calendarView2)
        calendarIncome = findViewById(R.id.calendarIncome)
        calendarExpense = findViewById(R.id.calendarExpense)
        calendarTotal = findViewById(R.id.calendarTotal)
    }

    private fun setupBottomNavigation() {
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
                    Toast.makeText(this, "Reports clicked", Toast.LENGTH_SHORT).show()
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

    private fun loadSampleTransactions() {
        // Sample transactions with dates

        addTransaction("2026-03-02", TransactionItem(-200.0, "Food", "Bank"))
        addTransaction("2026-03-03", TransactionItem(-256.0, "Groceries", "Bank"))
        addTransaction("2026-03-03", TransactionItem(123.0, "Salary", "Bank"))
        addTransaction("2026-03-04", TransactionItem(50.0, "Bonus", "Cash"))
        addTransaction("2026-03-10", TransactionItem(-75.0, "Shopping", "Bank"))
        addTransaction("2026-03-15", TransactionItem(-120.0, "Dining", "Bank"))
        addTransaction("2026-03-20", TransactionItem(200.0, "Freelance", "Bank"))
        addTransaction("2026-03-25", TransactionItem(-45.0, "Transport", "Bank"))
        addTransaction("2026-03-30", TransactionItem(-300.0, "Rent", "Bank"))

        // Add some transactions for April
        addTransaction("2026-04-01", TransactionItem(500.0, "Salary", "Bank"))
        addTransaction("2026-04-05", TransactionItem(-150.0, "Groceries", "Bank"))
        addTransaction("2026-04-10", TransactionItem(-200.0, "Rent", "Bank"))
    }

    private fun addTransaction(date: String, transaction: TransactionItem) {
        if (transactionData.containsKey(date)) {
            val existingList = transactionData[date]!!.toMutableList()
            existingList.add(transaction)
            transactionData[date] = existingList
        } else {
            transactionData[date] = listOf(transaction)
        }
    }

    private fun setupCalendar() {
        // Set minimum date (optional)
        val calendar = Calendar.getInstance()
        calendar.set(2026, 0, 1) // January 1, 2026
        calendarView.minDate = calendar.timeInMillis

        // Set maximum date (optional)
        calendar.set(2026, 11, 31) // December 31, 2026
        calendarView.maxDate = calendar.timeInMillis

        // Set current date
        calendar.set(2026, 2, 1) // March 1, 2026
        calendarView.date = calendar.timeInMillis

        // Set date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val dateKey = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)

            val transactions = transactionData[dateKey] ?: emptyList()

            if (transactions.isNotEmpty()) {
                val message = buildString {
                    append("Transactions for ${month + 1}/$dayOfMonth/$year:\n")
                    transactions.forEach { transaction ->
                        val sign = if (transaction.amount > 0) "+" else ""
                        append("${transaction.category}: $sign$${String.format("%.2f", transaction.amount)}\n")
                    }
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "No transactions for this date", Toast.LENGTH_SHORT).show()
            }

            // Update summary for the selected month
            updateSummaryForMonth(selectedDate)
        }
    }

    private fun getCurrentMonthDate(): Calendar {
        val calendar = Calendar.getInstance()
        calendar.set(2026, 2, 1) // March 2026
        return calendar
    }

    private fun updateSummaryForMonth(date: Calendar) {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)

        var totalIncome = 0.0
        var totalExpense = 0.0

        // Calculate totals for the selected month
        transactionData.forEach { (dateKey, transactions) ->
            val dateParts = dateKey.split("-")
            val transactionYear = dateParts[0].toInt()
            val transactionMonth = dateParts[1].toInt() - 1

            if (transactionYear == year && transactionMonth == month) {
                transactions.forEach { transaction ->
                    if (transaction.amount > 0) {
                        totalIncome += transaction.amount
                    } else {
                        totalExpense += Math.abs(transaction.amount)
                    }
                }
            }
        }

        val total = totalIncome - totalExpense

        calendarIncome.text = String.format("%.0f", totalIncome)
        calendarExpense.text = String.format("%.0f", totalExpense)
        calendarTotal.text = String.format("%.0f", total)

        // Set color for total
        calendarTotal.setTextColor(
            if (total < 0) ContextCompat.getColor(this, R.color.expense_red)
            else ContextCompat.getColor(this, R.color.income_green)
        )
    }
}