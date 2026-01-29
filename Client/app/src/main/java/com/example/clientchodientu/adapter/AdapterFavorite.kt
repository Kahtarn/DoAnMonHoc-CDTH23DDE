package com.example.clientchodientu.adapter

import android.util.Log
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
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

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
        if (timeString.isNullOrBlank()) return "Không rõ thời gian"

        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        try {
            val past = format.parse(timeString) ?: return "Định dạng sai"
            val now = Date()

            // Sử dụng giá trị tuyệt đối Math.abs để tránh số âm khi máy chậm hơn server
            val diffMillis = now.time - past.time
            val absDiff = Math.abs(diffMillis)

            // Nếu lệch dưới 1 phút (bất kể âm hay dương) thì coi là vừa xong
            if (absDiff < 60000) return "Vừa xong"

            val minutes = TimeUnit.MILLISECONDS.toMinutes(absDiff)
            val hours = TimeUnit.MILLISECONDS.toHours(absDiff)
            val days = TimeUnit.MILLISECONDS.toDays(absDiff)

            // Log để debug: Bạn sẽ thấy sự chênh lệch khủng khiếp nếu giờ máy bị sai
            Log.d("TIME_DEBUG", "Diff: $diffMillis | Now: ${now.time} | Past: ${past.time}")

            return when {
                minutes < 60 -> "$minutes phút trước"
                hours < 24 -> "$hours giờ trước"
                days < 7 -> "$days ngày trước"
                else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(past)
            }
        } catch (e: Exception) {
            return "Lỗi thời gian"
        }
    }

    fun updateData(newList: List<Product>) {
        this.favoritePost = newList
        notifyDataSetChanged()
    }

}