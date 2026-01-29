package com.example.clientchodientu.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.AdapterProduct
import com.example.clientchodientu.dto.product.CategoryResponse
import com.example.clientchodientu.dto.product.ProductResponse
import com.example.clientchodientu.entity.Category
import com.example.clientchodientu.entity.Product
import com.example.clientchodientu.ui.auth.ProfileUser
import com.example.clientchodientu.ui.favorite.FavoriteActivity
import com.example.clientchodientu.ui.product.ProductDetailActivity
import com.example.clientchodientu.untils.token.TokenManager
import kotlinx.coroutines.launch
import kotlin.toString
//import com.example.clientchodientu.ui.user.PostManagerActivity
import com.example.clientchodientu.untils.token.ApiClient
import com.example.exampletemplate.AdapterCategory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

class FragmentHome : Fragment(){
    private val baseUrl = "https://uncondensable-diplopic-gibson.ngrok-free.dev/api"
    private val urlProductAll = "$baseUrl/product/getAll"
    private val urlCategory = "$baseUrl/category/getCategories"
    private fun getFilterUrl(id: Int) = "$baseUrl/product/getByCategory?categoryId=$id"
    private val gson = Gson()
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewProduct: RecyclerView
    private lateinit var recyclerViewCategory: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnFavorite : ImageButton

    private lateinit var token: String
    private fun getSearchUrl(query: String) = "$baseUrl/product/filter?name=$query"
    private var searchJob: Job? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnFavorite = view.findViewById(R.id.btnFavorite)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerViewProduct = view.findViewById(R.id.rvProduct)
        recyclerViewCategory = view.findViewById(R.id.rvCategory)
        recyclerViewProduct.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        searchView = view.findViewById(R.id.searchView)

        TokenManager.init(requireContext())
        token = TokenManager.getToken().toString()

        lifecycleScope.launch {
            setupSearchView()
            loadProduct(urlProductAll)
            loadCategories()
        }

        btnFavorite.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
        }

        Log.d("TOKEN_HOME", token)

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
                val response = ApiClient.getClient(requireContext()).newCall(request).execute()
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
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT_ID", productId)
            startActivity(intent)
        }
    }

    suspend fun loadProduct(url: String) {

        withContext(Dispatchers.IO) {
            try {
                val request = Request
                    .Builder()
                    .url(url)
                    .build()
                val response = ApiClient.getClient(requireContext()).newCall(request).execute()
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
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT)
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