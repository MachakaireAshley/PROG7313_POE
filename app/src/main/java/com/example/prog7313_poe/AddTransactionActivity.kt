package com.example.prog7313_poe

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var cancelButton: Button
    private lateinit var expenseButton: Button
    private lateinit var incomeButton: Button
    private lateinit var transferButton: Button
    private lateinit var dateText: TextView
    private lateinit var calendarButton: ImageButton
    private lateinit var amountInput: TextInputEditText
    private lateinit var categoryCard: View
    private lateinit var accountCard: View
    private lateinit var memberCard: View
    private lateinit var saveButton: Button
    private lateinit var saveContinueButton: Button

    private lateinit var receiptPhotoCard: View
    private lateinit var attachButton: ImageButton
    private lateinit var memoInput: TextInputEditText
    private lateinit var categoryArrowButton: ImageButton
    private lateinit var accountArrowButton: ImageButton
    private lateinit var memberArrowButton: ImageButton

    private var selectedType = "expense"
    private var selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        initViews()
        setupClickListeners()
        setupDatePicker()
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
        categoryCard = findViewById(R.id.categoryCard)
        accountCard = findViewById(R.id.accountCard)
        memberCard = findViewById(R.id.choose_memberCard)
        saveButton = findViewById(R.id.saveButton)
        saveContinueButton = findViewById(R.id.saveContinueButton)

        receiptPhotoCard = findViewById(R.id.receiptPhotoCard)
        attachButton = findViewById(R.id.add_attachButton)
        memoInput = findViewById(R.id.add_memoInput)
        categoryArrowButton = findViewById(R.id.add_categoryButton)
        accountArrowButton = findViewById(R.id.add_select_accountButton)
        memberArrowButton = findViewById(R.id.add_choose_memberCardButton)
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

        // Category selection (card + arrow)
        categoryCard.setOnClickListener { showCategoryPicker() }
        categoryArrowButton.setOnClickListener { showCategoryPicker() }

        // Account selection
        accountCard.setOnClickListener { showAccountPicker() }
        accountArrowButton.setOnClickListener { showAccountPicker() }

        // Member selection
        memberCard.setOnClickListener { showMemberPicker() }
        memberArrowButton.setOnClickListener { showMemberPicker() }

        // Receipt photo
        receiptPhotoCard.setOnClickListener { showPhotoPicker() }
        attachButton.setOnClickListener { showPhotoPicker() }

        saveButton.setOnClickListener { saveTransaction(shouldFinish = true) }
        saveContinueButton.setOnClickListener { saveTransaction(shouldFinish = false) }
    }

    private fun updateTypeButtonStyles() {
        // Background tints - using resource IDs
        val expenseColorRes = R.color.expense_red
        val incomeColorRes = R.color.income_green
        val transferColorRes = R.color.custom_blue
        val transparentRes = android.R.color.transparent

        expenseButton.backgroundTintList = ContextCompat.getColorStateList(
            this, if (selectedType == "expense") expenseColorRes else transparentRes
        )
        incomeButton.backgroundTintList = ContextCompat.getColorStateList(
            this, if (selectedType == "income") incomeColorRes else transparentRes
        )
        transferButton.backgroundTintList = ContextCompat.getColorStateList(
            this, if (selectedType == "transfer") transferColorRes else transparentRes
        )

        // Text colors - using color integers for simplicity
        val white = ContextCompat.getColor(this, android.R.color.white)
        val darkText = ContextCompat.getColor(this, android.R.color.black) // fallback, but white is fine

        expenseButton.setTextColor(if (selectedType == "expense") white else white)
        incomeButton.setTextColor(if (selectedType == "income") white else white)
        transferButton.setTextColor(if (selectedType == "transfer") white else white)
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
        val dateFormat = java.text.SimpleDateFormat("EEE, dd MMM yyyy", java.util.Locale.getDefault())
        dateText.text = dateFormat.format(selectedDate.time)
    }

    private fun saveTransaction(shouldFinish: Boolean) {
        val amount = amountInput.text.toString()
        if (amount.isEmpty()) {
            amountInput.error = "Please enter amount"
            return
        }

        // Placeholder: future database insertion
        Toast.makeText(this, "Transaction saved: $selectedType - $amount", Toast.LENGTH_SHORT).show()

        if (shouldFinish) {
            finish()
        } else {
            clearForm()
        }
    }

    private fun clearForm() {
        amountInput.text?.clear()
        memoInput.text?.clear()
        selectedType = "expense"
        updateTypeButtonStyles()
        selectedDate = Calendar.getInstance()
        updateDateText()
    }

    private fun showCategoryPicker() {
        Toast.makeText(this, "Select category (to be connected to database)", Toast.LENGTH_SHORT).show()
    }

    private fun showAccountPicker() {
        Toast.makeText(this, "Select account (to be connected to database)", Toast.LENGTH_SHORT).show()
    }

    private fun showMemberPicker() {
        Toast.makeText(this, "Select member (to be connected to database)", Toast.LENGTH_SHORT).show()
    }

    private fun showPhotoPicker() {
        Toast.makeText(this, "Attach receipt photo (feature coming soon)", Toast.LENGTH_SHORT).show()
    }
}