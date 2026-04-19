package com.example.prog7313_poe

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var sharedPreferences: SharedPreferences

    // Setting items
    private lateinit var recurringItemsSetting: LinearLayout
    private lateinit var budgetsSetting: LinearLayout
    private lateinit var categoriesSetting: LinearLayout
    private lateinit var membersSetting: LinearLayout
    private lateinit var themeSetting: LinearLayout
    private lateinit var themeValue: TextView
    private lateinit var pinLockSwitch: SwitchMaterial
    private lateinit var exportCsvSetting: LinearLayout

    // Sample data storage
    private val recurringItems = mutableListOf<RecurringItem>()
    private val budgets = mutableListOf<Budget>()
    private val categories = mutableListOf<String>()
    private val members = mutableListOf<String>()

    data class RecurringItem(
        val name: String,
        val amount: Double,
        val category: String,
        val frequency: String
    )

    data class Budget(
        val category: String,
        val amount: Double
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences = getSharedPreferences("PocketProtectorPrefs", Context.MODE_PRIVATE)

        initializeViews()
        setupBottomNavigation()
        loadSampleData()
        setupClickListeners()
        loadSettings()
    }

    private fun initializeViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        recurringItemsSetting = findViewById(R.id.recurringItemsSetting)
        budgetsSetting = findViewById(R.id.budgetsSetting)
        categoriesSetting = findViewById(R.id.categoriesSetting)
        membersSetting = findViewById(R.id.membersSetting)
        themeSetting = findViewById(R.id.themeSetting)
        themeValue = findViewById(R.id.themeValue)
        pinLockSwitch = findViewById(R.id.pinLockSwitch)
        exportCsvSetting = findViewById(R.id.exportCsvSetting)
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
                    Toast.makeText(this, "Reports clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_settings -> {
                    // Already on Settings page
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadSampleData() {
        // Sample categories
        categories.addAll(listOf(
            "Food", "Groceries", "Transport", "Shopping",
            "Entertainment", "Bills", "Rent", "Healthcare",
            "Education", "Salary", "Freelance", "Investment"
        ))

        // Sample members
        members.addAll(listOf("Self", "Spouse", "Family", "Friend", "Business"))

        // Sample recurring items
        recurringItems.addAll(listOf(
            RecurringItem("Netflix Subscription", 15.99, "Entertainment", "Monthly"),
            RecurringItem("Gym Membership", 49.99, "Healthcare", "Monthly"),
            RecurringItem("Internet Bill", 89.99, "Bills", "Monthly"),
            RecurringItem("Rent", 1500.00, "Rent", "Monthly")
        ))

        // Sample budgets
        budgets.addAll(listOf(
            Budget("Food", 500.0),
            Budget("Groceries", 800.0),
            Budget("Transport", 300.0),
            Budget("Shopping", 400.0),
            Budget("Entertainment", 200.0)
        ))
    }

    private fun setupClickListeners() {
        recurringItemsSetting.setOnClickListener {
            showRecurringItemsDialog()
        }

        budgetsSetting.setOnClickListener {
            showBudgetsDialog()
        }

        categoriesSetting.setOnClickListener {
            showCategoriesDialog()
        }

        membersSetting.setOnClickListener {
            showMembersDialog()
        }

        themeSetting.setOnClickListener {
            showThemeDialog()
        }

        pinLockSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showPinLockSetupDialog()
            } else {
                sharedPreferences.edit().putBoolean("pin_lock_enabled", false).apply()
                Toast.makeText(this, "Pin lock disabled", Toast.LENGTH_SHORT).show()
            }
        }

        exportCsvSetting.setOnClickListener {
            exportDataToCSV()
        }
    }

    private fun loadSettings() {
        // Load theme
        val currentNightMode = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        themeValue.text = if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) "Dark" else "Light"

        // Load pin lock setting
        val pinLockEnabled = sharedPreferences.getBoolean("pin_lock_enabled", false)
        pinLockSwitch.isChecked = pinLockEnabled
    }

    private fun showRecurringItemsDialog() {
        val items = recurringItems.map { "${it.name} - ${it.frequency}: R${String.format("%.2f", it.amount)}" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Recurring Items")
            .setItems(items) { _, which ->
                val item = recurringItems[which]
                showRecurringItemOptions(item, which)
            }
            .setPositiveButton("Add New") { _, _ ->
                showAddRecurringItemDialog()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showRecurringItemOptions(item: RecurringItem, position: Int) {
        val options = arrayOf("Edit", "Delete")

        MaterialAlertDialogBuilder(this)
            .setTitle(item.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditRecurringItemDialog(item, position)
                    1 -> {
                        recurringItems.removeAt(position)
                        Toast.makeText(this, "${item.name} deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showAddRecurringItemDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_recurring_item, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.itemNameInput)
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.amountInput)
        val categoryInput = dialogView.findViewById<TextInputEditText>(R.id.categoryInput)
        val frequencyInput = dialogView.findViewById<TextInputEditText>(R.id.frequencyInput)

        // Setup category dropdown
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryInput.setAdapter(categoryAdapter)
        categoryInput.setOnClickListener { categoryInput.showDropDown() }

        // Setup frequency dropdown
        val frequencies = arrayOf("Daily", "Weekly", "Monthly", "Yearly")
        val frequencyAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, frequencies)
        frequencyInput.setAdapter(frequencyAdapter)
        frequencyInput.setOnClickListener { frequencyInput.showDropDown() }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Recurring Item")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = nameInput.text.toString()
                val amountStr = amountInput.text.toString()
                val category = categoryInput.text.toString()
                val frequency = frequencyInput.text.toString()

                if (name.isNotEmpty() && amountStr.isNotEmpty() && category.isNotEmpty() && frequency.isNotEmpty()) {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    recurringItems.add(RecurringItem(name, amount, category, frequency))
                    Toast.makeText(this, "Recurring item added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditRecurringItemDialog(item: RecurringItem, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_recurring_item, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.itemNameInput)
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.amountInput)
        val categoryInput = dialogView.findViewById<TextInputEditText>(R.id.categoryInput)
        val frequencyInput = dialogView.findViewById<TextInputEditText>(R.id.frequencyInput)

        nameInput.setText(item.name)
        amountInput.setText(item.amount.toString())
        categoryInput.setText(item.category)
        frequencyInput.setText(item.frequency)

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Recurring Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString()
                val amountStr = amountInput.text.toString()
                val category = categoryInput.text.toString()
                val frequency = frequencyInput.text.toString()

                if (name.isNotEmpty() && amountStr.isNotEmpty()) {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    recurringItems[position] = RecurringItem(name, amount, category, frequency)
                    Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showBudgetsDialog() {
        val budgetItems = budgets.map { "${it.category}: R${String.format("%.2f", it.amount)}" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Budgets")
            .setItems(budgetItems) { _, which ->
                val budget = budgets[which]
                showBudgetOptions(budget, which)
            }
            .setPositiveButton("Add New") { _, _ ->
                showAddBudgetDialog()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showBudgetOptions(budget: Budget, position: Int) {
        val options = arrayOf("Edit", "Delete")

        MaterialAlertDialogBuilder(this)
            .setTitle(budget.category)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditBudgetDialog(budget, position)
                    1 -> {
                        budgets.removeAt(position)
                        Toast.makeText(this, "Budget deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showAddBudgetDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_budget, null)
        val categoryInput = dialogView.findViewById<TextInputEditText>(R.id.categoryInput)
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.budgetAmountInput)

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryInput.setAdapter(categoryAdapter)
        categoryInput.setOnClickListener { categoryInput.showDropDown() }

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Budget")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val category = categoryInput.text.toString()
                val amountStr = amountInput.text.toString()

                if (category.isNotEmpty() && amountStr.isNotEmpty()) {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    budgets.add(Budget(category, amount))
                    Toast.makeText(this, "Budget added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditBudgetDialog(budget: Budget, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_budget, null)
        val categoryInput = dialogView.findViewById<TextInputEditText>(R.id.categoryInput)
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.budgetAmountInput)

        categoryInput.setText(budget.category)
        amountInput.setText(budget.amount.toString())

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Budget")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val category = categoryInput.text.toString()
                val amountStr = amountInput.text.toString()

                if (category.isNotEmpty() && amountStr.isNotEmpty()) {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    budgets[position] = Budget(category, amount)
                    Toast.makeText(this, "Budget updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCategoriesDialog() {
        val categoryArray = categories.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Categories")
            .setItems(categoryArray) { _, which ->
                showCategoryOptions(which)
            }
            .setPositiveButton("Add New") { _, _ ->
                showAddCategoryDialog()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showCategoryOptions(position: Int) {
        val options = arrayOf("Edit", "Delete")

        MaterialAlertDialogBuilder(this)
            .setTitle(categories[position])
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditCategoryDialog(position)
                    1 -> {
                        categories.removeAt(position)
                        Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showAddCategoryDialog() {
        val input = TextInputEditText(this)
        input.hint = "Category Name"

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Category")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val category = input.text.toString()
                if (category.isNotEmpty()) {
                    categories.add(category)
                    Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditCategoryDialog(position: Int) {
        val input = TextInputEditText(this)
        input.hint = "Category Name"
        input.setText(categories[position])

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Category")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newCategory = input.text.toString()
                if (newCategory.isNotEmpty()) {
                    categories[position] = newCategory
                    Toast.makeText(this, "Category updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showMembersDialog() {
        val memberArray = members.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Members")
            .setItems(memberArray) { _, which ->
                showMemberOptions(which)
            }
            .setPositiveButton("Add New") { _, _ ->
                showAddMemberDialog()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showMemberOptions(position: Int) {
        val options = arrayOf("Edit", "Delete")

        MaterialAlertDialogBuilder(this)
            .setTitle(members[position])
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showEditMemberDialog(position)
                    1 -> {
                        members.removeAt(position)
                        Toast.makeText(this, "Member deleted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showAddMemberDialog() {
        val input = TextInputEditText(this)
        input.hint = "Member Name"

        MaterialAlertDialogBuilder(this)
            .setTitle("Add Member")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val member = input.text.toString()
                if (member.isNotEmpty()) {
                    members.add(member)
                    Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditMemberDialog(position: Int) {
        val input = TextInputEditText(this)
        input.hint = "Member Name"
        input.setText(members[position])

        MaterialAlertDialogBuilder(this)
            .setTitle("Edit Member")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newMember = input.text.toString()
                if (newMember.isNotEmpty()) {
                    members[position] = newMember
                    Toast.makeText(this, "Member updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Light", "Dark", "System Default")

        MaterialAlertDialogBuilder(this)
            .setTitle("Select Theme")
            .setItems(themes) { _, which ->
                when (which) {
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        themeValue.text = "Light"
                        sharedPreferences.edit().putString("theme", "light").apply()
                    }
                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        themeValue.text = "Dark"
                        sharedPreferences.edit().putString("theme", "dark").apply()
                    }
                    2 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        themeValue.text = "System"
                        sharedPreferences.edit().putString("theme", "system").apply()
                    }
                }
            }
            .show()
    }

    private fun showPinLockSetupDialog() {
        val pinInput = TextInputEditText(this)
        pinInput.hint = "Enter 4-digit PIN"
        pinInput.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        MaterialAlertDialogBuilder(this)
            .setTitle("Set Up Pin Lock")
            .setView(pinInput)
            .setPositiveButton("Set PIN") { _, _ ->
                val pin = pinInput.text.toString()
                if (pin.length == 4 && pin.all { it.isDigit() }) {
                    sharedPreferences.edit()
                        .putBoolean("pin_lock_enabled", true)
                        .putString("user_pin", pin)
                        .apply()
                    Toast.makeText(this, "Pin lock enabled", Toast.LENGTH_SHORT).show()
                } else {
                    pinLockSwitch.isChecked = false
                    Toast.makeText(this, "Please enter a valid 4-digit PIN", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                pinLockSwitch.isChecked = false
            }
            .show()
    }

    private fun exportDataToCSV() {
        try {
            val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            val fileName = "pocket_protector_export_${dateFormat.format(Date())}.csv"
            val file = File(getExternalFilesDir(null), fileName)

            val fileOutputStream = FileOutputStream(file)
            val writer = fileOutputStream.bufferedWriter()

            // Write headers
            writer.write("Recurring Items\n")
            writer.write("Name,Amount,Category,Frequency\n")
            recurringItems.forEach {
                writer.write("${it.name},${it.amount},${it.category},${it.frequency}\n")
            }

            writer.write("\nBudgets\n")
            writer.write("Category,Amount\n")
            budgets.forEach {
                writer.write("${it.category},${it.amount}\n")
            }

            writer.write("\nCategories\n")
            categories.forEach {
                writer.write("$it\n")
            }

            writer.write("\nMembers\n")
            members.forEach {
                writer.write("$it\n")
            }

            writer.close()
            Toast.makeText(this, "Exported to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Export failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}