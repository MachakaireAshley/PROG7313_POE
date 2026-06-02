package com.example.prog7313_poe

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prog7313_poe.databinding.ItemBadgeBinding

class BadgeAdapter(private val badges: List<Badge>) :
    RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(val binding: ItemBadgeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]

        holder.binding.badgeName.text = badge.name
        holder.binding.badgeDescription.text = badge.description
        holder.binding.badgeCategory.text = formatCategory(badge.category)

        if (badge.isUnlocked) {
            holder.binding.badgeIcon.text = badge.icon
            holder.binding.root.alpha = 1f

            when (badge.type) {
                BadgeType.BRONZE -> {
                    holder.binding.badgeName.setTextColor(Color.parseColor("#8B5A2B"))
                    holder.binding.badgeCategory.setTextColor(Color.parseColor("#8B5A2B"))
                }

                BadgeType.SILVER -> {
                    holder.binding.badgeName.setTextColor(Color.parseColor("#808080"))
                    holder.binding.badgeCategory.setTextColor(Color.parseColor("#808080"))
                }

                BadgeType.GOLD -> {
                    holder.binding.badgeName.setTextColor(Color.parseColor("#D4A017"))
                    holder.binding.badgeCategory.setTextColor(Color.parseColor("#D4A017"))
                }

                BadgeType.DIAMOND -> {
                    holder.binding.badgeName.setTextColor(Color.parseColor("#2196F3"))
                    holder.binding.badgeCategory.setTextColor(Color.parseColor("#2196F3"))
                }

                BadgeType.LEGENDARY -> {
                    holder.binding.badgeName.setTextColor(Color.parseColor("#7B1FA2"))
                    holder.binding.badgeCategory.setTextColor(Color.parseColor("#7B1FA2"))
                }
            }

            holder.binding.badgeDescription.setTextColor(Color.DKGRAY)

        } else {
            holder.binding.badgeIcon.text = "🔒"
            holder.binding.root.alpha = 0.45f
            holder.binding.badgeName.setTextColor(Color.GRAY)
            holder.binding.badgeCategory.setTextColor(Color.GRAY)
            holder.binding.badgeDescription.setTextColor(Color.GRAY)
        }
    }

    override fun getItemCount(): Int = badges.size

    private fun formatCategory(category: BadgeCategory): String {
        return when (category) {
            BadgeCategory.SAVINGS -> "Savings Badge"
            BadgeCategory.BUDGETING -> "Budgeting Badge"
            BadgeCategory.CONSISTENCY -> "Consistency Badge"
            BadgeCategory.SPENDING_CONTROL -> "Spending Control Badge"
            BadgeCategory.GOAL_ACHIEVEMENT -> "Goal Achievement Badge"
        }
    }
}