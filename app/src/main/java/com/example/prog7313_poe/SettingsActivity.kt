package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        setupBottomNav()
        setupSettingsClickListeners()
    }

    private fun initViews() {
        bottomNav = findViewById(R.id.bottomNavigationView)
    }

    private fun setupBottomNav() {
        bottomNav.selectedItemId = R.id.nav_settings
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
                R.id.nav_reports -> {
                    startActivity(Intent(this, ReportsActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_settings -> true
                else -> false
            }
        }
    }

    private fun setupSettingsClickListeners() {
        findViewById<android.view.View>(R.id.recurringItemsSetting).setOnClickListener {
            startActivity(Intent(this, RecurringItemActivity::class.java))
        }

        findViewById<android.view.View>(R.id.budgetsSetting).setOnClickListener {
            Toast.makeText(this, "Budget settings coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.categoriesSetting).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        findViewById<android.view.View>(R.id.membersSetting).setOnClickListener {
            startActivity(Intent(this, MemberActivity::class.java))
        }

        findViewById<android.view.View>(R.id.pinLockSetting).setOnClickListener {
            Toast.makeText(this, "PIN setup coming soon", Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.exportCsvSetting).setOnClickListener {
            Toast.makeText(this, "Export feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}