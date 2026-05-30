package com.example.prog7313_poe

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class CategoryActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var addButton: Button
    private lateinit var categoriesGrid: RecyclerView
    private lateinit var db: AppDatabase
    private lateinit var adapter: CategoryAdapter
    private lateinit var categoryRepo: CategoryRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        db = AppDatabase.getDatabase(this)

        val currentUserId= FirebaseAuth.getInstance().currentUser?.uid
        if(currentUserId==null){
            Toast.makeText(this, "User Not Logged In", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryDao= db.categoryDao()
        categoryRepo= CategoryRepo(categoryDao, currentUserId)

        categoryRepo.listenForCloudChanges()

        backButton = findViewById(R.id.categoryBackButton)
        addButton = findViewById(R.id.category_addBotton)
        categoriesGrid = findViewById(R.id.categoriesGrid)
        categoriesGrid.layoutManager = GridLayoutManager(this, 4)

        backButton.setOnClickListener { finish() }
        addButton.setOnClickListener { showAddCategoryDialog() }

        loadCategories()
    }

    private fun loadCategories() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid?: return // 👈 ADD THIS

       lifecycleScope.launch {
           categoryRepo.getCategories().collect {
               categories -> adapter= CategoryAdapter(categories){ category ->
                   Toast.makeText(this@CategoryActivity, "Selected: ${category.name}", Toast.LENGTH_SHORT).show()
           }
               categoriesGrid.adapter=adapter
           }
       }
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
                    val currentUserId= FirebaseAuth.getInstance().currentUser?.uid?: ""
                    lifecycleScope.launch {
                        val category= Category(
                            userId = currentUserId,
                            name = name,
                            percentage = 0,
                            lastUpdated = 0
                        )
                        categoryRepo.saveCategory(category)
                        Toast.makeText(this@CategoryActivity, "Category added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}