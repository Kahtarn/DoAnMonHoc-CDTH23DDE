package com.example.clientchodientu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.entity.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class AdapterFavorite(
    private var favoritePost: List<Product>,
) : RecyclerView.Adapter<AdapterFavorite.FavoriteViewHolder>() {
    var onItemClick: ((Product) -> Unit)? = null
    var onFavoriteClick: ((Product) -> Unit)? = null
    var onSellingClick: ((Product) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_post_favorite, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: FavoriteViewHolder,
        position: Int
    ) {
        val p = favoritePost[position]
        holder.title.text = p.title
        val province = p.seller?.provinceName ?: "N/A"
        val ward = p.seller?.wardName ?: ""
        holder.location.text = if (ward.isNotEmpty()) "$province\n$ward" else province
        holder.price.text = "$${p.price}"
        holder.sellerName.text = p.seller?.fullName ?: "Người bán ẩn danh"
        holder.createAt.text = convertTimeAgo(p.createAt)
        holder.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
        val BASE_URL = "https://uncondensable-diplopic-gibson.ngrok-free.dev"
        val thumbnailUrl = BASE_URL + p.thumbnailUrl

        Glide.with(holder.itemView.context)
            .load(thumbnailUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(p)
        }

        holder.btnFavorite.setOnClickListener {
            holder.btnFavorite.setImageResource(R.drawable.ic_favorite_unfilled)
            onFavoriteClick?.invoke(p)
        }


    }

    override fun getItemCount(): Int {
        return favoritePost.size
    }

    class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val location: TextView = itemView.findViewById(R.id.tvLocation)
        val sellerName: TextView = itemView.findViewById(R.id.tvSellerName)
        val createAt: TextView = itemView.findViewById(R.id.tvCreateAt)
        val img: ImageView = itemView.findViewById(R.id.imgProduct)
        val btnFavorite: AppCompatImageView = itemView.findViewById(R.id.btnFavorite)
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

    fun updateData(newList: List<Product>) {
        this.favoritePost = newList
        notifyDataSetChanged()
    }

}