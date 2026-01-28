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

class AdapterPostOfUser(private var list: List<Product>) :
    RecyclerView.Adapter<AdapterPostOfUser.ViewHolderPostOfUser>() {

    var onItemClick: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderPostOfUser {
        // Ní kiểm tra lại file layout này có đúng tên không nhé
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_product, parent, false)
        return ViewHolderPostOfUser(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolderPostOfUser, position: Int) {
        val product = list[position]
        holder.title.text = product.title

        val province = product.seller?.provinceName ?: "N/A"
        val ward = product.seller?.wardName ?: ""
        holder.tvLocation.text = if (ward.isNotEmpty()) "$ward, $province" else province
        holder.tvPrice.text = "${product.price} đ"
        holder.tvCreateAt.text = convertTimeAgo(product.createAt)

        val BASE_URL = "http://10.0.2.2:8080"
        val thumbnailUrl = BASE_URL + (product.thumbnailUrl ?: "")

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
        if (timeString == null) return "N/A"
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            val past = format.parse(timeString) ?: return ""
            val now = Date()
            val diff = now.time - past.time

            val second = 1000L
            val minute = 60 * second
            val hour = 60 * minute
            val day = 24 * hour

            when {
                diff < 0 -> "Vừa xong"
                diff < minute -> "Vừa xong"
                diff < hour -> "${diff / minute} phút trước"
                diff < day -> "${diff / hour} giờ trước"
                diff < 7 * day -> "${diff / day} ngày trước"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(past)
            }
        } catch (e: Exception) {
            "Lỗi định dạng"
        }
    }

    class ViewHolderPostOfUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgProduct)
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvCreateAt: TextView = itemView.findViewById(R.id.tv_CreateAt)
    }
}