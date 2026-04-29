package com.example.prog7313_poe

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import android.widget.ImageView
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat

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
    //for the photos
    private lateinit var photoButton: View
    private lateinit var photoPreview: ImageView

    // Start and End Time views
    private lateinit var startTimeText: TextView
    private lateinit var endTimeText: TextView
    private lateinit var startTimeButton: ImageButton
    private lateinit var endTimeButton: ImageButton

    private var selectedType = "expense"
    private var selectedDate = Calendar.getInstance()
    private var selectedCategoryId: Int? = null
    private var selectedAccountId: Int? = null
    private var selectedMemberId: Int? = null


    private var selectedStartTime: Date = Date()  // Initialize with current date/time
    private var selectedEndTime: Date = Date()    // Initialize with current date/time

    private lateinit var db: AppDatabase
    private var categories = listOf<Category>()
    private var accounts = listOf<Account>()
    private var members = listOf<Member>()

    //photo variables
    private var pendingPhotoUri: Uri? = null
    private var photoPath: String? = null

    // Flag to track if user has actually selected a time
    private var isStartTimeSelected = false
    private var isEndTimeSelected = false

    //taking a photo
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        // This runs after the user takes a photo
        // success = true means the photo was taken successfully
        if (success) {
            photoPreview.setImageURI(pendingPhotoUri)
            photoPreview.visibility = View.VISIBLE
        }
    }

    //selecting an image from gallery
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        uri?.let {
            val file = copyUriToFile(it)
            photoPath = file.absolutePath
            photoPreview.setImageURI(it)
            photoPreview.visibility = View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)
        db = AppDatabase.getDatabase(this)
        initViews()
        setupClickListeners()
        setupDatePicker()

        setupTimePickers()
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

        photoButton = findViewById(R.id.receiptPhotoCard)
        photoPreview = findViewById(R.id.add_photoPreview)

        // NEW: Initialize time views
        startTimeText = findViewById(R.id.add_startTimeText)
        endTimeText = findViewById(R.id.add_endTimeText)
        startTimeButton = findViewById(R.id.add_startTimeButton)
        endTimeButton = findViewById(R.id.add_endTimeButton)
    }

   //Setup time pickers
    private fun setupTimePickers() {
        updateStartTimeText()
        updateEndTimeText()

        startTimeButton.setOnClickListener {
            showTimePicker(isStartTime = true)
        }

        endTimeButton.setOnClickListener {
            showTimePicker(isStartTime = false)
        }
    }

    // Show time picker dialog
    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()

        // Use existing time if available
        if (isStartTime && isStartTimeSelected) {
            calendar.time = selectedStartTime
        } else if (!isStartTime && isEndTimeSelected) {
            calendar.time = selectedEndTime
        }

        TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val timeCalendar = Calendar.getInstance()
                // Use the selected date as base
                timeCalendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
                timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                timeCalendar.set(Calendar.MINUTE, minute)
                timeCalendar.set(Calendar.SECOND, 0)

                if (isStartTime) {
                    selectedStartTime = timeCalendar.time
                    isStartTimeSelected = true
                    updateStartTimeText()
                } else {
                    selectedEndTime = timeCalendar.time
                    isEndTimeSelected = true
                    updateEndTimeText()
                }
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    // Update start time text
    private fun updateStartTimeText() {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        startTimeText.text = if (isStartTimeSelected) format.format(selectedStartTime) else "Select time"
    }

    // Update end time text
    private fun updateEndTimeText() {
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        endTimeText.text = if (isEndTimeSelected) format.format(selectedEndTime) else "Select time"
    }

    private fun loadDropdownData() {

        categoryCard.isEnabled = false
        accountCard.isEnabled = false
        memberCard.isEnabled = false
        categoryArrow.isEnabled = false
        accountArrow.isEnabled = false
        memberArrow.isEnabled = false

        val userId = UserSession.userId

        Thread {
            categories = db.categoryDao().getAll(userId)
            accounts = db.accountDao().getAll(userId)
            members = db.memberDao().getAll(userId)

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
        photoButton.setOnClickListener { showPhotoOptions() }
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

        // Create a calendar instance to combine date and time for start time
        val startCalendar = Calendar.getInstance()
        startCalendar.time = selectedStartTime
        startCalendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
        val finalStartTime = startCalendar.time

        // Create a calendar instance to combine date and time for end time
        val endCalendar = Calendar.getInstance()
        endCalendar.time = selectedEndTime
        endCalendar.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH))
        val finalEndTime = endCalendar.time

        val userId = UserSession.userId

        val transaction = Transaction(
            name = memoInput.text.toString().ifEmpty { "Transaction" },
            amount = amount,
            transactionType = selectedType,
            date = selectedDate.time,
            accountId = selectedAccountId!!,
            categoryId = selectedCategoryId!!,
            memberId = selectedMemberId!!,
            photo = photoPath != null,
            photoPath = photoPath,
            startTime = finalStartTime,
            endTime = finalEndTime
        )

        Thread {
            db.transactionDao().insert(transaction)

            //update the account balance when a transaction is added
            val account = db.accountDao().getById(selectedAccountId!!,userId)
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
        //Reset time values
        selectedStartTime = Date()
        selectedEndTime = Date()
        isStartTimeSelected = false
        isEndTimeSelected = false
        photoPath = null
        pendingPhotoUri = null
        photoPreview.visibility = View.GONE
        photoPreview.setImageURI(null)
        updateTypeButtonStyles()
        updateDateText()
        //Update time texts
        updateStartTimeText()
        updateEndTimeText()
    }

    private fun showPhotoOptions() {
        AlertDialog.Builder(this)
            .setTitle("Add Photo")
            .setItems(arrayOf("Take Photo", "Choose from Gallery")) { _, which ->
                when (which) {
                    0 -> takePhoto()    // user chose camera
                    1 -> pickFromGallery() // user chose gallery
                }
            }.show()
    }



    private fun takePhoto() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, open camera
            val photoFile = createImageFile()
            photoPath = photoFile.absolutePath
            val uri = FileProvider.getUriForFile(this, "${packageName}.provider", photoFile)
            pendingPhotoUri = uri
            takePictureLauncher.launch(uri)
        } else {
            // Ask the user for permission first
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun pickFromGallery() {
        pickImageLauncher.launch("image/*") // "image/*" means any image type
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("IMG_${timestamp}_", ".jpg", storageDir)
    }

    private fun copyUriToFile(uri: Uri): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IMG_$timestamp.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        return file
    }


    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            takePhoto() // permission granted, open camera
        } else {
            Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
        }
    }
}