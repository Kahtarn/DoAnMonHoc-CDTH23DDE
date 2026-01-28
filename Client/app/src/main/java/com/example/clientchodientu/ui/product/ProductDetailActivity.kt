package com.example.clientchodientu.ui.product

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.ProductImageAdapter
import com.example.clientchodientu.dto.product.DetailsData
import com.example.clientchodientu.dto.product.SetFavoriteRespond
import com.example.clientchodientu.untils.token.ApiClient
import com.example.clientchodientu.untils.ApiResponseData
import com.example.clientchodientu.entity.ProductDetailData
import com.example.clientchodientu.ui.product.ImageViewerActivity
import com.example.clientchodientu.ui.chat.DetailChatActivity
import com.example.clientchodientu.ui.user.PublicDetailsUserActivity

import com.example.clientchodientu.untils.token.ApiResponse
import com.example.clientchodientu.untils.token.TokenManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailActivity : AppCompatActivity() {

    private val BASE_URL = "http://10.0.2.2:8080"

    private lateinit var tvTitle: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvTimePosted: TextView
    private lateinit var tvDescription: TextView
    private lateinit var rvProductImages: RecyclerView
    private lateinit var tvImageCounter: TextView

    private lateinit var layoutSellerInfo: ConstraintLayout
    private lateinit var ivSellerAvatar: ImageView
    private lateinit var tvSellerName: TextView
    private lateinit var tvSellerPhonePreview: TextView

    private lateinit var btnBack: ImageView
    private lateinit var btnFavorite: ImageView
    private lateinit var btnCallSeller: AppCompatButton
    private lateinit var btnChatWithSeller: LinearLayout

    private val gson = Gson()
    private var currentProductId: Int = -1
    private var isFavorited: Boolean = false

    private var sellerId: Int = -1
    private var sellerPhoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        currentProductId = intent.getIntExtra("PRODUCT_ID", -1)
        if (currentProductId == -1) {
            showToast("Lỗi: Không tìm thấy sản phẩm!")
            finish()
            return
        }
        TokenManager.init(this)

        initViews()
        setupEvents()
        loadProductData()
        layoutSellerInfo.setOnClickListener {
            if (sellerId != -1) {
                val nextIntent = Intent(this, PublicDetailsUserActivity::class.java)
                nextIntent.putExtra("sellerId", sellerId)
                startActivity(nextIntent)
            }
        }
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tvProductTitle)
        tvPrice = findViewById(R.id.tvProductPrice)
        tvAddress = findViewById(R.id.tvProductAddress)
        tvTimePosted = findViewById(R.id.tvTimePosted)
        tvDescription = findViewById(R.id.tvProductDescription)
        rvProductImages = findViewById(R.id.rvProductImages)
        tvImageCounter = findViewById(R.id.tvImageCounter)

        layoutSellerInfo = findViewById(R.id.layoutSellerInfo)
        ivSellerAvatar = findViewById(R.id.ivSellerAvatar)
        tvSellerName = findViewById(R.id.tvSellerName)
        tvSellerPhonePreview = findViewById(R.id.tvSellerPhonePreview)

        btnBack = findViewById(R.id.btnBack)
        btnFavorite = findViewById(R.id.btnFavorite)
        btnCallSeller = findViewById(R.id.btnCallSeller)
        btnChatWithSeller = findViewById(R.id.btnChatWithSeller)
    }

    private fun setupEvents() {
        btnBack.setOnClickListener { finish() }
        btnFavorite.setOnClickListener { updateFavoriteStatus() }

        btnCallSeller.setOnClickListener {
            sellerPhoneNumber?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(intent)
            } ?: showToast("Không có số điện thoại người bán")
        }

        btnChatWithSeller.setOnClickListener { handleChatAction() }


    }

    private fun loadProductData() {
        val url = "$BASE_URL/api/product/detail/$currentProductId"
        val request = Request.Builder().url(url).build()

        ApiClient.getClient(this).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { showToast("Lỗi kết nối: ${e.message}") }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread { showToast("Lỗi Server: ${response.code}") }
                    return
                }

                response.body?.string()?.let { json ->
                    try {
                        val type = object : TypeToken<ApiResponseData<DetailsData>>() {}.type
                        val apiResponse = gson.fromJson<ApiResponseData<DetailsData>>(json, type)

                        if (apiResponse.success) {
                            runOnUiThread { bindDataToUI(apiResponse.data) }
                        } else {
                            runOnUiThread { showToast(apiResponse.message) }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { showToast("Lỗi xử lý dữ liệu") }
                    }
                }
            }
        })
    }

    private fun bindDataToUI(data: DetailsData) {
        val product = data.product
        val seller = product.seller
        sellerId = seller.id

        tvTitle.text = product.title
        tvPrice.text = formatCurrency(product.price)
        tvDescription.text = product.description ?: "Không có mô tả"
        tvTimePosted.text = convertTimeAgo(product.createAt)
        setupFavorite()
        if (seller != null) {
            tvSellerName.text = seller.fullName
            tvAddress.text = "${seller.wardName}, ${seller.provinceName}"
            tvSellerPhonePreview.text = "Liên hệ: ${seller.phone}"
            sellerPhoneNumber = seller.phone
        }
        val fullImageUrls = data.images.map { resolveUrl(it) }
        setupImageSlider(fullImageUrls)
    }

    private fun handleChatAction() {
//        Toast.makeText(this, "Đang mở chat...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailChatActivity::class.java)
        intent.putExtra("PRODUCT_ID", currentProductId)
        intent.putExtra("PARTNER_ID", sellerId)
        intent.putExtra("PARTNER_NAME", tvSellerName.text.toString())
        intent.putExtra("IS_FIRST_TIME_CHAT", true)

        // Tính toán RoomName ngay tại đây để bên kia có cái dùng luôn

        lifecycleScope.launch {
            val myId =
                TokenManager.getUserId(
                    this@ProductDetailActivity
                )
            val ids = listOf(myId.toString(), sellerId.toString()).sorted()
            val calculatedRoomName = "chat_user_${ids[0]}_user_${ids[1]}"
            intent.putExtra("MY_ID", myId)
            intent.putExtra("ROOM_NAME", calculatedRoomName)

            withContext(Dispatchers.Main) {
                Log.d("ProductD_MyId", myId.toString())
                startActivity(intent)
            }
        }
    }


    private fun setupImageSlider(imageUrls: List<String>) {
        if (imageUrls.isEmpty()) {
            tvImageCounter.text = "0 / 0"
            return
        }
        val adapter = ProductImageAdapter(imageUrls) { position ->
            val intent = Intent(this, ImageViewerActivity::class.java)
            intent.putStringArrayListExtra("IMAGES", ArrayList(imageUrls))
            intent.putExtra("POSITION", position)
            startActivity(intent)
        }

        rvProductImages.adapter = adapter
        rvProductImages.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvProductImages.onFlingListener = null
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rvProductImages)

        // Update Counter (1/5)
        rvProductImages.clearOnScrollListeners()
        rvProductImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = rvProductImages.layoutManager as LinearLayoutManager
                    val pos = layoutManager.findFirstVisibleItemPosition()
                    if (pos != RecyclerView.NO_POSITION) {
                        tvImageCounter.text = "${pos + 1} / ${imageUrls.size}"
                    }
                }
            }
        })
        tvImageCounter.text = "1 / ${imageUrls.size}"
    }

    private fun setupFavorite() {
        // Giả sử có một API để kiểm tra trạng thái yêu thích
        val url = "$BASE_URL/api/product/favorite-status/$currentProductId"
        val request =
            Request.Builder()
                .url(url)
                .get()
                .build()

        ApiClient.getClient(this).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { showToast("Lỗi kết nối: ${e.message}") }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread { showToast("Lỗi Server: ${response.code}") }
                    return
                }
                try {
                    val json = response.body?.string()
                    val type = object : TypeToken<ApiResponseData<Boolean>>() {}.type
                    val apiResponse = gson.fromJson<ApiResponseData<Boolean>>(json, type)

                    if (apiResponse.success) {
                        isFavorited = apiResponse.data
                        runOnUiThread {
                            val icon =
                                if (isFavorited) R.drawable.ic_favorite_filled else R.drawable.outline_favorite_24
                            btnFavorite.setImageResource(icon)
                            Log.d("ProductDetail", "Favorite status:$currentProductId $isFavorited")
                        }
                    } else {
                        runOnUiThread { showToast(apiResponse.message) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { showToast("Lỗi xử lý dữ liệu") }
                }

            }
        })
    }

    private fun updateFavoriteStatus() {
        val url = "$BASE_URL/api/product/set-favorite/$currentProductId"
        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody("application/json".toMediaType()))
            .build()

        ApiClient.getClient(this).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread { showToast("Lỗi kết nối: ${e.message}") }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    runOnUiThread {
                        showToast("Lỗi Server: ${response.code}")
                    }
                    return
                }
                try {
                    val json = response.body?.string()
                    val type = object : TypeToken<ApiResponseData<SetFavoriteRespond>>() {}.type
                    val apiResponse = gson.fromJson<ApiResponseData<SetFavoriteRespond>>(json, type)

                    if (apiResponse.success) {
                        isFavorited = apiResponse.data.isFavorite
                        runOnUiThread {
                            val icon =
                                if (isFavorited) R.drawable.ic_favorite_filled else R.drawable.outline_favorite_24
                            btnFavorite.setImageResource(icon)
                            Log.d("ProductDetail", "Favorite status:$currentProductId $isFavorited")
                        }
                    } else {
                        runOnUiThread {
                            showToast(apiResponse.message)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread { showToast("Lỗi xử lý dữ liệu") }
                }

            }
        })
    }

    // --- HELPER FUNCTIONS ---

    // Hàm nối chuỗi URL thông minh
    private fun resolveUrl(path: String?): String {
        if (path.isNullOrEmpty()) return ""
        return if (path.startsWith("http")) path else BASE_URL + path
    }

    private fun formatCurrency(price: BigDecimal?): String {
        return price?.let { DecimalFormat("#,###").format(it) + " đ" } ?: "0 đ"
    }

    private fun convertTimeAgo(timeString: String?): String {
        if (timeString.isNullOrEmpty()) return ""
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC") // Server trả về giờ UTC

        return try {
            val past = format.parse(timeString) ?: return ""
            val now = Date()
            val diff = now.time - past.time
            val minute = 60 * 1000L
            val hour = 60 * minute
            val day = 24 * hour

            when {
                diff < minute -> "Vừa xong"
                diff < hour -> "${diff / minute} phút trước"
                diff < day -> "${diff / hour} giờ trước"
                diff < 7 * day -> "${diff / day} ngày trước"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(past)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Lỗi xử lý thời gian"
        }
    }


    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}