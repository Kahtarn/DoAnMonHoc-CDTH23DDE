package com.example.clientchodientu.ui.edit

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.dto.product.EditPostRequest
import com.example.clientchodientu.dto.product.EditPostResponse
import com.example.clientchodientu.entity.Category
import com.example.clientchodientu.untils.token.ApiResponse
import com.example.clientchodientu.untils.token.ApiClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
class EditPostActivity : AppCompatActivity() {
    // UI components
    private lateinit var edtTitle: EditText
    private lateinit var spnCategory: Spinner
    private lateinit var edtPrice: EditText
    private lateinit var edtDescription: EditText
    private lateinit var btnBack : ImageView
    private lateinit var btnCancel: Button
    private val gson = Gson()
    private var productId: Int = -1
    private var intentCategoryId: Int = -1
    private val BASE_URL = "http://10.0.2.2:8080/api"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_post)
        initViews()
        receiveData()
        loadCategories()

        btnCancel= findViewById(R.id.btnCancel)
        btnCancel.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Hủy chỉnh sửa?")
                .setMessage("Dữ liệu đã nhập sẽ bị mất.")
                .setPositiveButton("Thoát") { _, _ ->
                    finish()
                }
                .setNegativeButton("Ở lại", null)
                .show()

        }
        btnBack.setOnClickListener {finish()}
        findViewById<Button>(R.id.btnUpdate).setOnClickListener { validateAndSave() }
    }

    private fun initViews() {
        edtTitle = findViewById(R.id.edtTitle)
        edtPrice = findViewById(R.id.edtPrice)
        edtDescription = findViewById(R.id.edtDescription)
        spnCategory = findViewById(R.id.spnCategory)
        btnBack = findViewById(R.id.ivBack)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun receiveData() {
        productId = intent.getIntExtra("productId", -1)
        intentCategoryId = intent.getIntExtra("categoryId", -1)

        edtTitle.setText(intent.getStringExtra("title"))
        edtPrice.setText(intent.getStringExtra("price"))
        edtDescription.setText(intent.getStringExtra("description"))
    }

    private fun loadCategories() = lifecycleScope.launch {
        val categories = withContext(Dispatchers.IO) {
            runCatching {
                val request = Request.Builder().url("$BASE_URL/category/getCategories").build()
                val response = ApiClient.getClient(this@EditPostActivity).newCall(request).execute()
                val body = response.body?.string()
                gson.fromJson(body, CategoryResponse::class.java).data
            }.getOrNull()
        }

        categories?.let { list ->
            val adapter = ArrayAdapter(this@EditPostActivity, android.R.layout.simple_spinner_item, list)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCategory.adapter = adapter

            val pos = list.indexOfFirst { it.id == intentCategoryId }
            if (pos != -1) spnCategory.setSelection(pos)
        } ?: showToast("Không thể tải danh mục")
    }

    private fun validateAndSave() {
        val title = edtTitle.text.toString().trim()
        val priceStr = edtPrice.text.toString().trim()
        val desc = edtDescription.text.toString().trim()
        val categoryId = (spnCategory.selectedItem as? Category)?.id ?: return

        if (title.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin")
            return
        }

        val price = priceStr.toBigDecimalOrNull() ?: run {
            showToast("Giá tiền không hợp lệ")
            return
        }

        updateProduct(EditPostRequest(title, price, desc, categoryId))
    }

    private fun updateProduct(requestObj: EditPostRequest) = lifecycleScope.launch {
        val isSuccess = withContext(Dispatchers.IO) {
            runCatching {
                val body = RequestBody.create("application/json".toMediaTypeOrNull(), gson.toJson(requestObj))
                val request = Request.Builder().url("$BASE_URL/product/update/$productId").put(body).build()
                val response = ApiClient.getClient(this@EditPostActivity).newCall(request).execute()
                gson.fromJson(response.body?.string(), EditPostResponse::class.java).success
            }.getOrDefault(false)
        }

        if (isSuccess) {
            showToast("Cập nhật thành công!")
            finish()
        } else {
            showToast("Cập nhật thất bại!")
        }
    }

    private fun showToast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}