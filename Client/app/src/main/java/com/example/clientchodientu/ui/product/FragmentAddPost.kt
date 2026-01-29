package com.example.clientchodientu.ui.product

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapters.SelectedImageAdapter
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.entity.Category
import com.example.clientchodientu.ui.home.FragmentHome
import com.example.clientchodientu.untils.token.ApiClient
import com.example.clientchodientu.untils.FileUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody

class FragmentAddPost : Fragment() {
    private val gson = Gson()
    private val urlCate = "https://uncondensable-diplopic-gibson.ngrok-free.dev/api/category/getCategories"
    private val urlPost = "https://uncondensable-diplopic-gibson.ngrok-free.dev/api/product/post"
    private lateinit var imageAdapter: SelectedImageAdapter
    private val selectedUris = mutableListOf<Uri>()
    private lateinit var edtTitle: EditText
    private lateinit var edtPrice: EditText
    private lateinit var edtDescription: EditText
    private lateinit var spnCategory: Spinner
    private lateinit var btnPost: Button
    private lateinit var rvImages: RecyclerView
    private val categoryList = mutableListOf<Category>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupRecyclerView()
        loadCategories()
        btnPost.setOnClickListener {
            lifecycleScope.launch {
                handlePost()
            }
        }

    }

    private fun initViews(view: View) {
        edtTitle = view.findViewById(R.id.edtTitle)
        edtPrice = view.findViewById(R.id.edtPrice)
        edtDescription = view.findViewById(R.id.edtDescription)
        spnCategory = view.findViewById(R.id.spnCategory)
        btnPost = view.findViewById(R.id.btnPost)
        rvImages = view.findViewById(R.id.rvSelectedImages)
        edtPrice.filters = arrayOf(android.text.InputFilter.LengthFilter(10))
    }

    private val pickMultipleMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(6)) { uris ->
            if (uris.isNotEmpty()) {
                selectedUris.addAll(uris)
                if (selectedUris.size > 6) {
                    while (selectedUris.size > 6) selectedUris.removeAt(selectedUris.size - 1)
                    Toast.makeText(requireContext(), "Tối đa 6 ảnh thôi nhé", Toast.LENGTH_SHORT)
                        .show()
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
        rvImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = imageAdapter
    }

    private fun loadCategories() {
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                try {
                    val request = Request.Builder().url(urlCate).build()
                    val response =
                        ApiClient.getClient(requireContext()).newCall(request).execute()
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
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    categories
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnCategory.adapter = adapter
            } else {
                Toast.makeText(requireContext(), "Không thể tải danh mục", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    suspend fun handlePost() {
        val title = edtTitle.text.toString()
        val price = edtPrice.text.toString()
        val description = edtDescription.text.toString()

        if (title.isEmpty() || price.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập tiêu đề và giá", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (selectedUris.isEmpty()) {
            Toast.makeText(requireContext(), "Hãy chọn ít nhất 1 cái ảnh bìa", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if(description.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập mô tả sản phẩm!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val selectedCategory = spnCategory.selectedItem as? Category

        if (selectedCategory == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show()
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
                    val file = FileUtils.getFileFromUri(requireContext(), uri)
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

                val response = ApiClient.getClient(requireContext()).newCall(request).execute()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Đăng tin thành công!", Toast.LENGTH_SHORT)
                            .show()
                        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)

                        bottomNav.selectedItemId = R.id.nav_home
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi hệ thống: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }
}