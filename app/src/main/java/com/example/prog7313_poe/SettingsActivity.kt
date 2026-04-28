package com.example.prog7313_poe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

        bottomNav = findViewById(R.id.bottomNavigationView)
        setupBottomNav()
        setupSettingsClickListeners()
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
            showBudgetDialog()
        }
        findViewById<android.view.View>(R.id.categoriesSetting).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }
        findViewById<android.view.View>(R.id.membersSetting).setOnClickListener {
            startActivity(Intent(this, MemberActivity::class.java))
        }
        findViewById<android.view.View>(R.id.pinLockSetting).setOnClickListener {
            val hasPin = prefs.contains("user_pin")
            if (hasPin) {
                startActivity(Intent(this, PinLockActivity::class.java))
            } else {
                showSetupPinDialog()
            }
        }
        findViewById<android.view.View>(R.id.exportCsvSetting).setOnClickListener {
            Toast.makeText(this, "Export CSV coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBudgetDialog() {
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 10)

        val minInput = EditText(this)
        minInput.hint = "Minimum budget (ZAR)"

        val maxInput = EditText(this)
        maxInput.hint = "Maximum budget (ZAR)"

        val currentMin = prefs.getFloat("min_budget", 0f)
        val currentMax = prefs.getFloat("max_budget", 5000f)

        minInput.setText(currentMin.toInt().toString())
        maxInput.setText(currentMax.toInt().toString())

        layout.addView(minInput)
        layout.addView(maxInput)

        AlertDialog.Builder(this)
            .setTitle("Set Budget Range")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val min = minInput.text.toString().toDoubleOrNull()
                val max = maxInput.text.toString().toDoubleOrNull()

                if (min != null && max != null && min <= max) {
                    prefs.edit()
                        .putFloat("min_budget", min.toFloat())
                        .putFloat("max_budget", max.toFloat())
                        .apply()

                    Toast.makeText(this, "Budget range updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Invalid values", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showSetupPinDialog() {
        val input = EditText(this)
        input.hint = "Enter 4-digit PIN"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(this)
            .setTitle("Set PIN Lock")
            .setView(input)
            .setPositiveButton("Set") { _, _ ->
                val pin = input.text.toString()
                if (pin.length == 4 && pin.all { it.isDigit() }) {
                    prefs.edit().putString("user_pin", pin).apply()
                    Toast.makeText(this, "PIN saved", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}