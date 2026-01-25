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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.adapter.ProductImageAdapter
import com.example.clientchodientu.entity.ProductDetailData
import com.example.clientchodientu.ui.auth.ImageViewerActivity
import com.example.clientchodientu.ui.chat.DetailChatActivity
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.ApiResponse
import com.example.clientchodientu.untils.TokenManager
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class ProductDetailActivity : AppCompatActivity() {
    // 1. KHAI BÁO BIẾN (VARIABLES)
    //Thông tin sản phẩm
    private lateinit var tvTitle: TextView

    private lateinit var imageAdapter: ProductImageAdapter
    private lateinit var tvSubtitle: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvTimePosted: TextView
    private lateinit var tvDescription: TextView
    private lateinit var rvProductImages: RecyclerView
    private lateinit var tvImageCounter: TextView

    //Thông tin người bán (Seller)
    private lateinit var layoutSellerInfo: ConstraintLayout
    private lateinit var ivSellerAvatar: ImageView
    private lateinit var ivSellerBadge: ImageView
    private lateinit var tvSellerName: TextView
    private lateinit var tvRatingCount: TextView
    private lateinit var tvSellerPhonePreview: TextView

    //Tương tác (Action)
    private lateinit var btnBack: ImageView
    private lateinit var btnShare: ImageView
    private lateinit var btnMoreOptions: ImageView
    private lateinit var btnFavorite: ImageView
    private lateinit var btnChatWithSeller: LinearLayout
    private lateinit var btnCallSeller: AppCompatButton

    //Bình luận (Comment)
    private lateinit var layoutListComment: LinearLayout
    private lateinit var layoutEmptyComment: LinearLayout
    private lateinit var edtCommentInput: EditText
    private lateinit var btnSendComment: ImageView

    // Data Variables
    private val client = OkHttpClient()
    private val gson = Gson()
    private var currentProductId: Int = -1
    private var sellerId: Int = -1
    private var isFavorited: Boolean = false

    // 2. LIFECYCLE (VÒNG ĐỜI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        getIntentData()

        initViews()

        setupEvents()

        loadProductData()
    }

    // 3. KHỞI TẠO VIEW & DATA (SETUP)

    private fun getIntentData() {
        // Lấy ID sản phẩm truyền từ màn hình trước
//        currentProductId = 5//intent.getIntExtra("PRODUCT_ID", -1)
//        Toast.makeText(this, "Đang chạy chế độ TEST: Product ID = 1", Toast.LENGTH_LONG).show()
//        if (currentProductId == -1) {
//            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show()
//            finish()
//        }
        // 1. Lấy ID sản phẩm từ Intent (do HomeActivity gửi sang)
        // Số -1 là giá trị mặc định nếu không nhận được dữ liệu
        currentProductId = intent.getIntExtra("PRODUCT_ID", -1)

        // 2. Kiểm tra xem có nhận được ID hợp lệ không
        if (currentProductId == -1) {
            // Nếu lỗi (không có ID), báo lỗi và đóng màn hình này lại
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            // (Tuỳ chọn) Hiện thông báo để kiểm tra xem nhận đúng ID chưa
            // Toast.makeText(this, "Đang xem sản phẩm ID: $currentProductId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        rvProductImages = findViewById(R.id.rvProductImages)
        tvImageCounter = findViewById(R.id.tvImageCounter)
        btnBack = findViewById(R.id.btnBack)
        btnShare = findViewById(R.id.btnShare)
        btnMoreOptions = findViewById(R.id.btnMoreOptions)
        tvTitle = findViewById(R.id.tvProductTitle)
        tvPrice = findViewById(R.id.tvProductPrice)
        tvAddress = findViewById(R.id.tvProductAddress)
        tvTimePosted = findViewById(R.id.tvTimePosted)
        tvDescription = findViewById(R.id.tvProductDescription)
        btnFavorite = findViewById(R.id.btnFavorite)
        layoutSellerInfo = findViewById(R.id.layoutSellerInfo)
        ivSellerAvatar = findViewById(R.id.ivSellerAvatar)
        ivSellerBadge = findViewById(R.id.ivSellerBadge)
        tvSellerName = findViewById(R.id.tvSellerName)
        tvRatingCount = findViewById(R.id.tvRatingCount)
        tvSellerPhonePreview = findViewById(R.id.tvSellerPhonePreview)
        layoutListComment = findViewById(R.id.layoutListComment)
        layoutEmptyComment = findViewById(R.id.layoutEmptyComment)
        edtCommentInput = findViewById(R.id.edtCommentInput)
        btnSendComment = findViewById(R.id.btnSendComment)
        btnCallSeller = findViewById(R.id.btnCallSeller)
        btnChatWithSeller = findViewById(R.id.btnChatWithSeller)
    }

    // 4. XỬ LÝ SỰ KIỆN (EVENTS)

    private fun setupEvents() {
        btnBack.setOnClickListener { finish() }

        btnFavorite.setOnClickListener { toggleFavorite() }

        btnMoreOptions.setOnClickListener { showMoreOptions() }

        btnCallSeller.setOnClickListener {
            if (sellerId != -1) {
                val phoneNumber = "0987654321"
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:$phoneNumber")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin người bán", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnChatWithSeller.setOnClickListener { handleChatAction() }

        layoutSellerInfo.setOnClickListener {
            Toast.makeText(this, "Xem User ID: $sellerId", Toast.LENGTH_SHORT).show()
        }

        btnSendComment.setOnClickListener {
            val content = edtCommentInput.text.toString().trim()
            if (content.isNotEmpty()) {
                Toast.makeText(this, "Đã gửi: $content", Toast.LENGTH_SHORT).show()
                edtCommentInput.setText("")
            }
        }
    }

    // 5. LOGIC NGHIỆP VỤ (BUSINESS LOGIC)

    private fun loadProductData() {
        val ipAddress = "10.0.2.2"
        val port = "8080"
        val url = "http://$ipAddress:$port/api/product/detail/$currentProductId"
        val request = Request.Builder()
            .url(url)
            .build()

        ApiClient.getClient(this).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Lỗi kết nối: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    if (jsonResponse != null) {
                        try {
                            val apiResponse = gson.fromJson(jsonResponse, ApiResponse::class.java)

                            val data = apiResponse.data

                            runOnUiThread {
                                updateUI(data)
                            }
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@ProductDetailActivity,
                                    "Lỗi đọc dữ liệu: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "Lỗi Server: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun updateUI(data: ProductDetailData) {
        val productObj = data.product
        val sellerObj = productObj.seller
        tvTitle.text = productObj.title
        tvPrice.text = formatCurrency(productObj.price)
        tvDescription.text = productObj.description ?: ""
        tvTimePosted.text = convertTimeAgo(productObj.createAt)
        tvSellerName.text = sellerObj.fullName
        tvAddress.text = "Khu vực: ${sellerObj.wardName}, ${sellerObj.provinceName}"
        tvSellerPhonePreview.text = "SĐT liên hệ: ${sellerObj.phone}"
//        com.bumptech.glide.Glide.with(this)
//            .load(sellerObj.thumbnailUrl)
//            .placeholder(R.drawable.ic_launcher_foreground)
//            .into(ivSellerAvatar)
        setupImageSlider(data.images)
        // khanh
        this.sellerId = sellerObj.id
    }


    private fun handleChatAction() {
//        Toast.makeText(this, "Đang mở chat...", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DetailChatActivity::class.java)
        intent.putExtra("PRODUCT_ID", currentProductId)
        intent.putExtra("PARTNER_ID", sellerId)
        intent.putExtra("PARTNER_NAME", tvSellerName.text.toString())
        intent.putExtra("IS_FIRST_TIME_CHAT", true)

        // Tính toán RoomName ngay tại đây để bên kia có cái dùng luôn
        val myIdInt =
            TokenManager.getUserId(
                this,
                TokenManager.getToken().toString()
            ) // Giả sử Boss có hàm lấy ID của mình
        val ids = listOf(myIdInt.toString(), sellerId.toString()).sorted()
        val calculatedRoomName = "chat_user_${ids[0]}_user_${ids[1]}"
        intent.putExtra("ROOM_NAME", calculatedRoomName)
        startActivity(intent)
    }


    private fun toggleFavorite() {
        isFavorited = !isFavorited
        if (isFavorited) {
            btnFavorite.setImageResource(R.drawable.favorite_filled)
            Toast.makeText(this, "Đã yêu thích", Toast.LENGTH_SHORT).show()
        } else {
            btnFavorite.setImageResource(R.drawable.outline_favorite_24)
            Toast.makeText(this, "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupImageSlider(images: List<String>) {
        if (images.isEmpty()) {
            tvImageCounter.text = "0 / 0"
            return
        }
        val adapter = ProductImageAdapter(images) { position ->
            val intent = Intent(this, ImageViewerActivity::class.java)
            intent.putStringArrayListExtra("IMAGES", ArrayList(images)) // Truyền list ảnh
            intent.putExtra("POSITION", position) // Truyền vị trí ảnh đang xem
            startActivity(intent)
        }
        rvProductImages.adapter = adapter
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvProductImages.layoutManager = layoutManager
        val snapHelper = PagerSnapHelper()
        rvProductImages.onFlingListener = null
        snapHelper.attachToRecyclerView(rvProductImages)
        rvProductImages.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(layoutManager)
                    if (centerView != null) {
                        val pos = layoutManager.getPosition(centerView)
                        tvImageCounter.text = "${pos + 1} / ${images.size}"
                    }
                }
            }
        })
        tvImageCounter.text = "1 / ${images.size}"
    }

    fun formatCurrency(price: BigDecimal?): String {
        if (price == null) return "0 đ"
        try {
            val formatter = DecimalFormat("#,###")
            return "${formatter.format(price)} đ"
        } catch (e: Exception) {
            return "0 đ"
        }
    }

    private fun convertTimeAgo(timeString: String?): String {
        if (timeString == null) return "Lỗi: Không có thời gian"

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

        try {
            val past = format.parse(timeString) ?: return "Lỗi: Định dạng sai"
            val now = Date()
            format.timeZone = TimeZone.getTimeZone("UTC")
            if (past.time > now.time) {
                return "Lỗi dữ liệu (Thời gian ở tương lai)"
            }
            val diff = now.time - past.time
            val second = 1000L
            val minute = 60 * second
            val hour = 60 * minute
            val day = 24 * hour
            return when {
                diff < minute -> "Vừa xong"
                diff < hour -> "${diff / minute} phút trước"
                diff < day -> "${diff / hour} giờ trước"
                diff < 7 * day -> "${diff / day} ngày trước"
                else -> {
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy")
                    outputFormat.format(past)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Lỗi xử lý thời gian"
        }
    }

    private fun showMoreOptions() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.activity_bottom_sheet_options, null)
        dialog.setContentView(view)
        val window = dialog.window
        if (window != null) {
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val layoutParams = window.attributes
            layoutParams.gravity = Gravity.CENTER
            layoutParams.width = (resources.displayMetrics.widthPixels * 0.90).toInt()
            window.attributes = layoutParams
        }
        val btnClose = view.findViewById<View>(R.id.btnCloseSheet)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}