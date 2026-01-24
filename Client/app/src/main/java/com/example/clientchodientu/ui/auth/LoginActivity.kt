package com.example.clientchodientu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.auth.login.LoginRequest
import com.example.clientchodientu.dto.auth.login.LoginResponse
import com.example.clientchodientu.ui.home.HomeActivity
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.TokenManager
import com.google.firebase.messaging.FirebaseMessaging
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

class LoginActivity : AppCompatActivity() {
    private lateinit var edtUsernameOrEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private var client = OkHttpClient()
    private val urlLogin = "http://10.0.2.2:8080/api/auth/login"

    private val urlSetFCMToken = "http://10.0.2.2:8080/api/chat/set-fcm-token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnForgotPassword = findViewById<Button>(R.id.btnForgotPassword)

        btnLogin = findViewById<Button>(R.id.btnLogin)
        btnRegister = findViewById<Button>(R.id.btnRegister)
        edtUsernameOrEmail = findViewById<EditText>(R.id.edtUsernameOrEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)

        TokenManager.init(this)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        btnLogin.setOnClickListener {
            val usernameOrEmail = edtUsernameOrEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Lỗi")
                    .setMessage("Vui lòng nhập đầy đủ thông tin.")
                    .setPositiveButton("OK", null)
                    .create()
                dialog.show()
            } else {
                lifecycleScope.launch {
                    login(usernameOrEmail, password);
                }
            }
        }

        btnForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    suspend fun login(usernameOrEmail: String, password: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val loginRequest = LoginRequest(usernameOrEmail, password)
                val jsonString = gson.toJson(loginRequest)
                val requestBody = jsonString.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(urlLogin)
                    .post(requestBody)
                    .build()
                val response = ApiClient.getClient(this@LoginActivity).newCall(request).execute()
                val responseString = response.body?.string()
                val data = gson.fromJson(responseString, LoginResponse::class.java)
                if (response.isSuccessful) {
                    if (data.success) {
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w("FCM", "Lấy token thất bại", task.exception)
                                return@addOnCompleteListener
                            }

                            // 3. Gửi Token lên Server
                            val token = task.result
                            Log.d("FCM", "Token hiện tại: $token")
                            TokenManager.updateFCMToken(this@LoginActivity,data.data.userId, token)

                            TokenManager.saveTokens(
                                data.data.accessToken,
                                data.data.refreshToken,
                                token,data.data.userId
                            )
                        }
                        withContext(Dispatchers.Main) {
                            Log.d("token", data.data.accessToken)
                            Toast.makeText(this@LoginActivity, data.message, Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
//                            finish()
                            Result.success(data)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            val dialog = AlertDialog.Builder(this@LoginActivity)
                                .setTitle("Lỗi")
                                .setMessage(data.message)
                                .setPositiveButton("OK", null)
                                .create()
                            dialog.show()
                            Result.failure(kotlin.Exception("Lỗi đăng nhập: ${data.message}"))
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        val dialog = AlertDialog.Builder(this@LoginActivity)
                            .setTitle("Lỗi")
                            .setMessage(data.message)
                            .setPositiveButton("OK", null)
                            .create()
                        dialog.show()
                        Result.failure(kotlin.Exception("Lỗi phản hồi từ máy chủ: ${response.code}"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                        .setTitle("Lỗi")
                        .setMessage("Đã xảy ra lỗi: ${e.localizedMessage}")
                        .setPositiveButton("OK", null)
                        .create()
                    dialog.show()
                }
                Result.failure(e)
            }
        }
    }
}