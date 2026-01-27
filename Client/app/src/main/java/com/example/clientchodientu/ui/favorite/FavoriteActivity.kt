package com.example.clientchodientu.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.AdapterFavorite
import com.example.clientchodientu.dto.product.DeleteProduct
import com.example.clientchodientu.dto.product.PostFavoriteResponse
import com.example.clientchodientu.dto.product.SetFavoriteRespond
import com.example.clientchodientu.entity.Product
import com.example.clientchodientu.ui.edit.EditPostActivity
import com.example.clientchodientu.ui.product.ProductDetailActivity
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.ApiResponseData
import com.example.clientchodientu.untils.TokenManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.properties.Delegates

class FavoriteActivity : AppCompatActivity() {
    lateinit var listFavorite: List<Product>
    private lateinit var rcv: RecyclerView
    private lateinit var back: ImageButton
    private val gson = GsonBuilder().create()
    private lateinit var token: String


    private var currentProductId: Int = -1
    private var BASE_URL: String = "http://10.0.2.2:8080/api/product"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorite)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        currentProductId = intent.getIntExtra("PRODUCT_ID", -1)
        Log.d("FavoriteActivity", "Received PRODUCT_ID: $currentProductId")
        rcv = findViewById(R.id.rvPosts)
        back = findViewById(R.id.btnBack)
        back.setOnClickListener {
            finish()
        }
        TokenManager.init(this)
        token = TokenManager.getToken().toString()
        rcv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            loadFavoriteProduct()
        }

    }

    suspend fun loadFavoriteProduct() {
        withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL/get-favorite"
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()
                val response = ApiClient.getClient(this@FavoriteActivity).newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val favoriteProduct =
                        gson.fromJson(responseBody, PostFavoriteResponse::class.java)

                    withContext(Dispatchers.Main) {
                        listFavorite = favoriteProduct.data
                        val message = favoriteProduct.message
                        val success = favoriteProduct.success

                        if (success) {
                            setupAdapter(listFavorite.toMutableList())

                            println("Dữ liệu về: ${listFavorite.size} sản phẩm")
                            if (listFavorite.isEmpty()) {
                                Toast.makeText(
                                    this@FavoriteActivity,
                                    "Không có sản phẩm nào",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(this@FavoriteActivity, message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@FavoriteActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun updateFavoriteStatus() {
        val url = "$BASE_URL/set-favorite/$currentProductId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody("application/json".toMediaType()))
            .build()

        ApiClient.getClient(this).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { showToast("Lỗi kết nối: ${e.message}") }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        showToast("Lỗi Server: ${response.code}")
                    }
                    return
                }
                try {
                    val json = response.body?.string()
                    val type = object : TypeToken<ApiResponseData<SetFavoriteRespond>>() {}.type
                    val apiResponse = gson.fromJson<ApiResponseData<SetFavoriteRespond>>(json, type)

                    if (apiResponse.success) {

                        runOnUiThread {
                            Log.d("ProductDetail", "Favorite status:$currentProductId false")
                        }
                    } else {
                        runOnUiThread {
                            showToast(apiResponse.message)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { showToast("Lỗi xử lý dữ liệu") }
                }

            }
        })
    }

    private fun setupAdapter(list: MutableList<Product>) {
        val adapter = AdapterFavorite(list)
        rcv.adapter = adapter
        adapter.onItemClick = { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }

        adapter.onFavoriteClick = { product ->
            currentProductId = product.id
            updateFavoriteStatus()
            val currentList = list.toMutableList()
            val index = currentList.indexOfFirst { it.id == product.id }

            if (index != -1) {
                currentList.removeAt(index)
                // Cập nhật lại list mới cho adapter
                adapter.updateData(currentList)

                Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}