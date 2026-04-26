package com.example.prog7313_poe

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MemberActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var addButton: Button
    private lateinit var membersRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)

        initViews()
        setupClickListeners()
        loadMembers()
    }

    private fun initViews() {
        backButton = findViewById(R.id.memberBackButton)
        addButton = findViewById(R.id.member_addButton)
        membersRecyclerView = findViewById(R.id.membersRecyclerView)

        membersRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            finish()
        }

        addButton.setOnClickListener {
            showAddMemberDialog()
        }
    }

    private fun loadMembers() {
        val members = listOf(
            Member("John Doe"),
            Member("Jane Smith"),
            Member("Bob Johnson")
        )

        val adapter = MemberAdapter(members)
        membersRecyclerView.adapter = adapter
    }

    private fun showAddMemberDialog() {
        Toast.makeText(this, "Add member dialog coming soon", Toast.LENGTH_SHORT).show()
    }

    data class Member(val name: String)
}