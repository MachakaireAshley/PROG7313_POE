package com.example.prog7313_poe

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

class AccountsActivity : AppCompatActivity() {

    private lateinit var netAssetsAmount: TextView
    private lateinit var accountIncomeAmount: TextView
    private lateinit var accountExpenseAmount: TextView
    private lateinit var accountsRecyclerView: RecyclerView
    private lateinit var addAccountButton: Button
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        db = AppDatabase.getDatabase(this)
        initViews()
        setupBottomNav()
        setupAddButton()
        loadAccounts()
        loadSummary()
    }

    private fun initViews() {
        netAssetsAmount = findViewById(R.id.netAssetsAmount)
        accountIncomeAmount = findViewById(R.id.account_incomeAmount)
        accountExpenseAmount = findViewById(R.id.account_expenseAmount)
        accountsRecyclerView = findViewById(R.id.accountsRecyclerView)
        addAccountButton = findViewById(R.id.addAccountButton)
        bottomNav = findViewById(R.id.bottomNavigationView)

        accountsRecyclerView.layoutManager = LinearLayoutManager(this)
        accountAdapter = AccountAdapter(emptyList())
        accountsRecyclerView.adapter = accountAdapter
    }

    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_accounts
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeListActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_accounts -> true
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

    private fun setupAddButton() {
        addAccountButton.setOnClickListener {
            showAddAccountDialog()
        }
    }

    private fun showAddAccountDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add New Account")
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_account, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.dialog_account_name)
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.dialog_account_amount)

        builder.setView(dialogView)
        builder.setPositiveButton("Save") { _, _ ->
            val name = nameInput.text.toString().trim()
            val amountStr = amountInput.text.toString().trim()
            if (name.isNotEmpty() && amountStr.isNotEmpty()) {
                val amount = amountStr.toDoubleOrNull() ?: 0.0
                val account = Account(accountName = name, amount = amount)
                Thread {
                    db.accountDao().insert(account)
                    runOnUiThread {
                        loadAccounts()
                        loadSummary()
                        Toast.makeText(this, "Account added", Toast.LENGTH_SHORT).show()
                    }
                }.start()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun loadAccounts() {
        Thread {
            val accounts = db.accountDao().getAll()
            runOnUiThread {
                accountAdapter.updateData(accounts)
            }
        }.start()
    }

    private fun loadSummary() {
        Thread {
            val now = java.util.Date()
            val start = getStartOfMonth(now)
            val end = getEndOfMonth(now)
            val incomes = db.transactionDao().getByTypeBetweenDates("income", start, end)
            val expenses = db.transactionDao().getByTypeBetweenDates("expense", start, end)
            val totalIncome = incomes.sumOf { it.amount }
            val totalExpense = expenses.sumOf { it.amount }
            //sum of all money within all accounts
            val accounts = db.accountDao().getAll()
            val netAssets = accounts.sumOf { it.amount }

            runOnUiThread {
                accountIncomeAmount.text = "R %.2f".format(totalIncome)
                accountExpenseAmount.text = "R %.2f".format(totalExpense)
                netAssetsAmount.text = "R %.2f".format(netAssets)
            }
        }.start()
    }

    private fun getStartOfMonth(date: java.util.Date): java.util.Date {
        val cal = java.util.Calendar.getInstance().apply { time = date }
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        return cal.time
    }

    private fun getEndOfMonth(date: java.util.Date): java.util.Date {
        val cal = java.util.Calendar.getInstance().apply { time = date }
        cal.set(java.util.Calendar.DAY_OF_MONTH, cal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23)
        cal.set(java.util.Calendar.MINUTE, 59)
        cal.set(java.util.Calendar.SECOND, 59)
        return cal.time
    }
}