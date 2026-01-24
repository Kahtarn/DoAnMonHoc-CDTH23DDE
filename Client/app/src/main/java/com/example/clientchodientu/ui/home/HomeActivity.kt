package com.example.clientchodientu.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.AdapterProduct
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.dto.product.ProductResponse
import com.example.clientchodientu.entity.Category
import com.example.clientchodientu.entity.Product
import com.example.clientchodientu.ui.product.AddPost
import com.example.clientchodientu.ui.product.ProductDetailActivity
import com.example.clientchodientu.untils.TokenManager
import kotlinx.coroutines.launch
import kotlin.toString
import com.example.clientchodientu.ui.user.PostManagerActivity
import com.example.clientchodientu.untils.ApiClient
import com.example.exampletemplate.AdapterCategory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Request

class HomeActivity : AppCompatActivity() {
    private val baseUrl = "http://10.0.2.2:8080/api"
    private val urlProductAll = "$baseUrl/product/getAll"
    private val urlCategory = "$baseUrl/category/getCategories"
    private fun getFilterUrl(id: Int) = "$baseUrl/product/getByCategory?categoryId=$id"
    private val gson = Gson()
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var token: String
    private fun getSearchUrl(query: String) = "$baseUrl/product/filter?name=$query"
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        progressBar = findViewById(R.id.progressBar)
        recyclerViewProduct = findViewById(R.id.rvProduct)
        recyclerViewCategory = findViewById(R.id.rvCategory)
        recyclerViewProduct.layoutManager = LinearLayoutManager(this)
        recyclerViewCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        searchView = findViewById(R.id.searchView)

        TokenManager.init(this)
        token = TokenManager.getToken().toString()

        lifecycleScope.launch {
            setupSearchView()
            loadProduct(urlProductAll)
            loadCategories()
        }

        bottomNav = findViewById(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    lifecycleScope.launch {
                        loadProduct(urlProductAll)
                        recyclerViewProduct.smoothScrollToPosition(0)
                    }
                    true
                }

                R.id.nav_save -> {
                    startActivity(Intent(this, PostManagerActivity::class.java))
                    true
                }

                R.id.nav_add -> {
                    startActivity(Intent(this, AddPost::class.java))
                    true
                }

                R.id.nav_chat -> {
                    true
                }

                R.id.nav_account -> {
                    true
                }

                else -> false
            }
        }

    }
    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    lifecycleScope.launch { loadProduct(getSearchUrl(it)) }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    if (!newText.isNullOrBlank()) {
                        loadProduct(getSearchUrl(newText))
                    } else {
                        loadProduct(urlProductAll)
                    }
                }
                return true
            }
        })
    }

    private fun setUpAdapterCategories(list: List<Category>) {
        val adapter = AdapterCategory(list)
        recyclerViewCategory.adapter = adapter

        adapter.onItemClick = { category ->
            lifecycleScope.launch {
                loadProduct(getFilterUrl(category.id))
            }
        }
    }

    suspend fun loadCategories() {
        withContext(Dispatchers.Main) { progressBar.visibility = View.VISIBLE }

        withContext(Dispatchers.IO) {
            try {
                val request = Request
                    .Builder()
                    .url(urlCategory)
                    .build()
                val response = ApiClient.getClient(this@HomeActivity).newCall(request).execute()
                val responseBody = response.body?.string()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseBody != null) {
                        val category = gson.fromJson(responseBody, CategoryResponse::class.java)
                        val success = category.success
                        val message = category.message
                        val listCategory = category.data

                        if (success) {
                            println(message)
                            progressBar.visibility = View.GONE
                            setUpAdapterCategories(listCategory)
                        } else {
                            Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private fun setUpAdapterProduct(list: List<Product>) {
        val adapter = AdapterProduct(list)
        recyclerViewProduct.adapter = adapter

        adapter.onItemClick = { productId ->
            val intent = Intent(this@HomeActivity, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            startActivity(intent)
        }
    }

    suspend fun loadProduct(url : String) {

        withContext(Dispatchers.IO) {
            try {
                val request = Request
                    .Builder()
                    .url(url)
                    .build()
                val response = ApiClient.getClient(this@HomeActivity).newCall(request).execute()
                val responseBody = response.body?.string()


                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && responseBody != null) {
                        val product = gson.fromJson(responseBody, ProductResponse::class.java)
                        val success = product.success
                        val message = product.message
                        val listProduct = product.data

                        if (success) {
                            println(message)
                            setUpAdapterProduct(listProduct)
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@HomeActivity, message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}