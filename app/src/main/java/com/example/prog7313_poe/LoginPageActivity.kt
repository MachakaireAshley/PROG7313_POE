package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginPageActivity : AppCompatActivity() {

    private lateinit var accountInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var forgotPasswordText: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        accountInput = findViewById(R.id.accountInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        forgotPasswordText = findViewById(R.id.forgotPasswordText)
    }

    private fun setupClickListeners() {


        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val account = accountInput.text.toString()
            val password = passwordInput.text.toString()

            if (account.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(account, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeListActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        forgotPasswordText.setOnClickListener {
            Toast.makeText(this, "Password reset feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}