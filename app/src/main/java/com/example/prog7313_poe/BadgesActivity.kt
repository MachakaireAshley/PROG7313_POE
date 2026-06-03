package com.example.prog7313_poe

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prog7313_poe.databinding.ActivityBadgesBinding

class BadgesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBadgesBinding
    private val viewModel: BadgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBadgesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.badgesRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.badges.observe(this) { badges ->
            binding.badgesRecyclerView.adapter = BadgeAdapter(badges)
        }

        binding.backButton.setOnClickListener { finish() }
    }
}