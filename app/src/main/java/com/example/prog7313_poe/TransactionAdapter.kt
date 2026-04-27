package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount() = transactions.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val transactionDate: TextView = itemView.findViewById(R.id.home_list_transactionDate)
        private val transactionCategory: TextView = itemView.findViewById(R.id.home_list_transactionCategory)
        private val transactionAmount: TextView = itemView.findViewById(R.id.home_list_transactionAmount)
        private val transactionSource: TextView = itemView.findViewById(R.id.home_list_transactionSource)

        fun bind(transaction: Transaction) {
            val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            transactionDate.text = sdf.format(transaction.date)
            transactionCategory.text = "Category: ${transaction.categoryId}"  // you can later fetch category name
            transactionAmount.text = "R %.2f".format(transaction.amount)
            val color = if (transaction.transactionType == "income") R.color.income_green else R.color.expense_red
            transactionAmount.setTextColor(ContextCompat.getColor(itemView.context, color))
            transactionSource.text = "Account: ${transaction.accountId}"
        }
    }
}