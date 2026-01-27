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
import java.util.TimeZone

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
        holder.price.text = "$${product.price}"
        holder.tvSellerName.text = product.seller?.fullName ?: "Người bán ẩn danh"
        holder.tvCreateAt.text = convertTimeAgo(product.createAt)

        val BASE_URL = "http://192.168.1.111:8080"
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
    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val img: ImageView = itemView.findViewById(R.id.imgProductM)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val position: TextView = itemView.findViewById(R.id.tvLocation)
        val tvSellerName: TextView = itemView.findViewById(R.id.tv_SellerName)
        val tvCreateAt: TextView = itemView.findViewById(R.id.tv_CreateAt)
    }
}