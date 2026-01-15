package com.example.clientchodientu.ui.home
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exampletemplate.AdapterCategory
import com.example.clientchodientu.adapter.AdapterProduct
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.dto.product.ProductResponse
import com.example.clientchodientu.R
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
class HomeActivity : AppCompatActivity() {
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var recyclerViewCategory : RecyclerView
    private val client = OkHttpClient()
    private val urlProduct = "http://10.0.2.2:8080/api/product/getAll"
    private val urlCategory = "http://10.0.2.2:8080/api/category/getCategories"
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
        recyclerViewProduct = findViewById<RecyclerView>(R.id.rvProduct)
        recyclerViewProduct.layoutManager = LinearLayoutManager(this)
        recyclerViewCategory = findViewById<RecyclerView>(R.id.rvCategory)
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        lifecycleScope.launch {
            loadProduct()
            loadCategory()
        }

    }

    suspend fun loadProduct(categoryId:Int?=null) {
        val finalurl = if (categoryId == null) {
            urlProduct
        } else {
            "http://10.0.2.2:8080/api/product/getCategories?categoryId=$categoryId/"
        }
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(finalurl)
                .build()
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val productResponse = gson.fromJson(responseBody, ProductResponse::class.java)
                    withContext(Dispatchers.Main) {
                        if (productResponse.data != null && productResponse.data.isNotEmpty()) {
                            val adapter = AdapterProduct(productResponse.data)
                            recyclerViewProduct.adapter = adapter
                        }
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
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    // Parse dữ liệu từ JSON
                    val categoryResponse = gson.fromJson(responseBody, CategoryResponse::class.java)

                    withContext(Dispatchers.Main) {
                        if (categoryResponse.data != null && categoryResponse.data.isNotEmpty()) {
                            // Khởi tạo Adapter với lambda click
                            val adaptercatergory =
                                AdapterCategory(categoryResponse.data) { selectedCategory ->
                                    // Khi bấm vào 1 category, load lại danh sách sản phẩm theo ID đó
                                    lifecycleScope.launch {
                                        loadProduct(selectedCategory.id)
                                    }
                                }
                            recyclerViewCategory.adapter = adaptercatergory
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.printStackTrace()
                    Log.e("API_ERROR", "Lỗi Category: ${e.message}")
                    Toast.makeText(
                        this@HomeActivity,
                        "Lỗi tải danh mục: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
