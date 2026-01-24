package com.example.clientchodientu.adapter

import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.entity.ChatMessage
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

object TimeUtil {
    public fun formatTime(timeObj: Any?): String {
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

class AdapterDetailChat(
    private val messageList: ArrayList<ChatMessage>,
    private val myId: Int,
    private val listener: OnMessageLongClickListener,
    private val receiverName: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        Log.d(
            "ChatDebug",
            "MsgContent: ${message.content} | Sender: ${message.senderId} | MyId: $myId"
        )
        return if (message.senderId == myId) {
            VIEW_TYPE_SENT // Là tin của mình -> trả về 1
        } else {
            VIEW_TYPE_RECEIVED // Là tin người khác -> trả về 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_me, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_chat_other, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messageList[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(msg)

            if (!msg.isRevoke) {
                holder.itemView.setOnLongClickListener { view ->
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                    // CALL THE NEW FUNCTION
                    showCustomPopup(view, msg, position, true)
                    true
                }
            } else {
                // Nếu đã thu hồi rồi thì không cho nhấn giữ nữa
                holder.itemView.setOnLongClickListener(null)
            }
        } else if (holder is ReceivedMessageViewHolder) {
            holder.txtSender.text = receiverName
            holder.bind(msg)
// is me = false, chi co copy khong co thu hoi
            if (!msg.isRevoke) {
                holder.itemView.setOnLongClickListener { view ->
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)

                    // CALL THE NEW FUNCTION
                    showCustomPopup(view, msg, position, false)
                    true
                }
            } else {
                // Nếu đã thu hồi rồi thì không cho nhấn giữ nữa
                holder.itemView.setOnLongClickListener(null)
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
                txtContent.alpha = 0.5f // Làm mờ đi chút cho giống thật
            } else {
                txtContent.text = msg.content
                txtContent.alpha = 1.0f
            }
            txtTimeSend.text = TimeUtil.formatTime(msg.createAt)
        }
    }

    class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSender: TextView = view.findViewById(R.id.txtSender)
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