package com.example.clientchodientu.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.chat.InboxRespond
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterChat(
    private val conversationList: List<InboxRespond>,
    private val onItemClick: (InboxRespond) -> Unit
) : RecyclerView.Adapter<AdapterChat.ChatViewHolder>() {

    class ChatViewHolder(view: ViewGroup) : RecyclerView.ViewHolder(view) {
        val txtLastMessage = view.findViewById<TextView>(R.id.tvLastMessage)
        val imgAvatar = view.findViewById<ImageView>(R.id.imgAvatar)
        val txtName = view.findViewById<TextView>(R.id.tvUserName)
        val txtDate = view.findViewById<TextView>(R.id.tvDate)

        fun bind(item: InboxRespond) {
            // Bind data to views here
            txtName.text = item.partnerName
            txtLastMessage.text = item.lastMessage
            txtDate.text = formatTime(item.time )

            Glide.with(itemView.context)
                .load(item.avatarUrl)
                .placeholder(R.drawable.ic_user_placeholder) // Tạo một icon mặc định
                .circleCrop() // Bo tròn ảnh
                .into(imgAvatar)
            if (item.isRevoke) {
                txtLastMessage.text = "Tin nhắn đã được thu hồi"
                txtLastMessage.setTypeface(null, Typeface.ITALIC) // In nghiêng
                txtLastMessage.alpha = 0.5f // Làm mờ
            } else {
                txtLastMessage.text = item.lastMessage
                txtLastMessage.setTypeface(null, Typeface.NORMAL)
                txtLastMessage.alpha = 1.0f
                txtLastMessage.setTextColor(Color.parseColor("#757575")) // màu xám
            }
        }

        private fun formatTime(timeObj: Any?): String {
            if (timeObj == null) return ""

            return try {
                when (timeObj) {
                    // Trường hợp dữ liệu từ Firestore
                    is Timestamp -> {
                        val date = timeObj.toDate()
                        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                        sdf.format(date)
                    }
                    // Trường hợp dữ liệu từ API (String)
                    is String -> {
                        if (timeObj.length >= 16) timeObj.substring(11, 16) else timeObj
                    }
                    else -> timeObj.toString()
                }
            } catch (e: Exception) {
                ""
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ChatViewHolder(view as ViewGroup)
    }

    override fun onBindViewHolder(
        holder: ChatViewHolder,
        position: Int
    ) {
        val item = conversationList[position]
        holder.bind(item)

        // Bắt sự kiện click vào cả dòng
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return conversationList.size
    }
}