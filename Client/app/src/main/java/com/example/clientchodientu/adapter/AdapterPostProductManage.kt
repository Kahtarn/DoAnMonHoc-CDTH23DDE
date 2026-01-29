package com.example.clientchodientu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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

class AdapterPostProductManage(
    private var listSelling: List<Product>,
    private var isSold: Boolean
) :
    RecyclerView.Adapter<AdapterPostProductManage.SellingViewHolder>() {
    var onItemClick: ((Product) -> Unit)? = null
    var onEditClick: ((Product) -> Unit)? = null
    var onDeleteClick: ((Product) -> Unit)? = null
    var onSellingClick: ((Product) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SellingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_post_manager, parent, false)
        return SellingViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SellingViewHolder,
        position: Int
    ) {

        if (isSold) {
            holder.more.visibility = View.GONE
        }
        val p = listSelling[position]
        holder.title.text = p.title
        val province = p.seller?.provinceName ?: "N/A"
        val ward = p.seller?.wardName ?: ""
        holder.location.text = if (ward.isNotEmpty()) "$province\n$ward" else province
        holder.price.text = "$${p.price}"
        holder.sellerName.text = p.seller?.fullName ?: "Người bán ẩn danh"
        holder.createAt.text = convertTimeAgo(p.createAt)

        val BASE_URL = "http://10.0.2.2:8080"
        val thumbnailUrl = BASE_URL + p.thumbnailUrl

        Glide.with(holder.itemView.context)
            .load(thumbnailUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(holder.img)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(p)
        }

        holder.more.setOnClickListener { view ->
            val popup = androidx.appcompat.widget.PopupMenu(view.context, view)
            popup.menuInflater.inflate(R.menu.more_management_menu, popup.menu)

            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_edit -> {
                        onEditClick?.invoke(p)
                        true
                    }

                    R.id.menu_delete -> {
                        onDeleteClick?.invoke(p)
                        true
                    }

                    R.id.menu_sold -> {
                        onSellingClick?.invoke(p)
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return listSelling.size
    }

    class SellingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val price: TextView = itemView.findViewById(R.id.tvPrice)
        val location: TextView = itemView.findViewById(R.id.tvLocation)
        val sellerName: TextView = itemView.findViewById(R.id.tvSellerName)
        val createAt: TextView = itemView.findViewById(R.id.tvCreateAt)
        val img: ImageView = itemView.findViewById(R.id.imgProduct)
        val more: ImageButton = itemView.findViewById(R.id.btnMore)
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

    fun updateData(newList: List<Product>) {
        this.listSelling = newList
        notifyDataSetChanged()
    }

}