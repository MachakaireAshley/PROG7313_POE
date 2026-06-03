package com.example.prog7313_poe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BadgesActivity : AppCompatActivity() {

    private lateinit var badgesRecyclerView: RecyclerView   // ← changed name to match layout
    private lateinit var viewModel: BadgeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        badgesRecyclerView = findViewById(R.id.badgesRecyclerView)   // ← correct ID
        badgesRecyclerView.layoutManager = LinearLayoutManager(this)

        viewModel = ViewModelProvider(this)[BadgeViewModel::class.java]
        viewModel.badges.observe(this) { badges ->
            val adapter = BadgeAdapter(badges)
            badgesRecyclerView.adapter = adapter
        }
    }
}