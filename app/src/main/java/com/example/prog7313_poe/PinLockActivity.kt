package com.example.prog7313_poe

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PinLockActivity : AppCompatActivity() {

    private lateinit var pinDots: List<View>
    private var pinInput = ""
    private lateinit var prefs: SharedPreferences
    private var attempts = 0
    private val maxAttempts = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_lock)
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

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
                    if (pinInput.length == 4) checkPin()
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
            Toast.makeText(this, "Reset PIN from Settings", Toast.LENGTH_SHORT).show()
            finish()
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
        val correctPin = prefs.getString("user_pin", "") ?: ""
        if (pinInput == correctPin) {
            startActivity(Intent(this, HomeListActivity::class.java))
            finish()
        } else {
            attempts++
            pinInput = ""
            updatePinDots()
            val errorMsg = findViewById<TextView>(R.id.errorMessage)
            errorMsg.text = "Incorrect PIN"
            errorMsg.visibility = View.VISIBLE
            if (attempts >= maxAttempts) {
                errorMsg.text = "Too many attempts. Exiting."
                disableButtons()
            } else {
                findViewById<TextView>(R.id.attemptsRemaining).apply {
                    text = "${maxAttempts - attempts} attempts left"
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun disableButtons() {
        val all = listOf(R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnClear)
        all.forEach { findViewById<Button>(it).isEnabled = false }
    }
}