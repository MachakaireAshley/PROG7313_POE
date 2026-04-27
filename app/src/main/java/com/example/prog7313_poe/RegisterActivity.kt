package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var returnToLoginButton: Button
    private lateinit var passwordText: EditText
    private lateinit var emailText: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        initViews()
        setupClickListeners()

    }


    private fun initViews() {
        auth = FirebaseAuth.getInstance()
        emailText = findViewById(R.id.emailText)
        passwordText = findViewById(R.id.passwordText)
        registerButton = findViewById(R.id.buttonRegister)
        returnToLoginButton = findViewById(R.id.buttonBackToLogin)
    }

    private fun setupClickListeners() {
        registerButton.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeListActivity::class.java))
                        finish()
                    }
                    else {
                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }

        returnToLoginButton.setOnClickListener {
            startActivity(Intent(this, LoginPageActivity::class.java))
            finish()
        }
    }


}
