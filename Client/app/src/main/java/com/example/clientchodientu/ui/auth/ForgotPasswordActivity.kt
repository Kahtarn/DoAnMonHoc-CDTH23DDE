package com.example.clientchodientu.ui.auth


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.auth.forgotpassword.ForgotPasswordRequest
import com.example.clientchodientu.dto.auth.forgotpassword.ForgotPasswordRespond
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.core.content.edit
import okhttp3.OkHttpClient
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.trim
import kotlin.toString

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var edtemail: EditText
    private lateinit var btnSendOTP: Button
    private lateinit var ivBackOTP : ImageView
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ProfileUser)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ivBackOTP = findViewById(R.id.ivBackOTPverification)
        ivBackOTP.setOnClickListener {
            finish()
        }
        btnSendOTP = findViewById<Button>(R.id.btnSendOTPForgotPassword)
        edtemail = findViewById<EditText>(R.id.edtEmailForgotPassword)
        btnSendOTP.setOnClickListener {
            val email = edtemail.text.toString().trim()
            if (email.isEmpty()) {
                edtemail.error = "Vui lòng nhập email"
                focusEditText(edtemail)
                return@setOnClickListener
            } else {
                //dung dinh dang email
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    startCountDown(btnSendOTP)
                    lifecycleScope.launch {
                        sendOTPEmail(email)
                    }
                } else {
                    edtemail.error = "Vui lòng nhập đúng định dạng email"
                    focusEditText(edtemail)
                    return@setOnClickListener
                }
            }
        }

    }


    fun focusEditText(editText: EditText) {
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    suspend fun sendOTPEmail(email: String) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val client = OkHttpClient()
                val url = "http://10.0.2.2:8080/api/auth/forgot-password"
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
                            this@ForgotPasswordActivity,
                            data.message,
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("ForgotPasswordActivity", "Response body: $data")
                        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                        prefs.edit { putString("email", email) }

                        Log.d("emailForgotPassword", email.toString().trim())
                        val intent = Intent(
                            this@ForgotPasswordActivity,
                            ForgotPasswordOTPVerificationActivity::class.java
                        )
                        startActivity(intent);
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            data.message,
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("ForgotPasswordActivity", "Response body: $data")
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    e.message.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun startCountDown(button: Button) {
        // 1. Disable nút để không bấm được nữa
        button.isEnabled = false

        // Lưu lại text gốc (ví dụ: "Gửi mã OTP") để trả lại sau khi đếm xong
        val originalText = "Gửi mã lại OTP"

        // 2. Tạo bộ đếm ngược
        // Tham số 1: Tổng thời gian (60000ms = 60 giây)
        // Tham số 2: Bước nhảy (1000ms = 1 giây)
        countDownTimer = object : CountDownTimer(10000, 1000) {

            // Chạy mỗi giây (cập nhật giao diện)
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                button.text = "Thử lại sau ($secondsLeft)s"
            }

            // Chạy khi đếm xong (hết giờ)
            override fun onFinish() {
                button.isEnabled = true  // Mở khóa nút
                button.text = originalText // Trả lại text ban đầu
            }
        }
        // 3. Bắt đầu chạy
        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

}