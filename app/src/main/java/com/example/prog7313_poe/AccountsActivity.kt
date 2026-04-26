package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AccountsActivity : AppCompatActivity() {

    private lateinit var netAssetsAmount: TextView
    private lateinit var accountIncomeAmount: TextView
    private lateinit var accountExpenseAmount: TextView
    private lateinit var accountsRecyclerView: RecyclerView
    private lateinit var addAccountButton: Button
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        initViews()
        setupBottomNav()
        setupAddButton()
        loadAccounts()
    }

    private fun initViews() {
        netAssetsAmount = findViewById(R.id.netAssetsAmount)
        accountIncomeAmount = findViewById(R.id.account_incomeAmount)
        accountExpenseAmount = findViewById(R.id.account_expenseAmount)
        accountsRecyclerView = findViewById(R.id.accountsRecyclerView)
        addAccountButton = findViewById(R.id.addAccountButton)
        bottomNav = findViewById(R.id.bottomNavigationView)

        accountsRecyclerView.layoutManager = LinearLayoutManager(this)
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
            Toast.makeText(this, "Add account feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAccounts() {
        val accounts = listOf(
            Account("Bank Account", "R 2,500", "ZAR", R.drawable.ic_account_bank),
            Account("Cash", "R 1,200", "ZAR", R.drawable.ic_account_cash),
            Account("Savings", "R 300", "ZAR", R.drawable.ic_account_savings)
        )

        val adapter = AccountAdapter(accounts)
        accountsRecyclerView.adapter = adapter

        accountIncomeAmount.text = "R 1,234"
        accountExpenseAmount.text = "R 456"
        netAssetsAmount.text = "R 4,000"
    }

    data class Account(val name: String, val balance: String, val currency: String, val iconRes: Int)
}