package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AccountAdapter(private var accounts: List<Account>) :
    RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    fun updateData(newList: List<Account>) {
        accounts = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(accounts[position])
    }

    override fun getItemCount() = accounts.size

    class AccountViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val accountIcon: ImageView = itemView.findViewById(R.id.accountIcon)
        private val accountName: TextView = itemView.findViewById(R.id.accountName)
        private val accountBalance: TextView = itemView.findViewById(R.id.account_Balance)
        private val accountCurrency: TextView = itemView.findViewById(R.id.account_Currency)

        fun bind(account: Account) {

            val iconRes = when {
                account.accountName.contains("bank", ignoreCase = true) -> R.drawable.ic_account_bank
                account.accountName.contains("cash", ignoreCase = true) -> R.drawable.ic_account_cash
                else -> R.drawable.ic_account_savings
            }
            accountIcon.setImageResource(iconRes)
            accountName.text = account.accountName
            accountBalance.text = "R %.2f".format(account.amount)
            accountCurrency.text = "ZAR"
        }
    }
}