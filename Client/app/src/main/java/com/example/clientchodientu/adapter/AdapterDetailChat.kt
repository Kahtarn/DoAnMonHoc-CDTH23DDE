package com.example.clientchodientu.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clientchodientu.R
import com.example.clientchodientu.dto.chat.ChatMessage
import com.example.clientchodientu.untils.time.TimeUtil

class AdapterDetailChat(
    private val messageList: ArrayList<ChatMessage>,
    private val myId: Int,
    private val listener: OnMessageLongClickListener,
    private val receiverName: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT_TEXT = 1
        const val VIEW_TYPE_RECEIVED_TEXT = 2
        const val VIEW_TYPE_SENT_PRODUCT = 3
        const val VIEW_TYPE_RECEIVED_PRODUCT = 4
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        Log.d(
            "ChatDebug",
            "MsgContent: ${message.content} | Sender: ${message.senderId} | MyId: $myId"
        )
        val isMe = message.senderId == myId

        return if (message.type == "PRODUCT") {
            if (isMe) VIEW_TYPE_SENT_PRODUCT else VIEW_TYPE_RECEIVED_PRODUCT
        } else {
            if (isMe) VIEW_TYPE_SENT_TEXT else VIEW_TYPE_RECEIVED_TEXT
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_SENT_TEXT -> SentMessageViewHolder(
                inflater.inflate(
                    R.layout.item_chat_me,
                    parent,
                    false
                )
            )

            VIEW_TYPE_RECEIVED_TEXT -> ReceiveMessageViewHolder(
                inflater.inflate(
                    R.layout.item_chat_other,
                    parent,
                    false
                )
            )

            VIEW_TYPE_SENT_PRODUCT -> SentProductViewHolder(
                inflater.inflate(
                    R.layout.item_chat_product_me,
                    parent,
                    false
                )
            )

            VIEW_TYPE_RECEIVED_PRODUCT -> ReceivedProductViewHolder(
                inflater.inflate(
                    R.layout.item_chat_product_other,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Wrong view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messageList[position]
        val isMe = msg.senderId == myId

        when (holder) {
            is SentMessageViewHolder -> holder.bind(msg)
            is ReceiveMessageViewHolder -> holder.bind(msg)
            is SentProductViewHolder -> holder.bind(msg)
            is ReceivedProductViewHolder -> {
                holder.bind(msg)
                // Có thể thêm tên người gửi cho giống Telegram
            }
        }

        // Xử lý Popup Menu cho tất cả (trừ khi đã thu hồi)
        if (!msg.isRevoke) {
            holder.itemView.setOnLongClickListener { view ->
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                showCustomPopup(view, msg, position, isMe)
                true
            }
        }
    }

    override fun getItemCount() = messageList.size

    class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtContent: TextView = view.findViewById(R.id.txtContent)
        val txtTimeSend = view.findViewById<TextView>(R.id.txtTimeSend)
        fun bind(msg: ChatMessage) {
            if (msg.isRevoke) {
                txtContent.text = "Tin nhắn đã được thu hồi"
                txtContent.alpha = 0.5f
            } else {
                txtContent.text = msg.content
                txtContent.alpha = 1.0f
            }
            txtTimeSend.text = TimeUtil.formatTime(msg.createAt)
        }
    }

    class SentProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtContent: TextView = view.findViewById(R.id.txtContent)
        val txtProductName: TextView = view.findViewById(R.id.txtProductName)
        val txtProductPrice: TextView = view.findViewById(R.id.txtProductPrice)
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val txtTimeSend: TextView = view.findViewById(R.id.txtTimeSend)
        val divider: View = view.findViewById(R.id.divider_me)
        val layoutProduct = itemView.findViewById<LinearLayout>(R.id.layoutProductMe)
        fun bind(msg: ChatMessage) {
            if (msg.isRevoke) {
                imgProduct.visibility = View.GONE
                txtProductName.visibility = View.GONE
                txtProductPrice.visibility = View.GONE
                if (divider != null) divider.visibility = View.GONE

                txtContent.text = "Tin nhắn đã được thu hồi"

                layoutProduct.visibility = View.VISIBLE
                layoutProduct.alpha = 0.5f
            } else {
                imgProduct.visibility = View.VISIBLE
                txtProductName.visibility = View.VISIBLE
                txtProductPrice.visibility = View.VISIBLE
                if (divider != null) divider.visibility = View.VISIBLE

                txtContent.text = msg.content
                layoutProduct.alpha = 1.0f

                // Bốc Metadata từ Map (Firestore trả về Map<String, Any>)
                val meta = msg.metadata
                if (meta != null) {
                    txtProductName.text = meta.productName as? String ?: "Sản phẩm"
                    txtProductPrice.text = "${meta.productPrice}đ"

                    val imgUrl = meta.productImage as? String
                    Glide.with(itemView.context).load(imgUrl)
                        .placeholder(R.drawable.ic_placeholder_product).into(imgProduct)
                }

                itemView.alpha = 1.0f
            }
            txtTimeSend.text = TimeUtil.formatTime(msg.createAt)
        }
    }

    class ReceiveMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtContent: TextView = view.findViewById(R.id.txtContent)
        val txtTimeReceive = view.findViewById<TextView>(R.id.txtTimeReceive)

        fun bind(msg: ChatMessage) {
            if (msg.isRevoke) {
                txtContent.text = "Tin nhắn đã được thu hồi"
                txtContent.alpha = 0.5f
            } else {
                txtContent.text = msg.content
                txtContent.alpha = 1.0f
            }
            txtTimeReceive.text = TimeUtil.formatTime(msg.createAt)
        }
    }

    class ReceivedProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtContent: TextView = view.findViewById(R.id.txtContent)
        val txtProductName: TextView = view.findViewById(R.id.txtProductName)
        val txtProductPrice: TextView = view.findViewById(R.id.txtProductPrice)
        val imgProduct: ImageView = view.findViewById(R.id.imgProduct)
        val txtTimeSend: TextView = view.findViewById(R.id.txtTimeSend)
        val divider: View = view.findViewById(R.id.divider_other)
        val layoutProduct = itemView.findViewById<LinearLayout>(R.id.layoutChatProductOther)
        fun bind(msg: ChatMessage) {
            if (msg.isRevoke) {

                imgProduct.visibility = View.GONE
                txtProductName.visibility = View.GONE
                txtProductPrice.visibility = View.GONE
                divider.visibility = View.GONE

                txtContent.text = "Tin nhắn đã được thu hồi"
                layoutProduct.visibility = View.VISIBLE
                layoutProduct.alpha = 0.5f
            } else {
                imgProduct.visibility = View.VISIBLE
                txtProductName.visibility = View.VISIBLE
                txtProductPrice.visibility = View.VISIBLE
                if (divider != null) divider.visibility = View.VISIBLE

                txtContent.text = msg.content
                // Bốc Metadata từ Map (Firestore trả về Map<String, Any>)
                val meta = msg.metadata
                if (meta != null) {
                    txtProductName.text = meta.productName as? String ?: "Sản phẩm"
                    txtProductPrice.text = "${meta.productPrice}đ"

                    val imgUrl = meta.productImage as? String
                    Glide.with(itemView.context).load(imgUrl).into(imgProduct)
                }

                layoutProduct.alpha = 1.0f
            }
            txtTimeSend.text = TimeUtil.formatTime(msg.createAt)
        }
    }

    fun addMessage(message: ChatMessage) {
        messageList.add(message)
        // Chỉ reload vị trí mới thêm vào (tối ưu hiệu năng hơn notifyDataSetChanged)
        notifyItemInserted(messageList.size - 1)
    }

    fun updateMessage(updatedMessage: ChatMessage) {
        // 1. Tìm vị trí của tin nhắn cần sửa trong danh sách hiện tại
        val index = messageList.indexOfFirst { it.id == updatedMessage.id }

        if (index != -1) {
            // 2. Cập nhật dữ liệu tại vị trí đó
            messageList[index] = updatedMessage

            // 3. Chỉ render lại đúng dòng đó thôi (Hiệu năng cao)
            notifyItemChanged(index)
        }
    }

    private fun showCustomPopup(
        anchorView: View,
        message: ChatMessage,
        position: Int,
        isMe: Boolean
    ) {
        val inflater = LayoutInflater.from(anchorView.context)
        val popupView = inflater.inflate(R.layout.popup_message_option, null)

        // --- BƯỚC 1: Xử lý Logic Ẩn/Hiện nút TRƯỚC khi đo kích thước ---
        val btnRevoke = popupView.findViewById<TextView>(R.id.btnRevoke)
        val btnCopy = popupView.findViewById<TextView>(R.id.btnCopy)

        // Xử lý nút Copy (Luôn hiện)
        btnCopy.setOnClickListener {
            listener.onCopyMessage(message)
            // Lưu ý: Cần khai báo popupWindow ở scope rộng hơn hoặc dùng biến tạm để dismiss
            // (Xem phần dưới để thấy cách xử lý popupWindow.dismiss())
        }

        // Xử lý nút Thu hồi (Chỉ hiện khi isMe = true)
        if (isMe) {
            btnRevoke.visibility = View.VISIBLE
            btnRevoke.setOnClickListener {
                listener.onRevokeMessage(message, position)
            }
        } else {
            // QUAN TRỌNG: Dùng GONE để nó biến mất hoàn toàn và layout co lại
            btnRevoke.visibility = View.GONE
        }

        // --- BƯỚC 2: Khởi tạo PopupWindow ---
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.elevation = 10f

        // Gán sự kiện dismiss lại cho các nút sau khi có đối tượng popupWindow
        btnCopy.setOnClickListener {
            listener.onCopyMessage(message)
            popupWindow.dismiss()
        }
        if (isMe) {
            btnRevoke.setOnClickListener {
                listener.onRevokeMessage(message, position)
                popupWindow.dismiss()
            }
        }

        // --- BƯỚC 3: Tính toán vị trí
        val offset = calculatePos(anchorView, popupView, isMe)

        // --- BƯỚC 4: Hiển thị ---
        popupWindow.showAtLocation(
            anchorView,
            Gravity.NO_GRAVITY,
            offset.first,
            offset.second
        )
    }

    //  dai voai luon
    private fun calculatePos(anchorView: View, popupView: View, isMe: Boolean): Pair<Int, Int> {
        // 1. Lấy kích thước màn hình
        val displayMetrics = anchorView.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        // 2. Đo kích thước thật của Popup
        // Phải đo sau khi đã set Visibility (GONE/VISIBLE) ở hàm trên
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val popupWidth = popupView.measuredWidth
        val popupHeight = popupView.measuredHeight

        // 3. Lấy vị trí của tin nhắn trên màn hình
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val anchorX = location[0]
        val anchorY = location[1]

        // --- TÍNH TOÁN Y (DỌC) ---
        // Logic: Ưu tiên hiện bên dưới, nếu hết chỗ thì nhảy lên trên
        val spaceBelow = screenHeight - (anchorY + anchorView.height)
        // Check xem bên dưới có đủ chỗ chứa popup + 1 khoảng đệm (ví dụ 50px) không
        val showBelow = spaceBelow > popupHeight + 50

        val finalY = if (showBelow) {
            anchorY + anchorView.height // Hiện ngay dưới tin nhắn
        } else {
            anchorY - popupHeight // Hiện ngay trên tin nhắn
        }

        // --- TÍNH TOÁN X (NGANG) ---
        var finalX: Int

        if (isMe) {
            // Nếu là mình: Căn Phải (Right Align)
            // X = (Vị trí X của tin nhắn + Chiều rộng tin nhắn) - Chiều rộng Popup
            finalX = (anchorX + anchorView.width) - popupWidth
        } else {
            // Nếu là người khác: Căn Trái (Left Align)
            // X = Vị trí X của tin nhắn
            finalX = anchorX
        }

        // --- FIX TRÀN MÀN HÌNH (PADDING) ---
        val margin = 16 // Khoảng cách tối thiểu với mép màn hình
        if (finalX < margin) {
            finalX = margin
        } else if (finalX + popupWidth > screenWidth - margin) {
            finalX = screenWidth - popupWidth - margin
        }

        return Pair(finalX, finalY)
    }
}