package com.example.clientchodientu.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.entity.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class AdapterProduct(private var ListProduct: List<Product>) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {
    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int = ListProduct.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = ListProduct[position]
        holder.title.text = product.title
        val province = product.seller?.provinceName ?: "N/A"
        val ward = product.seller?.wardName ?: ""
        holder.position.text = if (ward.isNotEmpty()) "$province\n$ward" else province
        holder.price.text = "${product.price} VNĐ"
        holder.tvSellerName.text = product.seller?.fullName ?: "Người bán ẩn danh"
        holder.tvCreateAt.text = convertTimeAgo(product.createAt)

        val BASE_URL = "https://uncondensable-diplopic-gibson.ngrok-free.dev"
        val thumbnailUrl = BASE_URL + product.thumbnailUrl

        Glide.with(holder.itemView.context)
            .load(thumbnailUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product.id)
        }
    }

    private fun convertTimeAgo(timeString: String?): String {
        if (timeString.isNullOrBlank()) return "Không rõ thời gian"

        // Thử parse với nhiều định dạng để tránh lỗi
        val patterns = arrayOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss"
        )

        var past: Date? = null

        for (pattern in patterns) {
            try {
                val format = SimpleDateFormat(pattern, Locale.getDefault())
                // Nếu chuỗi có chữ Z, ta ép nó hiểu là UTC. Nếu không, để mặc định Local.
                if (timeString.endsWith("Z")) {
                    format.timeZone = TimeZone.getTimeZone("UTC")
                } else {
                    format.timeZone = TimeZone.getDefault()
                }
                past = format.parse(timeString)
                if (past != null) break
            } catch (e: Exception) { continue }
        }

        if (past == null) return "Lỗi định dạng"

        val now = Date()
        val diffMillis = now.time - past.time

        // LOG DEBUG: Boss mở Logcat xem 2 con số này có khớp nhau không
        Log.d("TIME_CHECK", "Giờ máy: ${now.time} | Giờ Server: ${past.time} | Lệch: ${diffMillis/1000} giây")

        // Nếu lệch quá lớn (ví dụ lệch 7 tiếng = 25.200.000 ms)
        // thì khả năng cao vẫn sai múi giờ, ta dùng Math.abs để chữa cháy
        val absDiff = Math.abs(diffMillis)

        if (absDiff < 60000) return "Vừa xong"

        val minutes = TimeUnit.MILLISECONDS.toMinutes(absDiff)
        val hours = TimeUnit.MILLISECONDS.toHours(absDiff)
        val days = TimeUnit.MILLISECONDS.toDays(absDiff)

        return when {
            minutes < 60 -> "$minutes phút trước"
            hours < 24 -> "$hours giờ trước"
            days < 7 -> "$days ngày trước"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(past)
        }
    }
     class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val img: ImageView = itemView.findViewById(R.id.imgProduct)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val position: TextView = itemView.findViewById(R.id.tvLocation)
        val tvSellerName: TextView = itemView.findViewById(R.id.tv_SellerName)
        val tvCreateAt: TextView = itemView.findViewById(R.id.tv_CreateAt)
    }
}