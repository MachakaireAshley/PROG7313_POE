package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(
    private val categories: List<CategoryActivity.Category>,
    private val onItemClick: (CategoryActivity.Category) -> Unit
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

        fun bind(category: CategoryActivity.Category, onItemClick: (CategoryActivity.Category) -> Unit) {
            categoryIcon.setImageResource(category.iconRes)
            categoryName.text = category.name
            itemView.setOnClickListener { onItemClick(category) }
        }
    }
}