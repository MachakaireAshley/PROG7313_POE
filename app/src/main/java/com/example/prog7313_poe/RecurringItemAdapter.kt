package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecurringItemAdapter(
    private val items: List<RecurringItemActivity.RecurringItem>,
    private val onDetailClick: (RecurringItemActivity.RecurringItem) -> Unit
) : RecyclerView.Adapter<RecurringItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recurring_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onDetailClick)
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.recurring_itemTitle)
        private val categoryText: TextView = itemView.findViewById(R.id.recurring_item_categoryName)
        private val frequencyText: TextView = itemView.findViewById(R.id.recurring_item_frequency)
        private val nextDateText: TextView = itemView.findViewById(R.id.recurring_item_nextDate)
        private val amountText: TextView = itemView.findViewById(R.id.recurring_item_amount)
        private val detailButton: Button = itemView.findViewById(R.id.recurring_item_detailButton)
        private val deleteButton: Button = itemView.findViewById(R.id.recurring_item_deleteButton)

        fun bind(item: RecurringItemActivity.RecurringItem, onDetailClick: (RecurringItemActivity.RecurringItem) -> Unit) {
            titleText.text = item.title
            categoryText.text = item.category
            frequencyText.text = item.frequency
            nextDateText.text = "Next: ${item.nextDate}"
            amountText.text = item.amount

            detailButton.setOnClickListener { onDetailClick(item) }
            deleteButton.setOnClickListener {
                // Handle delete
            }
        }
    }
}