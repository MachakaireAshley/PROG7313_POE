package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginPageActivity : AppCompatActivity() {

    private lateinit var accountInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var forgotPasswordText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        accountInput = findViewById(R.id.accountInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
    }

    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            val account = accountInput.text.toString()
            val password = passwordInput.text.toString()

            if (account.isNotEmpty() && password.isNotEmpty()) {
                startActivity(Intent(this, HomeListActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            Toast.makeText(this, "Registration feature coming soon", Toast.LENGTH_SHORT).show()
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Password reset feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}