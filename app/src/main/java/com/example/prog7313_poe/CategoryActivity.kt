package com.example.prog7313_poe

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var addButton: Button
    private lateinit var categoriesGrid: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        initViews()
        setupClickListeners()
        loadCategories()
    }

    private fun initViews() {
        backButton = findViewById(R.id.categoryBackButton)
        addButton = findViewById(R.id.category_addBotton)
        categoriesGrid = findViewById(R.id.categoriesGrid)

        categoriesGrid.layoutManager = GridLayoutManager(this, 4)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        addButton.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun loadCategories() {
        val categories = listOf(
            Category("Food", R.drawable.ic_category_food),
            Category("Transport", R.drawable.ic_category_transport),
            Category("Shopping", R.drawable.ic_category_shopping),
            Category("Entertainment", R.drawable.ic_category_entertainment),
            Category("Health", R.drawable.ic_category_health),
            Category("Utilities", R.drawable.ic_category_utilities),
            Category("Education", R.drawable.ic_category_education),
            Category("Other", R.drawable.ic_category_other)
        )

        val adapter = CategoryAdapter(categories) { category ->
            Toast.makeText(this, "Selected: ${category.name}", Toast.LENGTH_SHORT).show()
        }
        categoriesGrid.adapter = adapter
    }

    private fun showAddCategoryDialog() {
        Toast.makeText(this, "Add category dialog coming soon", Toast.LENGTH_SHORT).show()
    }

    data class Category(val name: String, val iconRes: Int)
}