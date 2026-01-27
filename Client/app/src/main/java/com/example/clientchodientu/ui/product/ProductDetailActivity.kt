package com.example.clientchodientu.ui.product

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.ProductImageAdapter
import com.example.clientchodientu.dto.product.DetailsData
import com.example.clientchodientu.ui.product.ImageViewerActivity
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.ApiResponseData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
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

        initViews()
        setupEvents()
        loadProductData()
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
        btnFavorite.setOnClickListener { toggleFavorite() }

        btnCallSeller.setOnClickListener {
            sellerPhoneNumber?.let { phone ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                startActivity(intent)
            } ?: showToast("Không có số điện thoại người bán")
        }

        btnChatWithSeller.setOnClickListener { showToast("Tính năng Chat đang phát triển") }
    }

    private fun loadProductData() {
        val url = "$BASE_URL/api/product/details/$currentProductId"
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

        tvTitle.text = product.title
        tvPrice.text = formatCurrency(product.price)
        tvDescription.text = product.description ?: "Không có mô tả"
        tvTimePosted.text = convertTimeAgo(product.createAt)

        if (seller != null) {
            tvSellerName.text = seller.fullName
            tvAddress.text = "${seller.wardName}, ${seller.provinceName}"
            tvSellerPhonePreview.text = "Liên hệ: ${seller.phone}"
            sellerPhoneNumber = seller.phone
        }
        val fullImageUrls = data.images.map { resolveUrl(it) }
        setupImageSlider(fullImageUrls)
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
        rvProductImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
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
            ""
        }
    }

    private fun toggleFavorite() {
        isFavorited = !isFavorited
        val icon = if (isFavorited) R.drawable.favorite_filled else R.drawable.outline_favorite_24
        btnFavorite.setImageResource(icon)
        showToast(if (isFavorited) "Đã thêm vào yêu thích" else "Đã bỏ yêu thích")
    }

    private fun showMoreOptions() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.activity_bottom_sheet_options, null)
        dialog.setContentView(view)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            attributes.gravity = Gravity.BOTTOM // Bottom Sheet nên hiện ở dưới cùng
        }

        view.findViewById<View>(R.id.btnCloseSheet).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}