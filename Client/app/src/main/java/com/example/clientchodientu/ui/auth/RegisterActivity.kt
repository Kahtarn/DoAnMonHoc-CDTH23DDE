package com.example.clientchodientu.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.entity.Province
import com.example.clientchodientu.entity.Ward
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import android.view.View // FIX: Thêm import này
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import com.example.clientchodientu.dto.auth.register.OtpRequest
import com.example.clientchodientu.dto.auth.register.OtpResponse
import com.example.clientchodientu.dto.auth.register.RegisterRequest
import com.example.clientchodientu.dto.auth.register.RegisterResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.collections.toList
import kotlin.io.use
import kotlin.jvm.java
import kotlin.stackTraceToString
import kotlin.text.isEmpty
import kotlin.text.trim
import kotlin.toString
import com.example.clientchodientu.R


class RegisterActivity : AppCompatActivity() {
    private lateinit var edtUsername: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtFullName: EditText
    private lateinit var edtPhone: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var btnRegister: Button

    private lateinit var radioGroupGender: RadioGroup
    private var selectedGender: Boolean = true
    private lateinit var sTinh: Spinner
    private lateinit var sHuyen: Spinner
    private lateinit var edtOtpCode: EditText
    private var selectedProvinceCode: Int = 0
    private var selectedWardCode: Int = 0

    private var countDownTimer: CountDownTimer? = null
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sTinh = findViewById<Spinner>(R.id.sTinh)
        sHuyen = findViewById<Spinner>(R.id.sHuyen)
        edtUsername = findViewById<EditText>(R.id.edtUsername)
        edtEmail = findViewById<EditText>(R.id.edtEmail)
        edtPassword = findViewById<EditText>(R.id.edtPassword)
        edtFullName = findViewById<EditText>(R.id.edtFullname)
        edtPhone = findViewById<EditText>(R.id.edtPhone)
        edtOtpCode = findViewById<EditText>(R.id.edtOtp)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        btnRegister = findViewById<Button>(R.id.btnRegister)
        btnSendOtp = findViewById<Button>(R.id.btnSendOTPRegister)

        //mac dinh la Nam
        radioGroupGender.check(R.id.rbtnNam)
        radioGroupGender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbtnNam -> selectedGender = true
                R.id.rbtnNu -> selectedGender = false
            }
        }


        lifecycleScope.launch {
            loadProviceData()
        }
        btnSendOtp.setOnClickListener {
            edtEmail.clearFocus()

            // Hide keyboard
            val imm = getSystemService(INPUT_METHOD_SERVICE)
                    as InputMethodManager
            imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)

            val email = edtEmail.text.toString().trim()

            if (email.isEmpty()) {
                focusEditText(edtEmail)
                edtEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            } else {
                //check dinh dang email
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // 3️ Confirm click
                    Log.d("BTN_SEND_OTP", "Button clicked with email=$email")
                    lifecycleScope.launch {
                        sendOtp(email)
                    }
                } else {
                    Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegister.setOnClickListener {
            val userName = edtUsername.text.toString().trim()
            val fullName = edtFullName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val gender = selectedGender
            val provinceName = sTinh.selectedItem.toString()
            val wardName = sHuyen.selectedItem.toString()
            val otpCode = edtOtpCode.text.toString().trim()

            if (userName.isEmpty()) {
                focusEditText(edtUsername)
                edtUsername.error = "Vui lòng nhập tên đăng nhập"
                return@setOnClickListener
            }
            if (fullName.isEmpty()) {
                focusEditText(edtFullName)
                edtFullName.error = "Vui lòng nhập họ và tên"
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                focusEditText(edtPassword)
                edtPassword.error = "Vui lòng nhập mật khẩu"
                return@setOnClickListener
            }
            if (password.length < 6) {
                focusEditText(edtPassword)
                edtPassword.error = "Mật khẩu phải có ít nhất 6 ký tự"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                focusEditText(edtPhone)
                edtPhone.error = "Vui lòng nhập số điện thoại"
                return@setOnClickListener
            }
            if (phone.length !== 10) {
                focusEditText(edtPhone)
                edtPhone.error = "Số điện thoại phải có 10 chữ số"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                focusEditText(edtEmail)
                edtEmail.error = "Vui lòng nhập email"
                return@setOnClickListener
            }
            if (otpCode.isEmpty()) {
                focusEditText(edtOtpCode)
                edtOtpCode.error = "Vui lòng nhập mã OTP"
                return@setOnClickListener
            } else {
                // send register request
                lifecycleScope.launch {
                    sendRegisterRequest(
                        email,
                        userName,
                        password,
                        fullName,
                        phone,
                        gender,
                        provinceName,
                        wardName,
                        otpCode
                    );
                }
            }

        }
    }

    fun focusEditText(editText: EditText) {
        editText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    suspend fun sendOtp(email: String) {
        startCountDown(btnSendOtp)
        withContext(Dispatchers.IO) {
            try {
                val url = "http://10.0.2.2:8080/api/auth/send-otp"

                val gson = Gson()
                val otpRequest = OtpRequest(email)
                val jsonString = gson.toJson(otpRequest)
                Log.d("OTP_REQUEST_STRING", jsonString)
                val requestBody =
                    jsonString.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val rawBody = response.body?.string()

                Log.d("OTP_RAW_RESPONSE", rawBody.toString() ?: "null")

                val otpResponse = gson.fromJson(rawBody, OtpResponse::class.java)
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            otpResponse.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@withContext
                }


                withContext(Dispatchers.Main) {
                    if (otpResponse.success) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Gửi OTP thành công, kiểm tra email",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            otpResponse.message ?: "Gửi OTP thất bại",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                Log.e("SEND_OTP_ERROR", e.stackTraceToString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    suspend fun sendRegisterRequest(
        email: String,
        username: String,
        password: String,
        fullName: String,
        phone: String,
        gender: Boolean,
        provinceName: String,
        wardName: String,
        otpCode: String
    ) {
        withContext(Dispatchers.IO) {
            try {

                val gson = Gson()
                val registerRequest = RegisterRequest(
                    username,
                    email,
                    password,
                    fullName,
                    phone,
                    gender,
                    provinceName,
                    wardName,
                    otpCode
                )
                val jsonString = gson.toJson(registerRequest)
                Log.d("REGISTER_REQUEST_STRING", jsonString)
                val requestBody =
                    jsonString.toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/auth/register")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                val rawBody = response.body?.string()
                Log.d("OTP_RAW_RESPONSE", rawBody.toString() ?: "null")
                val registerResponse = gson.fromJson(rawBody, RegisterResponse::class.java)
                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@RegisterActivity,
                            registerResponse.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@withContext
                }

                withContext(Dispatchers.Main) {
                    if (registerResponse.success) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Đăng ký thành công, vui lòng đăng nhập",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            this@RegisterActivity,
                            registerResponse.message ?: "Đăng ký thất bại",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("REGISTER_ERROR", e.stackTraceToString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    suspend fun loadProviceData() {
        withContext(Dispatchers.IO) {
            try {
                val url = "https://provinces.open-api.vn/api/v2/p/"
                val request = Request.Builder().url(url).get().build()

                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()

                    if (response.isSuccessful && responseData != null) {
                        val data =
                            Gson().fromJson(responseData, Array<Province>::class.java).toList()
//                        val provinceName = data.map { it.name }
                        // Chuyển về Main Thread để Log và Update Spinner
                        withContext(Dispatchers.Main) {
                            // Bind dữ liệu vào Spinner luôn
                            val adapter = ArrayAdapter(
                                this@RegisterActivity,
                                android.R.layout.simple_spinner_item,
                                data
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            sTinh.adapter = adapter

                            sTinh.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        // Bây giờ dòng này mới chạy đúng vì adapter chứa Object Province
                                        val item = parent?.getItemAtPosition(position) as Province
                                        selectedProvinceCode = item.code
                                        Log.d("province_code", item.code.toString())
                                        // Gọi load Huyện với mã tỉnh vừa chọn
                                        lifecycleScope.launch {
                                            loadWardData(selectedProvinceCode)
                                        }
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("Load Address", "Response failed")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Load Address", "Error: ${e.message}")
                }
            }
        }
    }

    suspend fun loadWardData(provinceCode: Int) {
        withContext(Dispatchers.IO) {
            try {
                val url = "https://provinces.open-api.vn/api/v2/p/${provinceCode}?depth=2"
                val request = Request.Builder().url(url).get().build()

                client.newCall(request).execute().use { response ->
                    val responseData = response.body?.string()

                    if (response.isSuccessful && responseData != null) {
                        val province = Gson().fromJson(responseData, Province::class.java)
                        val listWard = province.ward ?: listOf()

                        // Chuyển về Main Thread để Log và Update Spinner
                        withContext(Dispatchers.Main) {
                            Log.d("Load Address", "Districts size: ${listWard.size}")

                            // Bind dữ liệu vào Spinner luôn
                            val adapter = ArrayAdapter(
                                this@RegisterActivity,
                                android.R.layout.simple_spinner_item,
                                listWard
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            sHuyen.adapter = adapter
                            sHuyen.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        val item = parent?.getItemAtPosition(position) as Ward
                                        selectedWardCode = item.code
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                                }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("Load Address", "Response failed")
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Load Address", "Error: ${e.message}")
                }
            }
        }
    }

    private fun startCountDown(button: Button) {
        // 1. Disable nút để không bấm được nữa
        button.isEnabled = false

        // Lưu lại text gốc (ví dụ: "Gửi mã OTP") để trả lại sau khi đếm xong
        val originalText = "Gửi lại mã OTP"

        // 2. Tạo bộ đếm ngược
        // Tham số 1: Tổng thời gian (60000ms = 60 giây)
        // Tham số 2: Bước nhảy (1000ms = 1 giây)
        countDownTimer = object : CountDownTimer(60000, 1000) {

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