package com.example.prog7313_poe

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecurringItemActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var addButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: AppDatabase
    private lateinit var adapter: RecurringItemAdapter
    private lateinit var recurringItemRepo: RecurringItemRepo
    private lateinit var categoryRepo: CategoryRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recurring_item)
        db = AppDatabase.getDatabase(this)


        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        val recurringItemDao = db.recurringItemDao()
        val categoryDao = db.categoryDao()


        categoryRepo = CategoryRepo(categoryDao, currentUserId)
        recurringItemRepo = RecurringItemRepo(recurringItemDao, categoryDao, currentUserId)

        categoryRepo.listenForCloudChanges()
        recurringItemRepo.listenForCloudChanges()


        backButton = findViewById(R.id.recurring_item_BackButton)
        addButton = findViewById(R.id.recurring_item_addButton)
        recyclerView = findViewById(R.id.recurringItemsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backButton.setOnClickListener { finish() }
        addButton.setOnClickListener { showAddDialog() }
        loadItems()
    }

    private fun loadItems() {
        lifecycleScope.launch {
            recurringItemRepo.getRecurringItems().collect { items ->
                adapter = RecurringItemAdapter(items) { item, delete ->
                    if (delete) {
                        lifecycleScope.launch {
                            recurringItemRepo.deleteRecurringItem(item.id) // You'll need to add this function
                            Toast.makeText(this@RecurringItemActivity, "Deleted", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@RecurringItemActivity, "Detail: ${item.title}", Toast.LENGTH_SHORT).show()
                    }
                }
                recyclerView.adapter = adapter
            }
        }
    }

    private fun showAddDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_recurring_item, null)
        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.recurring_item_nameInput)
        val amountInput = dialogView.findViewById<TextInputEditText>(R.id.recurring_item_amountInput)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.recurring_item_categorySpinner)
        val frequencySpinner = dialogView.findViewById<Spinner>(R.id.recurring_item_frequencySpinner)
        val startDateInput = dialogView.findViewById<TextInputEditText>(R.id.recurring_item_startDateInput)
        val endDateInput = dialogView.findViewById<TextInputEditText>(R.id.recurring_item_endDateInput)

        // load categories into spinner
        lifecycleScope.launch(Dispatchers.IO) {
            val currentUserIdForCategories = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val categories = db.categoryDao().getAll(currentUserIdForCategories)
            val names = categories.map { it.name }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@RecurringItemActivity, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapter
            }
        }
        val frequencyOptions = arrayOf("Monthly", "Weekly", "Yearly")
        frequencySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, frequencyOptions)

        startDateInput.setOnClickListener { showDatePicker(startDateInput) }
        endDateInput.setOnClickListener { showDatePicker(endDateInput) }

        AlertDialog.Builder(this)
            .setTitle("Add Recurring Item")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameInput.text.toString()
                val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
                val categoryPos = categorySpinner.selectedItemPosition
                val frequency = frequencySpinner.selectedItem.toString()
                val startDate = parseDate(startDateInput.text.toString())
                if (name.isNotEmpty() && amount > 0 && startDate != null) {
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    lifecycleScope.launch {
                        val catId = db.categoryDao().getAll(currentUserId)[categoryPos].id
                        val item = RecurringItem(
                            userId = currentUserId,
                            title = name,
                            amount = amount,
                            categoryId = catId,
                            frequency = frequency,
                            startDate = startDate,
                            endDate = parseDate(endDateInput.text.toString()),
                            lastUpdated = 0
                        )
                        recurringItemRepo.saveRecurringItem(item)
                        Toast.makeText(this@RecurringItemActivity, "Added", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Fill required fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDatePicker(editText: TextInputEditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            editText.setText("$y-${m+1}-$d")
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.parse(dateStr)
        } catch (e: Exception) { null }
    }
}