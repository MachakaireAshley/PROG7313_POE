package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<Category>,
    private val onItemClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position], onItemClick)
    }

    override fun getItemCount() = categories.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val categoryIcon: ImageView = itemView.findViewById(R.id.categoryIcon)
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: Category, onItemClick: (Category) -> Unit) {
            // use default icon based on name
            val iconRes = when {
                category.name.contains("food", ignoreCase = true) -> R.drawable.ic_category_food
                category.name.contains("transport", ignoreCase = true) -> R.drawable.ic_category_transport
                category.name.contains("shopping", ignoreCase = true) -> R.drawable.ic_category_shopping
                else -> R.drawable.ic_category_other
            }
            categoryIcon.setImageResource(iconRes)
            categoryName.text = category.name
            itemView.setOnClickListener { onItemClick(category) }
        }
    }
}