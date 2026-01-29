package com.example.clientchodientu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.auth.forgotpassword.ConfirmPasswordRequest
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.jvm.java
import kotlin.text.isEmpty
import kotlin.text.trim

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var newPass: EditText
    private lateinit var confirmPass: EditText
    private lateinit var btnResetPassword: Button
    private var client = OkHttpClient()
    private var gson = Gson()
    private var urlBase = "https://tifany-unsliding-casie.ngrok-free.dev/api/auth/reset-password";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ProfileUser)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        newPass = findViewById<EditText>(R.id.edtNewPassword)
        confirmPass = findViewById<EditText>(R.id.edtConfirmPassword)
        btnResetPassword = findViewById<Button>(R.id.btnDone)
        newPass.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        confirmPass.transformationMethod = android.text.method.PasswordTransformationMethod.getInstance()
        btnResetPassword.setOnClickListener {
            lifecycleScope.launch {
                handleResetClick()
            }
        }
    }
    fun isPasswordValid(password: String): Boolean {
        val passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{8,}$"
        return password.matches(passwordPattern.toRegex())
    }
    private fun handleResetClick() {
        val newPassword = newPass.text.toString().trim()
        val confirmPassword = confirmPass.text.toString().trim()

        // 1. VALIDATE TẠI UI THREAD (Trước khi vào coroutine)
        if (newPassword.isEmpty()) {
            newPass.error = "Vui lòng nhập mật khẩu mới"
            newPass.requestFocus()
            return
        }
        if(!isPasswordValid(newPassword) || !isPasswordValid(confirmPassword)) {
            Toast.makeText(this@ResetPasswordActivity, "Mật khẩu cần ít nhất 8 ký tự, 1 chữ hoa, 1 chữ số và KHÔNG có khoảng trắng!", Toast.LENGTH_SHORT).show()
            return
        }
        if (confirmPassword.isEmpty()) {
            confirmPass.error = "Vui lòng xác nhận mật khẩu"
            confirmPass.requestFocus()
            return
        }
        if (newPassword != confirmPassword) {
            confirmPass.error = "Mật khẩu không khớp"
            confirmPass.requestFocus()
            return
        }

        // 2. Lấy dữ liệu từ SharedPreferences
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val email = prefs.getString("email", "") ?: ""
        val otpCode = prefs.getString("otp", "") ?: ""

        if (email.isEmpty() || otpCode.isEmpty()) {
            Toast.makeText(this, "Lỗi: Mất thông tin phiên làm việc", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Gọi API
        lifecycleScope.launch {
            sendResetRequest(email, otpCode, newPassword)
        }
    }

    private suspend fun sendResetRequest(email: String, otpCode: String, newPass: String) {
        // Chuyển sang IO thread để gọi mạng
        withContext(Dispatchers.IO) {
            try {
                // Tạo Request Object (Server cần 3 trường này)
                val requestData = ConfirmPasswordRequest(
                    email = email,
                    otpCode = otpCode,
                    newPassword = newPass
                )

                val jsonString = gson.toJson(requestData)
                val requestBody = jsonString.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(urlBase)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val responseBodyStr = response.body?.string()

                // QUAN TRỌNG: Server của bạn trả về ApiResponse<String>, không phải ComfirmPasswordResponse
                // Bạn cần parse đúng kiểu dữ liệu
                // Giả sử server trả về: { "success": true, "message": "...", "result": "..." }

                // Quay lại Main Thread để cập nhật UI
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseBodyStr != null) {
                        // Parse JSON (Tuỳ vào cấu trúc class ApiResponse của bạn)
                        // Nếu lười parse phức tạp, check response code 200 là được
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Đổi mật khẩu thành công!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Chuyển về màn hình Login
                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Thất bại: ${response.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        "Lỗi kết nối: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("Reset Password", e.message.toString())
                }
            }
        }
    }
}