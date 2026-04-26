package com.example.prog7313_poe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PinLockActivity : AppCompatActivity() {

    private lateinit var pinDots: List<View>
    private var pinInput = ""
    private val correctPin = "1234"
    private var attempts = 0
    private val maxAttempts = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_lock)

        pinDots = listOf(
            findViewById(R.id.pinDot1),
            findViewById(R.id.pinDot2),
            findViewById(R.id.pinDot3),
            findViewById(R.id.pinDot4)
        )

        setupNumberButtons()
        setupForgotPinListener()
    }

    private fun setupNumberButtons() {
        val buttons = listOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8", R.id.btn9 to "9"
        )

        buttons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener {
                if (pinInput.length < 4) {
                    pinInput += value
                    updatePinDots()
                    if (pinInput.length == 4) {
                        checkPin()
                    }
                }
            }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            if (pinInput.isNotEmpty()) {
                pinInput = pinInput.dropLast(1)
                updatePinDots()
            }
        }
    }

    private fun setupForgotPinListener() {
        findViewById<TextView>(R.id.forgotPinText).setOnClickListener {
            android.widget.Toast.makeText(this, "PIN reset feature coming soon", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePinDots() {
        pinDots.forEachIndexed { index, dot ->
            dot.setBackgroundResource(
                if (index < pinInput.length) R.drawable.pin_dot_filled
                else R.drawable.pin_dot_empty
            )
        }
    }

    private fun checkPin() {
        if (pinInput == correctPin) {
            navigateToHome()
        } else {
            attempts++
            pinInput = ""
            updatePinDots()

            val errorMessage = findViewById<TextView>(R.id.errorMessage)
            val attemptsRemaining = findViewById<TextView>(R.id.attemptsRemaining)

            errorMessage.text = "Incorrect PIN"
            errorMessage.visibility = View.VISIBLE

            if (attempts >= maxAttempts) {
                errorMessage.text = "Too many attempts. Please try again later."
                disableButtons()
            } else {
                attemptsRemaining.text = "${maxAttempts - attempts} attempts remaining"
                attemptsRemaining.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeListActivity::class.java))
        finish()
    }

    private fun disableButtons() {
        val allButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnClear
        )
        allButtons.forEach {
            findViewById<Button>(it).isEnabled = false
        }
    }
}