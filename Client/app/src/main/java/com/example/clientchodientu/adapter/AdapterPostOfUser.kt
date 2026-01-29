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

        val BASE_URL = "https://uncondensable-diplopic-gibson.ngrok-free.dev"
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

    class ViewHolderPostOfUser(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgProduct)
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvCreateAt: TextView = itemView.findViewById(R.id.tv_CreateAt)
    }
}