package com.example.prog7313_poe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class AccountsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var accountsRecyclerView: RecyclerView
    private lateinit var addAccountButton: MaterialButton
    private lateinit var netAssetsAmount: TextView
    private lateinit var incomeAmount: TextView
    private lateinit var expenseAmount: TextView

    private lateinit var accountsAdapter: AccountsAdapter
    private val accountsList = mutableListOf<Account>()

    data class Account(
        val id: Int,
        val name: String,
        val balance: Double,
        val currency: String,
        val iconRes: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

        initializeViews()
        setupBottomNavigation()
        loadSampleAccounts()
        setupAccountsList()
        updateSummary()
        setupClickListeners()
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        accountsRecyclerView = findViewById(R.id.accountsRecyclerView)
        addAccountButton = findViewById(R.id.addAccountButton)
        netAssetsAmount = findViewById(R.id.netAssetsAmount)
        incomeAmount = findViewById(R.id.incomeAmount)
        expenseAmount = findViewById(R.id.expenseAmount)
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
                    // Already on Accounts page
                    Toast.makeText(this, "Accounts", Toast.LENGTH_SHORT).show()
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

    private fun loadSampleAccounts() {
        accountsList.addAll(
            listOf(
                Account(1, "Bank", 2500.00, "ZAR", R.drawable.ic_bank),
                Account(2, "Cash", 500.00, "ZAR", R.drawable.ic_cash),
                Account(3, "Credit Card", -1000.00, "USD", R.drawable.ic_credit_card)
            )
        )
    }

    private fun setupAccountsList() {
        accountsAdapter = AccountsAdapter(accountsList) { account ->
            // Handle account click
            Toast.makeText(this, "Selected: ${account.name}", Toast.LENGTH_SHORT).show()
        }

        accountsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AccountsActivity)
            adapter = accountsAdapter
        }
    }

    private fun updateSummary() {
        var totalIncome = 0.0
        var totalExpense = 0.0
        var netAssets = 0.0

        accountsList.forEach { account ->
            netAssets += account.balance
            if (account.balance > 0) {
                totalIncome += account.balance
            } else {
                totalExpense += Math.abs(account.balance)
            }
        }

        netAssetsAmount.text = String.format("R %,.0f", netAssets)
        incomeAmount.text = String.format("R %,.0f", totalIncome)
        expenseAmount.text = String.format("R %,.0f", totalExpense)

        // Set colors
        if (netAssets < 0) {
            netAssetsAmount.setTextColor(getColor(R.color.expense_red))
        } else {
            netAssetsAmount.setTextColor(getColor(R.color.custom_blue))
        }
    }

    private fun setupClickListeners() {
        addAccountButton.setOnClickListener {
            Toast.makeText(this, "Add new account feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    // Account Adapter
    inner class AccountsAdapter(
        private val accounts: List<Account>,
        private val onItemClick: (Account) -> Unit
    ) : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_account, parent, false)
            return AccountViewHolder(view, onItemClick)
        }

        override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
            holder.bind(accounts[position])
        }

        override fun getItemCount(): Int = accounts.size

        inner class AccountViewHolder(
            itemView: View,
            private val onItemClick: (Account) -> Unit
        ) : RecyclerView.ViewHolder(itemView) {

            private val accountIcon: ImageView = itemView.findViewById(R.id.accountIcon)
            private val accountName: TextView = itemView.findViewById(R.id.accountName)
            private val accountBalance: TextView = itemView.findViewById(R.id.accountBalance)
            private val accountCurrency: TextView = itemView.findViewById(R.id.accountCurrency)

            fun bind(account: Account) {
                accountIcon.setImageResource(account.iconRes)
                accountName.text = account.name
                accountBalance.text = String.format("R %,.2f", account.balance)
                accountCurrency.text = account.currency

                // Set balance color based on positive/negative
                if (account.balance < 0) {
                    accountBalance.setTextColor(itemView.context.getColor(R.color.expense_red))
                } else {
                    accountBalance.setTextColor(itemView.context.getColor(R.color.custom_blue))
                }

                itemView.setOnClickListener { onItemClick(account) }
            }
        }
    }
}