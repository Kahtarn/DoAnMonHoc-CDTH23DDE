package com.example.clientchodientu.ui.product

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.CategoryAdapterAutoCompleteTextView
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.dto.product.PostResponse
import com.example.clientchodientu.dto.product.PostResquest
import com.example.clientchodientu.entity.Category
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.TokenManager
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AddPost : AppCompatActivity() {
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var AddTitle : EditText
    private lateinit var AddPrice: EditText
    private lateinit var AddDescription: EditText
    private var selectedCategoryId:Int=-1
    private lateinit var btnXN: Button
    private var selectedImageUris = mutableListOf<android.net.Uri>()

    // Khai báo launcher để chọn tối đa 5 ảnh (bạn có thể thay đổi số lượng)
    private val pickMultipleImageLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            selectedImageUris.addAll(uris) // Lưu tất cả Uri vào danh sách để dùng khi Post

            Log.d("PhotoPicker", "Danh sách ảnh: $selectedImageUris")

            val imgPreview = findViewById<ImageView>(R.id.imgPreview)
            val layoutDefault = findViewById<LinearLayout>(R.id.Linearid3)

            imgPreview.setImageURI(uris[0]) // Hiển thị ảnh đầu tiên làm đại diện
            imgPreview.visibility = View.VISIBLE
            layoutDefault.visibility = View.GONE
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_add_post)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        AddTitle = findViewById(R.id.edtProductName)
        AddPrice = findViewById(R.id.edtProductPrice)
        AddDescription = findViewById(R.id.edtProductDescription)
        btnXN = findViewById(R.id.btnAccess)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        TokenManager.init(this)
        Log.d("GetToken", TokenManager.getToken().toString())
        val linearUpload = findViewById<LinearLayout>(R.id.Linearid3)
        linearUpload.setOnClickListener {
            Log.d("PhotoPicker", "Đã nhấn vào vùng thêm ảnh")
            pickMultipleImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        lifecycleScope.launch {
            getCategory()
        }
        btnXN.setOnClickListener {
            lifecycleScope.launch {
                postNewProduct()
            }
        }

    }
    private suspend fun postNewProduct() {
        withContext(Dispatchers.IO) {
            try {
                val title = AddTitle.text.toString().trim()
                val description = AddDescription.text.toString().trim()
                val priceStr = AddPrice.text.toString().trim()

                // Kiểm tra dữ liệu đầu vào để tránh lỗi logic
                if (title.isEmpty() || priceStr.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddPost, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    }
                    return@withContext
                }

                val priceValue = priceStr.toBigDecimalOrNull() ?: java.math.BigDecimal.ZERO
                val base64Images = urisToBase64List(selectedImageUris)
                val postData = PostResquest(
                    sellerId = TokenManager.getUserId(),
                    categoryId = selectedCategoryId,
                    title = title,
                    description = description,
                    price = priceValue,
                    thumbnailUrl = base64Images.getOrNull(0) ?: "",
                    imageUrl = base64Images
                )

                val gson = Gson()
                val jsonString = gson.toJson(postData)
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = jsonString.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/product/post") // Đảm bảo Server đã fix lỗi "/"
                    .post(body)
                    .addHeader("Authorization", "Bearer ${TokenManager.getToken()}")
                    .build()

                val response = ApiClient.getClient(this@AddPost).newCall(request).execute()

                response.use { resp -> // Sử dụng .use để tự động đóng response body
                    val responseBody = resp.body?.string()

                    if (resp.isSuccessful && responseBody != null) {
                        val jsonResponse = gson.fromJson(responseBody, PostResponse::class.java)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddPost, jsonResponse.message, Toast.LENGTH_LONG).show()
                            if (jsonResponse.success) {
                                // finish() hoặc chuyển trang tại đây
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.e("API_ERROR", "Code: ${resp.code} - Body: $responseBody")
                            Toast.makeText(this@AddPost, "Lỗi server: ${resp.code}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Error_conn", "Lỗi hệ thống: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddPost, "Không thể kết nối đến máy chủ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private suspend fun getCategory() {
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val gson = Gson()
                val url = "http://10.0.2.2:8080/api/category/getCategories"
                val request = Request.Builder().url(url).get().build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val responseCategory = gson.fromJson(body, CategoryResponse::class.java)
                    val data: List<Category> = responseCategory.data
                    Log.d("ItemCategory",data.toString())

                    withContext(Dispatchers.Main) {
                        val adapter = CategoryAdapterAutoCompleteTextView(this@AddPost, data)
                        autoCompleteTextView.setAdapter(adapter)

                        // THAY ĐỔI 1: Đặt threshold bằng 0 để nó cho phép hiện dropdown khi ô trống
                        autoCompleteTextView.threshold = 0

                        // THAY ĐỔI 2: Dùng setOnTouchListener thay vì OnClickListener
                        // OnClickListener đôi khi không chạy ở lần chạm đầu tiên do vướng Focus
                        autoCompleteTextView.setOnTouchListener { v, event ->
                            if (event.action == android.view.MotionEvent.ACTION_UP) {
                                autoCompleteTextView.showDropDown()
                            }
                            false
                        }

                        autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
                            val selectedCategory = parent.getItemAtPosition(position) as Category
                            selectedCategoryId = selectedCategory.id
                            // THAY ĐỔI 3: filter = false để sau khi chọn, danh sách không bị ẩn đi
                            autoCompleteTextView.setText(selectedCategory.name, false)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi: ${e.message}")
            }
        }
    }
    private fun urisToBase64List(uris: List<android.net.Uri>): List<String> {
        return uris.mapNotNull { uri ->
            try {
                contentResolver.openInputStream(uri).use { inputStream ->
                    val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream) ?: return@mapNotNull null

                    // Thu nhỏ kích thước để giảm dung lượng JSON
                    val maxSize = 1024
                    val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
                    val width = if (ratio > 1) maxSize else (maxSize * ratio).toInt()
                    val height = if (ratio > 1) (maxSize / ratio).toInt() else maxSize

                    val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, width, height, true)
                    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
                    scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)

                    val bytes = byteArrayOutputStream.toByteArray()

                    // Giải phóng RAM ngay lập tức
                    originalBitmap.recycle()
                    scaledBitmap.recycle()

                    android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
                }
            } catch (e: Exception) {
                Log.e("Base64Error", "Lỗi xử lý ảnh: ${e.message}")
                null
            }
        }
    }
}