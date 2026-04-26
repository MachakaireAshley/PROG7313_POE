package com.example.prog7313_poe

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox

class MemberAdapter(private val members: List<MemberActivity.Member>) :
    RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val memberName: TextView = itemView.findViewById(R.id.memberName)
        private val checkbox: MaterialCheckBox = itemView.findViewById(R.id.memberCheckbox)

        fun bind(member: MemberActivity.Member) {
            memberName.text = member.name
            checkbox.isChecked = false
        }
    }
}