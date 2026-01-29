package com.example.clientchodientu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.auth.forgotpassword.ForgotPasswordOTPVerificationRequest
import com.example.clientchodientu.dto.auth.forgotpassword.ForgotPasswordOTPVerificationRespond
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.core.content.edit
import com.example.clientchodientu.dto.auth.forgotpassword.ForgotPasswordRequest
import com.example.clientchodientu.dto.auth.forgotpassword.ForgotPasswordRespond
import kotlin.collections.forEachIndexed
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.isNotEmpty
import kotlin.text.last
import kotlin.text.trim
import kotlin.toString

class ForgotPasswordOTPVerificationActivity : AppCompatActivity() {
    private var client = OkHttpClient()
    private var urlBase = "https://uncondensable-diplopic-gibson.ngrok-free.dev/api/auth/";
    private var gson = Gson()
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var otp6: EditText
    private lateinit var btnVerify: Button
    private lateinit var ReSendOtp: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password_otp_verification)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ProfileUser)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        otp1 = findViewById(R.id.edtOtp1)
        otp2 = findViewById(R.id.edtOtp2)
        otp3 = findViewById(R.id.edtOtp3)
        otp4 = findViewById(R.id.edtOtp4)
        otp5 = findViewById(R.id.edtOtp5)
        otp6 = findViewById(R.id.edtOtp6)
        setupOtpInputs(otp1, otp2, otp3, otp4, otp5, otp6)
        btnVerify = findViewById(R.id.btnVerify)
        ReSendOtp = findViewById(R.id.tvReSendOtp)

        btnVerify.setOnClickListener {
            lifecycleScope.launch {
                verifyOtp()
            }
        }

        ReSendOtp.setOnClickListener {
            lifecycleScope.launch {
                var email = ""
                val pref = getSharedPreferences("auth", MODE_PRIVATE)
                email = pref.getString("email", "")!!
                sendOTPEmail(email)
            }
        }

    }

    suspend fun sendOTPEmail(email: String) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val client = OkHttpClient()
                val url = "https://uncondensable-diplopic-gibson.ngrok-free.dev/api/auth/forgot-password"
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val forgotPasswordRequest = ForgotPasswordRequest(email)

                val jsonString = gson.toJson(forgotPasswordRequest)
                val requestBody = jsonString.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .build()

                val response = client.newCall(request).execute()
                val repdpondyBody = response.body?.string()

                val data = gson.fromJson(
                    repdpondyBody, ForgotPasswordRespond::class.java
                )
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ForgotPasswordOTPVerificationActivity,
                            data.message,
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("ForgotPasswordActivity", "Response body: $data")
                        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                        prefs.edit { putString("email", email) }

                        Log.d("emailForgotPassword", email.toString().trim())
                        val intent = Intent(
                            this@ForgotPasswordOTPVerificationActivity,
                            ForgotPasswordOTPVerificationActivity::class.java
                        )
                        startActivity(intent);
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ForgotPasswordOTPVerificationActivity,
                            data.message,
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("ForgotPasswordActivity", "Response body: $data")
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordOTPVerificationActivity,
                    e.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    suspend fun verifyOtp() {
        withContext(Dispatchers.IO) {
            try {
                val otp = otp1.text.toString().trim() +
                        otp2.text.toString().trim() +
                        otp3.text.toString().trim() +
                        otp4.text.toString().trim() +
                        otp5.text.toString().trim() +
                        otp6.text.toString().trim()
                var email = ""
                val pref = getSharedPreferences("auth", MODE_PRIVATE)
                email = pref.getString("email", "")!!
                val forgotPasswordVerifyOtpRequest =
                    ForgotPasswordOTPVerificationRequest(
                        email = email,
                        otpCode = otp
                    )

                val jsonString = gson.toJson(forgotPasswordVerifyOtpRequest)
                val requestBody =
                    jsonString.toRequestBody("application/json; charset=utf-8".toMediaType())

                val request = Request.Builder()
                    .url(urlBase + "verify-otp-to-reset-password")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val respondyBody = response.body?.string()

                val data =
                    gson.fromJson(respondyBody, ForgotPasswordOTPVerificationRespond::class.java)

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Log.d("OTP", otp)
                        Log.d("email", email)
                        //luu otp vao shared preference
                        val pref = getSharedPreferences("auth", MODE_PRIVATE)
                        pref.edit {
                            putString("otp", otp)
                        }
                        Toast.makeText(
                            this@ForgotPasswordOTPVerificationActivity,
                            data.message,
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(
                            this@ForgotPasswordOTPVerificationActivity,
                            ResetPasswordActivity::class.java
                        )
                        startActivity(intent);
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.d("OTP", otp)
                        Log.d("email", email)
                        Toast.makeText(
                            this@ForgotPasswordOTPVerificationActivity,
                            data.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("error message", e.message.toString())
                    Toast.makeText(
                        this@ForgotPasswordOTPVerificationActivity,
                        e.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

private fun setupOtpInputs(
    edtOtp1: EditText,
    edtOtp2: EditText,
    edtOtp3: EditText,
    edtOtp4: EditText,
    edtOtp5: EditText,
    edtOtp6: EditText
) {
    // 1. Put all your EditTexts into a list in order
    val otpBoxes = mutableListOf(
        edtOtp1,
        edtOtp2,
        edtOtp3,
        edtOtp4,
        edtOtp5,
        edtOtp6
    )

    // 2. Loop through them to assign listeners
    otpBoxes.forEachIndexed { index, editText ->
        // buoc focus khi click eg: cursor o truoc text
        editText.setOnClickListener {
            editText.setSelection(editText.text.length)
        }
        //key event
        editText.setOnKeyListener { v, keyCode, event ->
            // If user presses BACKSPACE and the box is empty, go back
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                // FIX: If cursor is somehow at the start (0) but text exists, clear it manually
                if (editText.selectionStart == 0 && editText.text.isNotEmpty()) {
                    editText.text?.clear()
                    return@setOnKeyListener true
                }
                //go back if empty
                if (editText.text.isEmpty() && index > 0) {
                    otpBoxes[index - 1].requestFocus()
                    return@setOnKeyListener true
                }
            }
            false
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()

                //case 1: override exist number
                if (text.length > 1) {
                    val newNumber = text.last().toString() // Get the last char typed

                    // Update the box to show ONLY the new number
                    editText.setText(newNumber)
                    editText.setSelection(1) // Move cursor to end

                    // Move to next box immediately
                    if (index < otpBoxes.size - 1) {
                        otpBoxes[index + 1].requestFocus()
                    }
                }
                // CASE 2: User typed a number (Length is 1)
                // Move focus to the NEXT box (if it exists)
                else if (text.length == 1 && index < otpBoxes.size - 1) {
                    otpBoxes[index + 1].requestFocus()
                }
                // CASE 3: User deleted the number (Length is 0)
                // Move focus to the PREVIOUS box (if it exists)
                else if (text.isEmpty() && index > 0) {
                    otpBoxes[index - 1].requestFocus()
                }
            }
        })
    }
}
