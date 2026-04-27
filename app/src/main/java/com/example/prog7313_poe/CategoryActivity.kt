package com.example.prog7313_poe

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var addButton: Button
    private lateinit var categoriesGrid: RecyclerView
    private lateinit var db: AppDatabase
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        db = AppDatabase.getDatabase(this)

        backButton = findViewById(R.id.categoryBackButton)
        addButton = findViewById(R.id.category_addBotton)
        categoriesGrid = findViewById(R.id.categoriesGrid)
        categoriesGrid.layoutManager = GridLayoutManager(this, 4)

        backButton.setOnClickListener { finish() }
        addButton.setOnClickListener { showAddCategoryDialog() }

        loadCategories()
    }

    private fun loadCategories() {
        Thread {
            val list = db.categoryDao().getAll()
            runOnUiThread {
                adapter = CategoryAdapter(list) { category ->
                    Toast.makeText(this, "Selected: ${category.name}", Toast.LENGTH_SHORT).show()
                }
                categoriesGrid.adapter = adapter
            }
        }.start()
    }

    private fun showAddCategoryDialog() {
        val input = EditText(this)
        input.hint = "Category name"
        AlertDialog.Builder(this)
            .setTitle("New Category")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val category = Category(name = name, percentage = 0)
                    Thread {
                        db.categoryDao().insert(category)
                        runOnUiThread { loadCategories() }
                    }.start()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}