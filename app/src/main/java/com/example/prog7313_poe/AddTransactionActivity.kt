package com.example.prog7313_poe

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var cancelButton: Button
    private lateinit var expenseButton: Button
    private lateinit var incomeButton: Button
    private lateinit var transferButton: Button
    private lateinit var dateText: TextView
    private lateinit var calendarButton: ImageButton
    private lateinit var amountInput: TextInputEditText
    private lateinit var memoInput: TextInputEditText
    private lateinit var saveButton: Button
    private lateinit var saveContinueButton: Button

    private lateinit var categoryCard: View
    private lateinit var accountCard: View
    private lateinit var memberCard: View
    private lateinit var categoryArrow: ImageButton
    private lateinit var accountArrow: ImageButton
    private lateinit var memberArrow: ImageButton

    private var selectedType = "expense"
    private var selectedDate = Calendar.getInstance()
    private var selectedCategoryId: Int? = null
    private var selectedAccountId: Int? = null
    private var selectedMemberId: Int? = null

    private lateinit var db: AppDatabase
    private var categories = listOf<Category>()
    private var accounts = listOf<Account>()
    private var members = listOf<Member>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        db = AppDatabase.getDatabase(this)
        initViews()
        setupClickListeners()
        setupDatePicker()
        loadDropdownData()
        updateTypeButtonStyles()
    }

    private fun initViews() {
        cancelButton = findViewById(R.id.add_cancelButton)
        expenseButton = findViewById(R.id.add_expenseButton)
        incomeButton = findViewById(R.id.add_incomeButton)
        transferButton = findViewById(R.id.add_transferButton)
        dateText = findViewById(R.id.add_dateText)
        calendarButton = findViewById(R.id.add_buttonCalendar)
        amountInput = findViewById(R.id.add_amountInput)
        memoInput = findViewById(R.id.add_memoInput)
        saveButton = findViewById(R.id.saveButton)
        saveContinueButton = findViewById(R.id.saveContinueButton)

        categoryCard = findViewById(R.id.categoryCard)
        accountCard = findViewById(R.id.accountCard)
        memberCard = findViewById(R.id.choose_memberCard)
        categoryArrow = findViewById(R.id.add_categoryButton)
        accountArrow = findViewById(R.id.add_select_accountButton)
        memberArrow = findViewById(R.id.add_choose_memberCardButton)
    }

    private fun loadDropdownData() {

        categoryCard.isEnabled = false
        accountCard.isEnabled = false
        memberCard.isEnabled = false
        categoryArrow.isEnabled = false
        accountArrow.isEnabled = false
        memberArrow.isEnabled = false


        Thread {
            categories = db.categoryDao().getAll()
            accounts = db.accountDao().getAll()
            members = db.memberDao().getAll()

            runOnUiThread {
                categoryCard.isEnabled = true
                accountCard.isEnabled = true
                memberCard.isEnabled = true
                categoryArrow.isEnabled = true
                accountArrow.isEnabled = true
                memberArrow.isEnabled = true
            }
        }.start()
    }

    private fun setupClickListeners() {
        cancelButton.setOnClickListener { finish() }

        expenseButton.setOnClickListener {
            selectedType = "expense"
            updateTypeButtonStyles()
        }
        incomeButton.setOnClickListener {
            selectedType = "income"
            updateTypeButtonStyles()
        }
        transferButton.setOnClickListener {
            selectedType = "transfer"
            updateTypeButtonStyles()
        }

        categoryCard.setOnClickListener { showCategoryPicker() }
        categoryArrow.setOnClickListener { showCategoryPicker() }
        accountCard.setOnClickListener { showAccountPicker() }
        accountArrow.setOnClickListener { showAccountPicker() }
        memberCard.setOnClickListener { showMemberPicker() }
        memberArrow.setOnClickListener { showMemberPicker() }

        saveButton.setOnClickListener { saveTransaction(shouldFinish = true) }
        saveContinueButton.setOnClickListener { saveTransaction(shouldFinish = false) }
    }

    private fun updateTypeButtonStyles() {
        val expenseColor = R.color.expense_red
        val incomeColor = R.color.income_green
        val transferColor = R.color.custom_blue
        val transparent = android.R.color.transparent

        expenseButton.backgroundTintList = ContextCompat.getColorStateList(
            this, if (selectedType == "expense") expenseColor else transparent
        )
        incomeButton.backgroundTintList = ContextCompat.getColorStateList(
            this, if (selectedType == "income") incomeColor else transparent
        )
        transferButton.backgroundTintList = ContextCompat.getColorStateList(
            this, if (selectedType == "transfer") transferColor else transparent
        )
    }

    private fun setupDatePicker() {
        updateDateText()
        calendarButton.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    updateDateText()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateDateText() {
        val format = java.text.SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        dateText.text = format.format(selectedDate.time)
    }

    private fun showCategoryPicker() {
        val names = categories.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Category")
            .setItems(names) { _, which ->
                selectedCategoryId = categories[which].id
                Toast.makeText(this, "Category: ${categories[which].name}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showAccountPicker() {
        val names = accounts.map { it.accountName }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Account")
            .setItems(names) { _, which ->
                selectedAccountId = accounts[which].id
                Toast.makeText(this, "Account: ${accounts[which].accountName}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showMemberPicker() {
        val names = members.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Member")
            .setItems(names) { _, which ->
                selectedMemberId = members[which].id
                Toast.makeText(this, "Member: ${members[which].name}", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun saveTransaction(shouldFinish: Boolean) {
        val amountStr = amountInput.text.toString()
        if (amountStr.isEmpty()) {
            amountInput.error = "Enter amount"
            return
        }
        if (selectedCategoryId == null) {
            Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedAccountId == null) {
            Toast.makeText(this, "Select account", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedMemberId == null) {
            Toast.makeText(this, "Select member", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val transaction = Transaction(
            name = memoInput.text.toString().ifEmpty { "Transaction" },
            amount = amount,
            transactionType = selectedType,
            date = selectedDate.time,
            accountId = selectedAccountId!!,
            categoryId = selectedCategoryId!!,
            memberId = selectedMemberId!!,
            photo = false
        )

        Thread {
            db.transactionDao().insert(transaction)

            //update the account balance when a transaction is added
            val account = db.accountDao().getById(selectedAccountId!!)
            if (account != null) {
                val updatedAmount = when (selectedType) {
                    "income" -> account.amount + amount
                    "expense" -> account.amount - amount
                    else -> account.amount
                }
                db.accountDao().update(account.copy(amount = updatedAmount))
            }

            runOnUiThread {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
                if (shouldFinish) finish() else clearForm()
            }
        }.start()
    }

    private fun clearForm() {
        amountInput.text?.clear()
        memoInput.text?.clear()
        selectedCategoryId = null
        selectedAccountId = null
        selectedMemberId = null
        selectedType = "expense"
        selectedDate = Calendar.getInstance()
        updateTypeButtonStyles()
        updateDateText()
    }
}