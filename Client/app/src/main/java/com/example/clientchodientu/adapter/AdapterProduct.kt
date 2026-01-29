package com.example.clientchodientu.adapter

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

        // 1. Định dạng ISO 8601 từ Server
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // Ép nó hiểu String đầu vào là UTC
        }

        try {
            val past = format.parse(timeString) ?: return "Định dạng sai"
            val now = Date()
            val diffMillis = now.time - past.time

            if (diffMillis < 0) return "Vừa xong"

            val seconds = TimeUnit.MILLISECONDS.toSeconds(diffMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
            val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
            val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

            return when {
                seconds < 60 -> "Vừa xong"
                minutes < 60 -> "$minutes phút trước"
                hours < 24 -> "$hours giờ trước"
                days < 7 -> "$days ngày trước"
                else -> {
                    // Hiển thị ngày tháng cụ thể nếu quá 7 ngày
                    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    outputFormat.format(past)
                }
            }
        } catch (e: Exception) {
            return "Lỗi thời gian"
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