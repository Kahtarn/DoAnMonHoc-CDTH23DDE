package com.example.clientchodientu.ui.user

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.user.ApiUserResponse
import com.example.clientchodientu.dto.user.DetailsUserResponse
import com.example.clientchodientu.untils.token.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request

class PublicDetailsUserActivity : AppCompatActivity() {

    private var provinceName = ""
    private var wardName = ""
    private val gson = Gson()
    private var sellerId = -1
    private lateinit var tvFullname : TextView
    private lateinit var tvGender : TextView
    private lateinit var tvAddress : TextView
    private lateinit var tvEmail : TextView
    private lateinit var tvPhone : TextView
    private lateinit var tvCreateAt : TextView
    private lateinit var rvPost : RecyclerView
    private lateinit var btnSelling : TextView
    private lateinit var btnSold : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_public_details_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        init()
        receiveData()
        lifecycleScope.launch {
            loadDetailsUser()
        }
    }

    private fun init() {
        tvFullname = findViewById(R.id.tvFullName)
        tvGender = findViewById(R.id.tvGender)
        tvAddress = findViewById(R.id.tvAddress)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvCreateAt = findViewById(R.id.tvCreateAt)
        rvPost = findViewById(R.id.rvUserProducts)
        btnSelling = findViewById(R.id.btnSelling)
        btnSold = findViewById(R.id.btnSold)
    }

    private fun receiveData() {
        sellerId = intent.getIntExtra("sellerId", -1)
        Log.d("CHECK_ID", "ID nhận được: $sellerId")
    }

    private fun bindData(user: DetailsUserResponse) {
        tvFullname.text = user.fullName
        tvEmail.text = "Email: ${user.email}"
        tvPhone.text = "SĐT: ${user.phone}"
        tvGender.text = "Giới tính: ${if (user.gender) "Nữ" else "Nam"}"
        tvAddress.text = "Địa chỉ: ${user.wardName}, ${user.provinceName}"
        tvCreateAt.text = "Ngày tham gia: ${formatDate(user.createAt)}"
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateStr)
            val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    suspend fun loadDetailsUser() {
        withContext(Dispatchers.IO) {
            try {
                val url = "http://10.0.2.2:8080/api/user/details-user/${sellerId}"
                val request = Request.Builder().url(url).build()
                val response =
                    ApiClient.getClient(this@PublicDetailsUserActivity).newCall(request).execute()

                val responseBodyString = response.body?.string()

                if (response.isSuccessful && !responseBodyString.isNullOrEmpty()) {
                    val apiResponse = gson.fromJson(responseBodyString, ApiUserResponse::class.java)

                    withContext(Dispatchers.Main) {
                        apiResponse.data?.let { user ->
                            bindData(user)
                        } ?: run {
                            android.widget.Toast.makeText(
                                this@PublicDetailsUserActivity,
                                "Không có dữ liệu người dùng",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(
                            this@PublicDetailsUserActivity,
                            "Lỗi Server: ${response.code}",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        this@PublicDetailsUserActivity,
                        "Lỗi kết nối mạng",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}