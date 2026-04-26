package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryStatAdapter(private val categories: List<ReportsActivity.CategoryStat>) :
    RecyclerView.Adapter<CategoryStatAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_statistics, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val categoryRank: TextView = itemView.findViewById(R.id.categoryRank)
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        private val categoryAmount: TextView = itemView.findViewById(R.id.categoryAmount)
        private val categoryRatio: TextView = itemView.findViewById(R.id.categoryRatio)

        fun bind(category: ReportsActivity.CategoryStat) {
            categoryRank.text = category.rank
            categoryName.text = category.name
            categoryAmount.text = category.amount
            categoryRatio.text = category.ratio
        }
    }
}