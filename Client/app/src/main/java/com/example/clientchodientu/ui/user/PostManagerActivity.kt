package com.example.clientchodientu.ui.user

import android.os.Bundle
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
import com.example.clientchodientu.adapter.AdapterSellingProduct
import com.example.clientchodientu.dto.product.PostManagerResponse
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.TokenManager
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request

class PostManagerActivity : AppCompatActivity() {
    private lateinit var rcv : RecyclerView
    private lateinit var selling : TextView
    private val gson = GsonBuilder().create()
    private lateinit var sold : TextView
    private lateinit var token : String

    private val sellingUrl = "http://10.0.2.2:8080/api/product/my-selling"
    private val soldUrl = "http://10.0.2.2:8080/api/product/my-sold"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rcv = findViewById(R.id.rvPosts)
        selling = findViewById(R.id.btnSelling)
        sold = findViewById(R.id.btnSold)
        TokenManager.init(this)
        token = TokenManager.getToken().toString()
        rcv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            loadSellingProduct()
        }

        selling.setOnClickListener {
            selling.setTextColor(resources.getColor(R.color.my_active_tab, null))
            sold.setTextColor(resources.getColor(R.color.my_inactive_tab, null))
            lifecycleScope.launch {
                loadSellingProduct()
            }
        }

        sold.setOnClickListener {
            sold.setTextColor(resources.getColor(R.color.my_active_tab, null))
            selling.setTextColor(resources.getColor(R.color.my_inactive_tab, null))
            lifecycleScope.launch {
                loadSoldProduct()
            }
        }

    }

    suspend fun loadSellingProduct() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(sellingUrl)
                    .build()
                val response = ApiClient.getClient(this@PostManagerActivity).newCall(request).execute()
                val responseBody = response.body?.string()

                if(response.isSuccessful && responseBody != null) {
                    val sellingProduct = gson.fromJson(responseBody, PostManagerResponse::class.java)

                    withContext(Dispatchers.Main) {
                        val listSelling = sellingProduct.data
                        val message = sellingProduct.message
                        val success = sellingProduct.success

                        if(success) {
                            val adapter = AdapterSellingProduct(listSelling)
                            rcv.adapter = adapter
                            println("Dữ liệu về: ${listSelling.size} sản phẩm")
                            if (listSelling.isEmpty()) {
                                Toast.makeText(this@PostManagerActivity, "Không có tin đăng nào", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@PostManagerActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            catch (e : Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PostManagerActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun loadSoldProduct() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(soldUrl).build()
                val response = ApiClient.getClient(this@PostManagerActivity).newCall(request).execute()
                val responseBody = response.body?.string()

                if(response.isSuccessful && responseBody != null) {
                    val soldProduct = gson.fromJson(responseBody, PostManagerResponse::class.java)

                    val listSold = soldProduct.data
                    val message = soldProduct.message
                    val success = soldProduct.success

                    withContext(Dispatchers.Main) {
                        if(success) {
                            val adapter = AdapterSellingProduct(soldProduct.data)
                            rcv.adapter = adapter

                            if (listSold.isEmpty()) {
                                Toast.makeText(this@PostManagerActivity, "Không có tin đăng nào", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@PostManagerActivity, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}