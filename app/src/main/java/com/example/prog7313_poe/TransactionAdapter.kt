package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.DecimalFormat

class TransactionAdapter(
    private val onItemClick: (HomepageActivity.Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<HomepageActivity.Transaction> = emptyList()
    private val decimalFormat = DecimalFormat("#.00")

    fun submitList(newTransactions: List<HomepageActivity.Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    class TransactionViewHolder(
        itemView: View,
        private val onItemClick: (HomepageActivity.Transaction) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val dateTextView: TextView = itemView.findViewById(R.id.transactionDate)
        private val categoryTextView: TextView = itemView.findViewById(R.id.transactionCategory)
        private val amountTextView: TextView = itemView.findViewById(R.id.transactionAmount)
        private val sourceTextView: TextView = itemView.findViewById(R.id.transactionSource)

        fun bind(transaction: HomepageActivity.Transaction) {
            dateTextView.text = transaction.date
            categoryTextView.text = transaction.category

            val formattedAmount = "${if (transaction.type == "expense") "-" else "+"}$${transaction.amount}"
            amountTextView.text = formattedAmount
            amountTextView.setTextColor(
                if (transaction.type == "expense")
                    itemView.context.getColor(R.color.expense_red)
                else
                    itemView.context.getColor(R.color.income_green)
            )

            sourceTextView.text = transaction.source

            itemView.setOnClickListener { onItemClick(transaction) }
        }
    }
}