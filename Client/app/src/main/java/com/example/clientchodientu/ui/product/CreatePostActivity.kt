package com.example.clientchodientu.ui.product

import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapters.SelectedImageAdapter
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.entity.Category
import com.example.clientchodientu.untils.token.ApiClient
import com.example.clientchodientu.untils.FileUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.math.BigDecimal

class CreatePostActivity : AppCompatActivity() {
    private val gson = Gson()
    private val urlCate = "http://10.0.2.2:8080/api/category/getCategories"
    private val urlPost = "http://10.0.2.2:8080/api/product/post"
    private lateinit var imageAdapter: SelectedImageAdapter
    private val selectedUris = mutableListOf<Uri>()
    private lateinit var edtTitle: EditText
    private lateinit var edtPrice: EditText
    private lateinit var edtDescription: EditText
    private lateinit var spnCategory: Spinner
    private lateinit var btnPost: Button
    private lateinit var rvImages: RecyclerView
    private val categoryList = mutableListOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        loadCategories()
        btnPost.setOnClickListener {
            lifecycleScope.launch {
                handlePost()
            }
        }

    }
    private fun initViews() {
        edtTitle = findViewById(R.id.edtTitle)
        edtPrice = findViewById(R.id.edtPrice)
        edtDescription = findViewById(R.id.edtDescription)
        spnCategory = findViewById(R.id.spnCategory)
        btnPost = findViewById(R.id.btnPost)
        rvImages = findViewById(R.id.rvSelectedImages)
    }

    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(6)) { uris ->
        if (uris.isNotEmpty()) {
            selectedUris.addAll(uris)
            if (selectedUris.size > 6) {
                while (selectedUris.size > 6) selectedUris.removeAt(selectedUris.size - 1)
                Toast.makeText(this, "Tối đa 6 ảnh thôi nhé", Toast.LENGTH_SHORT).show()
            }
            imageAdapter.setData(selectedUris)
        }
    }

    private fun setupRecyclerView() {
        imageAdapter = SelectedImageAdapter(
            onAddImageClick = {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onRemoveImageClick = { position ->
                selectedUris.removeAt(position)
                imageAdapter.setData(selectedUris)
            }
        )
        rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = imageAdapter
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(urlCate).build()
                    val response =
                        ApiClient.getClient(this@CreatePostActivity).newCall(request).execute()
                    val responseBody = response.body?.string()

                    if (response.isSuccessful && responseBody != null) {
                        val json = gson.fromJson(responseBody, CategoryResponse::class.java)
                        if (json.success) json.data else null
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
            if (categories != null) {
                val adapter = ArrayAdapter(
                    this@CreatePostActivity,
                    android.R.layout.simple_spinner_item,
                    categories
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnCategory.adapter = adapter
            } else {
                Toast.makeText(this@CreatePostActivity, "Không thể tải danh mục", Toast.LENGTH_SHORT).show()
            }
        }
    }

    suspend fun handlePost() {
        val title = edtTitle.text.toString()
        val price = edtPrice.text.toString()

        if (title.isEmpty() || price.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề và giá", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedUris.isEmpty()) {
            Toast.makeText(this, "Hãy chọn ít nhất 1 cái ảnh bìa", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedCategory = spnCategory.selectedItem as? Category

        if (selectedCategory == null) {
            Toast.makeText(this, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = selectedCategory.id.toString()

        withContext(Dispatchers.IO) {
            try {
                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                builder.addFormDataPart("title", title)
                builder.addFormDataPart("description", edtDescription.text.toString())
                builder.addFormDataPart("price", price)
                builder.addFormDataPart("categoryId", categoryId)

                selectedUris.forEachIndexed { index, uri ->
                    val file = FileUtils.getFileFromUri(this@CreatePostActivity, uri)
                    file?.let {
                        val requestBody = it.asRequestBody("image/jpeg".toMediaType())

                        val keyName = if (index == 0) "thumbnailUrl" else "imageUrl"
                        builder.addFormDataPart(keyName, it.name, requestBody)
                    }
                }

                val request = Request.Builder()
                    .url(urlPost)
                    .post(builder.build())
                    .build()

                // 3. Thực thi gọi API
                val response = ApiClient.getClient(this@CreatePostActivity).newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@CreatePostActivity, "Đăng tin thành công!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@CreatePostActivity, "Lỗi: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CreatePostActivity, "Lỗi hệ thống: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}