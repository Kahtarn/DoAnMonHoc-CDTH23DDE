package com.example.clientchodientu.ui.user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.AdapterPostOfUser
import com.example.clientchodientu.dto.product.PostManagerResponse
import com.example.clientchodientu.dto.user.ApiUserResponse
import com.example.clientchodientu.dto.user.DetailsUserResponse
import com.example.clientchodientu.entity.Product
import com.example.clientchodientu.ui.product.ProductDetailActivity
import com.example.clientchodientu.untils.token.ApiClient
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request

class PublicDetailsUserActivity : AppCompatActivity() {
    private lateinit var postAdapter: AdapterPostOfUser
    private lateinit var btnBack : ImageView
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
    private lateinit var ivAvatar: ImageView

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
            loadSellingProduct()
        }

        btnSelling.setOnClickListener {
            btnSelling.setTextColor(resources.getColor(R.color.my_active_tab, null))
            btnSold.setTextColor(resources.getColor(R.color.my_inactive_tab, null))
            lifecycleScope.launch { loadSellingProduct() }
        }

        btnSold.setOnClickListener {
            btnSold.setTextColor(resources.getColor(R.color.my_active_tab, null))
            btnSelling.setTextColor(resources.getColor(R.color.my_inactive_tab, null))
            lifecycleScope.launch { loadSoldProduct() }
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun init() {
        tvFullname = findViewById(R.id.tvFullName)
        tvGender = findViewById(R.id.tvGender)
        tvAddress = findViewById(R.id.tvAddress)
        tvEmail = findViewById(R.id.tvEmail)
        tvPhone = findViewById(R.id.tvPhone)
        tvCreateAt = findViewById(R.id.tvCreatedAt)
        rvPost = findViewById(R.id.rvUserProducts)
        btnSelling = findViewById(R.id.btnSelling)
        btnSold = findViewById(R.id.btnSold)
        postAdapter = AdapterPostOfUser(emptyList())
        rvPost.layoutManager = LinearLayoutManager(this)
        rvPost.adapter = postAdapter
        ivAvatar = findViewById(R.id.ivAvatar)
        btnBack = findViewById(R.id.ivBack)
    }

    private fun receiveData() {
        sellerId = intent.getIntExtra("sellerId", -1)
    }
    private fun resolveUrl(path: String?): String {
        val baseUrl = "http://10.0.2.2:8080"
        if (path.isNullOrBlank() || path == "null") return ""
        val cleanPath = if (path.startsWith("/")) path else "/$path"
        return if (path.startsWith("http")) path else baseUrl + cleanPath
    }
    private fun bindData(user: DetailsUserResponse) {
        tvFullname.text = user.fullName
        tvEmail.text = "Email: ${user.email}"
        tvPhone.text = "SĐT: ${user.phone}"
        tvGender.text = "Giới tính: ${if (user.gender) "Nữ" else "Nam"}"
        tvAddress.text = "Địa chỉ: ${user.wardName}, ${user.provinceName}"
        tvCreateAt.text = "Ngày tham gia: ${formatDate(user.createAt)}"

        val avatarPath = user.avatarUrl
        if (!avatarPath.isNullOrEmpty() && avatarPath != "null") {
            val fullAvatarUrl = resolveUrl(avatarPath)

            Glide.with(this)
                .load(fullAvatarUrl)
                .placeholder(R.drawable.ic_user_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .circleCrop()
                .into(ivAvatar)
        } else {
            Log.e("DEBUG_URL", "Path ảnh bị rỗng hoặc null")
            ivAvatar.setImageResource(R.drawable.ic_user_placeholder)
        }
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
                val response = ApiClient.getClient(this@PublicDetailsUserActivity).newCall(request).execute()
                val responseBodyString = response.body?.string()
                if (response.isSuccessful && !responseBodyString.isNullOrEmpty()) {
                    val apiResponse = gson.fromJson(responseBodyString, ApiUserResponse::class.java)
                    withContext(Dispatchers.Main) {
                        val userData = apiResponse.data
                        if (userData != null) {
                            bindData(userData)
                        } else {
                            Log.e("AAA", "Loi: apiResponse.data bi NULL sau khi parse Gson")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun setupAdapter(list: List<Product>) {
        val adapter = AdapterPostOfUser(list)
        rvPost.layoutManager = LinearLayoutManager(this)
        rvPost.adapter = adapter
        adapter.onItemClick = { productId ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            startActivity(intent)
        }
    }

    suspend fun loadSellingProduct() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/product/public/selling/$sellerId")
                    .build()

                val response = ApiClient
                    .getClient(this@PublicDetailsUserActivity)
                    .newCall(request)
                    .execute()

                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val result = gson.fromJson(responseBody, PostManagerResponse::class.java)

                    withContext(Dispatchers.Main) {
                        if (result.success) {
                            val data = result.data ?: emptyList()
                            setupAdapter(result.data ?: emptyList())

                            if (data.isEmpty()) {
                                Toast.makeText(
                                    this@PublicDetailsUserActivity,
                                    "Không có tin đăng nào",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                this@PublicDetailsUserActivity,
                                "API trả về thất bại",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@PublicDetailsUserActivity,
                        "Lỗi kết nối",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    suspend fun loadSoldProduct() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/product/public/sold/$sellerId")
                    .build()
                val response = ApiClient
                    .getClient(this@PublicDetailsUserActivity)
                    .newCall(request)
                    .execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                    val result = gson.fromJson(responseBody, PostManagerResponse::class.java)
                    withContext(Dispatchers.Main) {
                        if (result.success) {
                            val data = result.data ?: emptyList()
                            setupAdapter(data)
                            if (data.isEmpty()) {
                                Toast.makeText(
                                    this@PublicDetailsUserActivity,
                                    "Không có tin đăng đã bán nào",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(this@PublicDetailsUserActivity, "API trả về thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PublicDetailsUserActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}