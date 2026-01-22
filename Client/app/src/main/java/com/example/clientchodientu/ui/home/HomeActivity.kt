package com.example.clientchodientu.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exampletemplate.AdapterCategory
import com.example.clientchodientu.adapter.AdapterProduct
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.dto.product.ProductResponse
import com.example.clientchodientu.R
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.TokenManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.toString
import com.example.clientchodientu.ui.product.ProductDetailActivity
import com.example.clientchodientu.ui.user.PostManagerActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView
    private val client = OkHttpClient()

    private lateinit var progressBar: ProgressBar

    private lateinit var searchView: SearchView

    private var searchJob: Job? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("Chat", "Notification permission granted")
        } else {
            Log.e("Chat", "Notification permission denied")
        }
    }
    // --- SỬA ĐOẠN NÀY ---
    // 1. Khai báo IP máy tính của bạn (để dùng chung)
    private val SERVER_IP = "10.0.2.2"
    private val PORT = "8080"

    // 2. Cập nhật URL dùng biến IP trên
    private val urlProduct = "http://$SERVER_IP:$PORT/api/product/getAll"
    private val urlCategory = "http://$SERVER_IP:$PORT/api/category/getCategories"
    // --------------------
//    private val urlProduct = "http://10.0.2.2:8080/api/product/getAll"
//    private val urlCategory = "http://10.0.2.2:8080/api/category/getCategories"

    private lateinit var token: String
    val gson = GsonBuilder()
        .create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        askNotificationPermission();
        TokenManager.init(this)
        token = TokenManager.getToken().toString()
        Log.d("TOKEN_HOME", "Token lấy được: $token")
        recyclerViewProduct = findViewById<RecyclerView>(R.id.rvProduct)
        recyclerViewProduct.layoutManager = LinearLayoutManager(this)
        recyclerViewCategory = findViewById<RecyclerView>(R.id.rvCategory)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)

        progressBar.visibility = View.GONE

        recyclerViewCategory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        lifecycleScope.launch {
            loadProduct()
            loadCategory()
        }
        progressBar = findViewById(R.id.progressBar)
        searchView = findViewById(R.id.searchView)
        setupSearchView()


        bottomNav = findViewById(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    // Đang ở Home rồi thì không cần làm gì hoặc cuộn lên đầu
                    recyclerViewProduct.smoothScrollToPosition(0)
                    true
                }
                R.id.nav_save -> {
                     startActivity(Intent(this, PostManagerActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    // Mở màn hình đăng tin
                    true
                }
                R.id.nav_chat -> {
                    // Mở màn hình Chat
                    true
                }
                R.id.nav_account -> {
                    // Mở màn hình Profile/Account
                    true
                }
                else -> false
            }
        }

    }

    suspend fun loadProduct(categoryId: Int? = null) {
        val finalurl = if (categoryId == null) {
            urlProduct
        } else {
//            "http://10.0.2.2:8080/api/product/getByCategory?categoryId=$categoryId"
            "http://$SERVER_IP:$PORT/api/product/getByCategory?categoryId=$categoryId"
        }
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(finalurl)
                .build()
            try {
                val response = ApiClient.getClient(this@HomeActivity).newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val productResponse = gson.fromJson(responseBody, ProductResponse::class.java)
                    withContext(Dispatchers.Main) {
                        val tvEmpty = findViewById<TextView>(R.id.tvEmptyState)
                        if (productResponse.data != null && productResponse.data.isNotEmpty()) {
                            recyclerViewProduct.visibility = android.view.View.VISIBLE
                            tvEmpty.visibility = android.view.View.GONE
                            val adapter = AdapterProduct(productResponse.data)
                            // Gán sự kiện click chuyển trang
                            adapter.onItemClick = { productId ->
                                val intent =
                                    Intent(this@HomeActivity, ProductDetailActivity::class.java)
                                intent.putExtra("PRODUCT_ID", productId)
                                startActivity(intent)
                            }
                            recyclerViewProduct.adapter = adapter
                        } else {
                            recyclerViewProduct.visibility = android.view.View.GONE
                            tvEmpty.visibility = android.view.View.VISIBLE

                            val adapter = AdapterProduct(emptyList())
                            recyclerViewProduct.adapter = adapter
                        }
                        Log.d("API_SUCCESS", "Số sản phẩm tải được: ${productResponse.data.size}")
                        Log.d("API_SUCCESS", "Dữ liệu sản phẩm: $responseBody")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("API_ERROR", "Lỗi tải sản phẩm: ${response.message}")
                        Toast.makeText(
                            this@HomeActivity,
                            "Lỗi tải sản phẩm: ${response.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Log.e("GSON_LOI", "Chi tiết: ${e.message}")
                    Toast.makeText(
                        this@HomeActivity,
                        "Lỗi: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    suspend fun loadCategory() {
        withContext(Dispatchers.IO) {
            // Sử dụng biến client và url đã khai báo ở trên class
            val request = Request.Builder()
                .url(urlCategory)
                .build()

            try {
                val response = ApiClient.getClient(this@HomeActivity).newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // Parse dữ liệu từ JSON
                    val categoryResponse = gson.fromJson(responseBody, CategoryResponse::class.java)

                    withContext(Dispatchers.Main) {
                        if (categoryResponse.data != null && categoryResponse.data.isNotEmpty()) {
                            // Khởi tạo Adapter với lambda click
                            val adaptercatergory =
                                AdapterCategory(categoryResponse.data) { selectedCategory ->
                                    android.util.Log.d(
                                        "CLICK_TEST",
                                        "Đã click vào: ${selectedCategory.name} - ID: ${selectedCategory.id}"
                                    )
                                    lifecycleScope.launch {
                                        loadProduct(selectedCategory.id)
                                    }
                                }
                            Log.d("respond_cate", categoryResponse.data.toString())
                            adaptercatergory.updateData(categoryResponse.data)
                            recyclerViewCategory.adapter = adaptercatergory
                        }
                        Log.d("API_SUCCESS", "Số danh mục tải được: ${categoryResponse.data.size}")
                        Log.d("API_SUCCESS", "Dữ liệu danh mục: $responseBody")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("API_ERROR", "Lỗi tải danh mục: ${response.message}")
                        Toast.makeText(
                            this@HomeActivity,
                            "Lỗi tải danh mục: ${response.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Log.e("API_ERROR", "Lỗi Category: ${e.message}")
                    Toast.makeText(
                        this@HomeActivity,
                        "Lỗi: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            // Khi nhấn nút Enter trên bàn phím
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchJob?.cancel()
                    performSearchWithOkHttp(it)
                    searchView.clearFocus() // Ẩn bàn phím
                }
                return true
            }

            // Khi gõ từng chữ (Search as you type)
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onQueryTextChange(newText: String?): Boolean {
                // Kỹ thuật DEBOUNCE (Trễ 500ms)
                searchJob?.cancel() // Hủy job cũ
                searchJob = lifecycleScope.launch {
                    delay(500) // Đợi 500ms xem user có gõ tiếp không

                    if (!newText.isNullOrBlank()) {
                        performSearchWithOkHttp(newText)
                    } else {
                        // Nếu xóa trắng thì ẩn list hoặc clear data
                        runOnUiThread {
                            Log.d("SEARCH_RESULT", "Search text is empty")
//                            AdapterProduct.submitList(emptyList())
                            val adapterProduct = AdapterProduct(emptyList())
                            adapterProduct.updateData(emptyList())
                        }
                    }
                }
                return true
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun performSearchWithOkHttp(keyword: String) {
        // Hiện loading
        runOnUiThread { progressBar.visibility = View.VISIBLE }

        // Chạy trên luồng IO (Background)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // 1. Tạo URL chuẩn (Lưu ý: 10.0.2.2 là localhost của máy ảo)
                val urlBuilder = HttpUrl.Builder()
                    .scheme("http")
                    .host(SERVER_IP)
                    .port(8080)
                    .addPathSegments("api/product/filter")
                    .addQueryParameter("name", keyword)
                    .addQueryParameter("sort", "newest")
                    .build()

                // 2. Tạo Request
                val request = Request.Builder()
                    .url(urlBuilder)
                    .get()
                    .build()

                // 3. Thực thi
                val response = ApiClient.getClient(this@HomeActivity).newCall(request).execute()

                // 4. Xử lý kết quả trả về
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()

                    if (jsonString != null) {
                        // Parse JSON: ApiResponse<List<Product>>
                        val productResponse = gson.fromJson(jsonString, ProductResponse::class.java)

                        // Cập nhật UI (Phải về Main Thread)
                        withContext(Dispatchers.Main) {
//                            productAdapter.submitList(productResponse.data)
                            val adapterProduct = AdapterProduct(productResponse.data)
                            recyclerViewProduct.adapter = adapterProduct
                            adapterProduct.updateData(productResponse.data)
                            progressBar.visibility = View.GONE
                            Log.d("SEARCH_RESULT", "Found ${productResponse.data.size} products")
                            Log.d("SEARCH_RESULT", productResponse.data.toString())
                            progressBar.visibility = View.GONE
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@HomeActivity,
                            "Lỗi server: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("API_ERROR", e.toString())
                    Toast.makeText(this@HomeActivity, "Lỗi kết nối!", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}