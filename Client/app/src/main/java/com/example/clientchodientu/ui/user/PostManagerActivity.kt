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
import com.example.clientchodientu.dto.product.DeleteProduct
import com.example.clientchodientu.dto.product.PostManagerResponse
import com.example.clientchodientu.entity.Product
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
                            setupAdapter(listSelling)

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
                            setupAdapter(listSold)

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

    private fun showDeleteDialog(productId: Int) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa bài viết này không?")
            .setPositiveButton("Xóa") { _, _ ->
                lifecycleScope.launch {
                    deleteProductApi(productId)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun setupAdapter(list: List<Product>) {
        val adapter = AdapterSellingProduct(list)
        rcv.adapter = adapter

        adapter.onDeleteClick = { product ->
            showDeleteDialog(product.id)
        }

        adapter.onEditClick = { product ->

        }
    }

    private suspend fun deleteProductApi(productId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val deleteUrl = "http://10.0.2.2:8080/api/product/delete/$productId"

                val request = Request.Builder()
                    .url(deleteUrl)
                    .delete()
                    .build()

                val response = ApiClient.getClient(this@PostManagerActivity).newCall(request).execute()
                val responseBody = response.body?.string()

                val apiResponse = gson.fromJson(responseBody, DeleteProduct::class.java)
                val success = apiResponse.success
                val message = apiResponse.message

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && success) {
                        Toast.makeText(this@PostManagerActivity, message, Toast.LENGTH_SHORT).show()
                        loadSellingProduct()
                    } else {
                        val errorMsg = apiResponse?.message ?: "Xóa thất bại"
                        Toast.makeText(this@PostManagerActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PostManagerActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}