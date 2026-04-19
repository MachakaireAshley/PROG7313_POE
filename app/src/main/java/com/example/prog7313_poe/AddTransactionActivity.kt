package com.example.prog7313_poe

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var expenseButton: MaterialButton
    private lateinit var incomeButton: MaterialButton
    private lateinit var transferButton: MaterialButton
    private lateinit var dateText: TextView
    private lateinit var amountInput: TextInputEditText
    private lateinit var categoryInput: TextInputEditText
    private lateinit var accountInput: TextInputEditText
    private lateinit var transferAccountLayout: TextInputLayout
    private lateinit var transferAccountInput: TextInputEditText
    private lateinit var memberInput: TextInputEditText
    private lateinit var memoInput: TextInputEditText
    private lateinit var receiptPhotoCard: MaterialCardView
    private lateinit var saveButton: MaterialButton
    private lateinit var saveContinueButton: MaterialButton

    private var currentTransactionType = "expense" // expense, income, transfer
    private var selectedDate = Calendar.getInstance()
    private var selectedCategory = ""
    private var selectedAccount = ""
    private var selectedTransferAccount = ""
    private var selectedMember = ""
    private var receiptImageUri: Uri? = null

    // Sample data for dropdowns
    private val expenseCategories = listOf("Food", "Groceries", "Transport", "Shopping", "Entertainment", "Bills", "Rent", "Healthcare", "Education", "Other")
    private val incomeCategories = listOf("Salary", "Freelance", "Investment", "Gift", "Refund", "Bonus", "Other")
    private val accounts = listOf("Bank Account", "Cash Wallet", "Credit Card", "Savings Account")
    private val members = listOf("Self", "Spouse", "Family", "Friend", "Other")

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        receiptImageUri = uri
        if (uri != null) {
            Toast.makeText(this, "Receipt photo attached", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        initializeViews()
        setupClickListeners()
        setupDropdowns()
        updateDateText()
    }

    private fun initializeViews() {
        expenseButton = findViewById(R.id.expenseButton)
        incomeButton = findViewById(R.id.incomeButton)
        transferButton = findViewById(R.id.transferButton)
        dateText = findViewById(R.id.dateText)
        amountInput = findViewById(R.id.amountInput)
        categoryInput = findViewById(R.id.categoryInput)
        accountInput = findViewById(R.id.accountInput)
        transferAccountLayout = findViewById(R.id.transferAccountLayout)
        transferAccountInput = findViewById(R.id.transferAccountInput)
        memberInput = findViewById(R.id.memberInput)
        memoInput = findViewById(R.id.memoInput)
        receiptPhotoCard = findViewById(R.id.receiptPhotoCard)
        saveButton = findViewById(R.id.saveButton)
        saveContinueButton = findViewById(R.id.saveContinueButton)
    }

    private fun setupClickListeners() {
        expenseButton.setOnClickListener {
            setTransactionType("expense")
        }

        incomeButton.setOnClickListener {
            setTransactionType("income")
        }

        transferButton.setOnClickListener {
            setTransactionType("transfer")
        }

        dateText.setOnClickListener {
            showDatePicker()
        }

        receiptPhotoCard.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        saveButton.setOnClickListener {
            if (validateInputs()) {
                saveTransaction()
                finish()
            }
        }

        saveContinueButton.setOnClickListener {
            if (validateInputs()) {
                saveTransaction()
                clearForm()
            }
        }
    }

    private fun setTransactionType(type: String) {
        currentTransactionType = type

        // Reset button styles
        expenseButton.backgroundTintList = getColorStateList(R.color.custom_blue)
        incomeButton.backgroundTintList = getColorStateList(R.color.custom_blue)
        transferButton.backgroundTintList = getColorStateList(R.color.custom_blue)
        transferButton.strokeWidth = 1

        // Set selected button style
        when (type) {
            "expense" -> {
                expenseButton.backgroundTintList = getColorStateList(R.color.expense_red)
                transferAccountLayout.visibility = android.view.View.GONE
                updateCategoryDropdown(expenseCategories)
            }
            "income" -> {
                incomeButton.backgroundTintList = getColorStateList(R.color.income_green)
                transferAccountLayout.visibility = android.view.View.GONE
                updateCategoryDropdown(incomeCategories)
            }
            "transfer" -> {
                transferButton.backgroundTintList = getColorStateList(R.color.custom_blue)
                transferButton.strokeWidth = 0
                transferAccountLayout.visibility = android.view.View.VISIBLE
                updateCategoryDropdown(listOf("Transfer"))
                categoryInput.setText("Transfer")
                categoryInput.isEnabled = false
            }
        }
    }

    private fun updateCategoryDropdown(categories: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        categoryInput.setAdapter(adapter)
        categoryInput.setText("")
        categoryInput.isEnabled = true
    }

    private fun setupDropdowns() {
        // Category dropdown
        updateCategoryDropdown(expenseCategories)
        categoryInput.setOnClickListener {
            categoryInput.showDropDown()
        }

        // Account dropdown
        val accountAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, accounts)
        accountInput.setAdapter(accountAdapter)
        accountInput.setOnClickListener {
            accountInput.showDropDown()
        }
        accountInput.setOnItemClickListener { _, _, position, _ ->
            selectedAccount = accounts[position]
        }

        // Transfer Account dropdown
        val transferAccountAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, accounts)
        transferAccountInput.setAdapter(transferAccountAdapter)
        transferAccountInput.setOnClickListener {
            transferAccountInput.showDropDown()
        }
        transferAccountInput.setOnItemClickListener { _, _, position, _ ->
            selectedTransferAccount = accounts[position]
        }

        // Member dropdown
        val memberAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, members)
        memberInput.setAdapter(memberAdapter)
        memberInput.setOnClickListener {
            memberInput.showDropDown()
        }
        memberInput.setOnItemClickListener { _, _, position, _ ->
            selectedMember = members[position]
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateDateText()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateText() {
        val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
        dateText.text = dateFormat.format(selectedDate.time)
    }

    private fun validateInputs(): Boolean {
        val amount = amountInput.text.toString()
        val category = categoryInput.text.toString()
        val account = accountInput.text.toString()

        when {
            amount.isEmpty() -> {
                amountInput.error = "Please enter an amount"
                return false
            }
            amount.toDoubleOrNull() == null -> {
                amountInput.error = "Please enter a valid number"
                return false
            }
            category.isEmpty() -> {
                categoryInput.error = "Please select a category"
                return false
            }
            account.isEmpty() -> {
                accountInput.error = "Please select an account"
                return false
            }
            currentTransactionType == "transfer" && transferAccountInput.text.toString().isEmpty() -> {
                transferAccountInput.error = "Please select account to transfer to"
                return false
            }
            currentTransactionType == "transfer" && transferAccountInput.text.toString() == account -> {
                transferAccountInput.error = "Cannot transfer to the same account"
                return false
            }
            else -> return true
        }
    }

    private fun saveTransaction() {
        val amount = amountInput.text.toString().toDouble()
        val category = categoryInput.text.toString()
        val account = accountInput.text.toString()
        val memo = memoInput.text.toString()
        val member = memberInput.text.toString()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
        val type = currentTransactionType

        // Create transaction object
        val transaction = Transaction(
            date = date,
            category = category,
            amount = amount,
            type = type,
            account = account,
            member = member,
            memo = memo,
            receiptUri = receiptImageUri?.toString()
        )

        // Save to SharedPreferences or Database
        saveTransactionToStorage(transaction)

        Toast.makeText(this, "Transaction saved successfully!", Toast.LENGTH_SHORT).show()
    }
    private fun clearForm() {
        amountInput.text?.clear()
        categoryInput.setText("")
        accountInput.setText("")
        transferAccountInput.setText("")
        memberInput.setText("")
        memoInput.text?.clear()
        receiptImageUri = null
        amountInput.requestFocus()

        Toast.makeText(this, "Form cleared. Ready for next transaction.", Toast.LENGTH_SHORT).show()
    }
}