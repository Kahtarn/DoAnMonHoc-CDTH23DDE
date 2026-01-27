package com.example.clientchodientu.ui.edit

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.product.CategoryResponse
//import com.example.clientchodientu.dto.product.PostRequest
//import com.example.clientchodientu.dto.product.PostResonse
import com.example.clientchodientu.entity.Category
import com.example.clientchodientu.untils.ApiClient
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.math.BigDecimal

class EditProduct : AppCompatActivity() {
    private lateinit var Tvtitle: TextInputEditText
    private lateinit var Actvcategory: AutoCompleteTextView
    private lateinit var Tvprice: TextInputEditText
    private lateinit var Tvdescription: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnHuy: Button
    private var selectedCategoryId: Int = -1
    private val gson = Gson()
    private var listCategoryFull = mutableListOf<Category>()
    private val urlCategory = "http://10.0.2.2:8080/api/category/getCategories"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_product)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        Tvtitle = findViewById(R.id.TIET_Title)
        Actvcategory = findViewById(R.id.actv_category)
        Tvprice = findViewById(R.id.TIET_price)
        Tvdescription = findViewById(R.id.TIET_des)
        btnSave = findViewById(R.id.btnSave)

        val titleOld = intent.getStringExtra("title")
        val categoryold = intent.getStringExtra("category")
        val priceOld = intent.getStringExtra("price")
        val descOld = intent.getStringExtra("description")

        Tvtitle.setText(titleOld)
        Tvprice.setText(priceOld.toString())
        Tvdescription.setText(descOld)
        Actvcategory.setText(categoryold)
        lifecycleScope.launch {
            loadCategory()
        }
        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            lifecycleScope.launch {
//                UpdatePostProduct()
            }
        }
        btnHuy = findViewById(R.id.btnCancel)
        btnHuy.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn hủy mọi thay đổi không?")
                .setPositiveButton("Đồng ý") { _, _ ->
                    finish()
                }
                .setNegativeButton("Không", null)
                .show()
        }
    }

    suspend fun loadCategory() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request
                    .Builder()
                    .url(urlCategory)
                    .build()
                val response = ApiClient.getClient(this@EditProduct).newCall(request).execute()
                val responseBody = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseBody != null) {
                        val categoryResponse =
                            gson.fromJson(responseBody, CategoryResponse::class.java)
                        val listFromApi = categoryResponse.data ?: emptyList()

                        listCategoryFull.clear()
                        listCategoryFull.addAll(listFromApi)

                        val names = listFromApi.map { it.name ?: "N/A" }

                        val adapter = android.widget.ArrayAdapter(
                            this@EditProduct,
                            android.R.layout.simple_dropdown_item_1line,
                            names
                        )
                        Actvcategory.setAdapter(adapter)
                    }
                }
            }catch (e: Exception) {
                println(e.message)
            }
        }
    }
//    suspend fun UpdatePostProduct(){
//        withContext(Dispatchers.IO) {
//            try {
//                val title = Tvtitle.text.toString()
//                val priceStr = Tvprice.text.toString()
//                val description = Tvdescription.text.toString()
//
//                if (selectedCategoryId == -1) {
//                    val currentCategoryName = Actvcategory.text.toString()
//                    selectedCategoryId = listCategoryFull.find { it.name == currentCategoryName }?.id ?: -1
//                }
//
//                val updateData = PostRequest(
//                    title = title,
//                    categoryId = selectedCategoryId,
//                    price = priceStr.toBigDecimalOrNull() ?: BigDecimal.ZERO,
//                    description = description
//                )
//
//                val jsonBody = gson.toJson(updateData)
//                val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
//
//                val productId = intent.getIntExtra("id", -1)
//                val updateUrl = "http://10.0.2.2:8080/api/product/update/$productId"
//
//                val request = Request.Builder()
//                    .url(updateUrl)
//                    .put(requestBody)
//                    .build()
//
//                val response = ApiClient.getClient(this@EditProduct).newCall(request).execute()
//                val responseBody = response.body?.string()
//
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccessful && responseBody != null) {
//                        val apiResponse = gson.fromJson(responseBody, PostResonse::class.java)
//                        if (apiResponse.success) {
//                            Toast.makeText(this@EditProduct, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
//                            finish()
//                        } else {
//                            Toast.makeText(this@EditProduct, apiResponse.message, Toast.LENGTH_LONG).show()
//                        }
//                    } else {
//                    }
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this@EditProduct, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
}

