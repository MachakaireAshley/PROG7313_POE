package com.example.prog7313_poe

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var categoryCard: android.view.View
    private lateinit var accountCard: android.view.View
    private lateinit var memberCard: android.view.View
    private lateinit var saveButton: Button
    private lateinit var saveContinueButton: Button

    private var selectedType = "expense"
    private var selectedDate = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        initViews()
        setupClickListeners()
        setupDatePicker()
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
    }

    private fun setupClickListeners() {
        cancelButton.setOnClickListener {
            finish()
        }

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

        categoryCard.setOnClickListener {
            Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show()
        }

        accountCard.setOnClickListener {
            Toast.makeText(this, "Select account", Toast.LENGTH_SHORT).show()
        }

        memberCard.setOnClickListener {
            Toast.makeText(this, "Select member", Toast.LENGTH_SHORT).show()
        }

        saveButton.setOnClickListener {
            saveTransaction()
        }

        saveContinueButton.setOnClickListener {
            saveTransaction()
            clearForm()
        }
    }

    private fun updateTypeButtonStyles() {
        // Simple styling - can be enhanced
        expenseButton.isSelected = selectedType == "expense"
        incomeButton.isSelected = selectedType == "income"
        transferButton.isSelected = selectedType == "transfer"
    }

    private fun setupDatePicker() {
        updateDateText()

        calendarButton.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate.set(year, month, dayOfMonth)
                    updateDateText()
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }

    private fun updateDateText() {
        val dateFormat = java.text.SimpleDateFormat("EEE, dd MMM yyyy", java.util.Locale.getDefault())
        dateText.text = dateFormat.format(selectedDate.time)
    }

    private fun saveTransaction() {
        val amount = amountInput.text.toString()

        if (amount.isEmpty()) {
            amountInput.error = "Please enter amount"
            return
        }

        Toast.makeText(this, "Transaction saved: $selectedType - $amount", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun clearForm() {
        amountInput.text?.clear()
    }
}