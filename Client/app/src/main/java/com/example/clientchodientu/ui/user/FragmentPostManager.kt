package com.example.clientchodientu.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.AdapterPostProductManage
import com.example.clientchodientu.dto.product.DeleteProduct
import com.example.clientchodientu.dto.product.PostManagerResponse
import com.example.clientchodientu.entity.Product
import com.example.clientchodientu.ui.edit.EditPostActivity
import com.example.clientchodientu.untils.token.ApiClient
import com.example.clientchodientu.untils.token.TokenManager

import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request

class FragmentPostManager : Fragment() {
    private lateinit var rcv: RecyclerView
    private lateinit var selling: TextView
    private lateinit var back: ImageButton
    private val gson = GsonBuilder().create()
    private lateinit var sold: TextView
    private lateinit var token: String

    private val sellingUrl = "http://10.0.2.2:8080/api/product/my-selling"
    private val soldUrl = "http://10.0.2.2:8080/api/product/my-sold"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.ProfileUser)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        rcv = view.findViewById(R.id.rvPosts)
        selling = view.findViewById(R.id.btnSelling)
        sold = view.findViewById(R.id.btnSold)
        back = view.findViewById(R.id.btnBack)
        TokenManager.init(requireContext())
        token = TokenManager.getToken().toString()
        rcv.layoutManager = LinearLayoutManager(requireContext())

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
                val response = ApiClient.getClient(requireContext()).newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val sellingProduct =
                        gson.fromJson(responseBody, PostManagerResponse::class.java)

                    withContext(Dispatchers.Main) {
                        val listSelling = sellingProduct.data
                        val message = sellingProduct.message
                        val success = sellingProduct.success

                        if (success) {
                            setupAdapter(listSelling, false)

                            println("Dữ liệu về: ${listSelling.size} sản phẩm")
                            if (listSelling.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Không có tin đăng nào",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    suspend fun loadSoldProduct() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(soldUrl).build()
                val response = ApiClient.getClient(requireContext()).newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val soldProduct = gson.fromJson(responseBody, PostManagerResponse::class.java)

                    val listSold = soldProduct.data
                    val message = soldProduct.message
                    val success = soldProduct.success

                    withContext(Dispatchers.Main) {
                        if (success) {
                            setupAdapter(listSold, true)

                            if (listSold.isEmpty()) {
                                Toast.makeText(
                                    requireContext(),
                                    "Không có tin đăng nào",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun markasSold(productId: Int) {
        withContext(Dispatchers.IO) {
            try {
                val url = "http://10.0.2.2:8080/api/product/$productId/mark-as-sold"
                val body = okhttp3.RequestBody.create(null, "")
                val request = Request.Builder()
                    .url(url)
                    .patch(body)
                    .build()

                val response = ApiClient.getClient(requireContext()).newCall(request).execute()
                val responseBody = response.body?.string()
                val apiResponse = gson.fromJson(responseBody, DeleteProduct::class.java)
                val success = apiResponse.success
                val message = apiResponse.message

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && success) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        loadSellingProduct()
                    } else {
                        val errorMsg = apiResponse?.message ?: "Bán thất bại"
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDeleteDialog(productId: Int) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
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

    private fun setupAdapter(list: List<Product>, isSold: Boolean) {
        val adapter = AdapterPostProductManage(list, isSold)
        rcv.adapter = adapter

        adapter.onDeleteClick = { product ->
            showDeleteDialog(product.id)
        }

        adapter.onEditClick = { product ->
            val intent = Intent(requireContext(), EditPostActivity::class.java)
            intent.putExtra("productId", product.id)
            intent.putExtra("title", product.title)
            intent.putExtra("categoryName", product.category?.name)
            intent.putExtra("categoryId", product.category?.id)
            intent.putExtra("price", product.price.toString())
            intent.putExtra("description", product.description)
            intent.putExtra("thumbnailUrl", product.thumbnailUrl)
            startActivity(intent)
        }
        adapter.onSellingClick = { product ->
            lifecycleScope.launch {
                markasSold(product.id)
            }
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

                val response = ApiClient.getClient(requireContext()).newCall(request).execute()
                val responseBody = response.body?.string()

                val apiResponse = gson.fromJson(responseBody, DeleteProduct::class.java)
                val success = apiResponse.success
                val message = apiResponse.message

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && success) {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        loadSellingProduct()
                    } else {
                        val errorMsg = apiResponse?.message ?: "Xóa thất bại"
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}