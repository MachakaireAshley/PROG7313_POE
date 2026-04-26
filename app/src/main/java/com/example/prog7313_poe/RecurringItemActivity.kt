package com.example.prog7313_poe

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecurringItemActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var addButton: Button
    private lateinit var recurringItemsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recurring_item)

        initViews()
        setupClickListeners()
        loadRecurringItems()
    }

    private fun initViews() {
        backButton = findViewById(R.id.recurring_item_BackButton)
        addButton = findViewById(R.id.recurring_item_addButton)
        recurringItemsRecyclerView = findViewById(R.id.recurringItemsRecyclerView)

        recurringItemsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        addButton.setOnClickListener {
            showAddRecurringItemDialog()
        }
    }

    private fun loadRecurringItems() {
        val items = listOf(
            RecurringItem("Netflix Subscription", "Entertainment", "Monthly", "Mar 15, 2026", "$15.99"),
            RecurringItem("Spotify", "Music", "Monthly", "Mar 10, 2026", "$9.99"),
            RecurringItem("Gym Membership", "Health", "Monthly", "Mar 5, 2026", "$49.99")
        )

        val adapter = RecurringItemAdapter(items) { item ->
            showItemDetails(item)
        }
        recurringItemsRecyclerView.adapter = adapter
    }

    private fun showAddRecurringItemDialog() {
        Toast.makeText(this, "Add recurring item dialog coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun showItemDetails(item: RecurringItem) {
        Toast.makeText(this, "Item: ${item.title}", Toast.LENGTH_SHORT).show()
    }

    data class RecurringItem(val title: String, val category: String, val frequency: String, val nextDate: String, val amount: String)
}