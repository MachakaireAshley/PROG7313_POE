package com.example.prog7313_poe

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MemberActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var addButton: Button
    private lateinit var membersRecyclerView: RecyclerView
    private lateinit var db: AppDatabase
    private lateinit var adapter: MemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        db = AppDatabase.getDatabase(this)

        backButton = findViewById(R.id.memberBackButton)
        addButton = findViewById(R.id.member_addButton)
        membersRecyclerView = findViewById(R.id.membersRecyclerView)
        membersRecyclerView.layoutManager = LinearLayoutManager(this)

        backButton.setOnClickListener { finish() }
        addButton.setOnClickListener { showAddMemberDialog() }

        loadMembers()
    }

    private fun loadMembers() {
        Thread {
            val list = db.memberDao().getAll()
            runOnUiThread {
                adapter = MemberAdapter(list)
                membersRecyclerView.adapter = adapter
            }
        }.start()
    }

    private fun showAddMemberDialog() {
        val input = EditText(this)
        input.hint = "Member name"
        AlertDialog.Builder(this)
            .setTitle("New Member")
            .setView(input)
            .setPositiveButton("Add") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val member = Member(name = name)
                    Thread {
                        db.memberDao().insert(member)
                        runOnUiThread { loadMembers() }
                    }.start()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}