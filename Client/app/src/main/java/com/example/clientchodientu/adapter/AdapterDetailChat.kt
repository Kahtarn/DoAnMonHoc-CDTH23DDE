package com.example.clientchodientu.adapter

import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.R
import com.example.clientchodientu.entity.chat.ChatMessage

class AdapterDetailChat(
    private val messageList: ArrayList<ChatMessage>,
    private val myId: Int,
    private val listener: OnMessageLongClickListener
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
                    showCustomPopup(view, msg, position)

                    true
                }
            } else {
                // Nếu đã thu hồi rồi thì không cho nhấn giữ nữa
                holder.itemView.setOnLongClickListener(null)
            }
        } else if (holder is ReceivedMessageViewHolder) {
            holder.txtSender.text = msg.senderId.toString()
            holder.bind(msg)
        }
    }

    override fun getItemCount() = messageList.size

    class SentMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtContent: TextView = view.findViewById(R.id.txtContent)

        fun bind(msg: ChatMessage) {
            if (msg.isRevoke) {
                txtContent.text = "Tin nhắn đã được thu hồi"
                txtContent.alpha = 0.5f // Làm mờ đi chút cho giống thật
            } else {
                txtContent.text = msg.content
                txtContent.alpha = 1.0f
            }
        }
    }

    class ReceivedMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSender: TextView = view.findViewById(R.id.txtSender)
        val txtContent: TextView = view.findViewById(R.id.txtContent)

        fun bind(msg: ChatMessage) {
            if (msg.isRevoke) {
                txtContent.text = "Tin nhắn đã được thu hồi"
                txtContent.alpha = 0.5f
            } else {
                txtContent.text = msg.content
                txtContent.alpha = 1.0f
            }
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

    private fun showCustomPopup(anchorView: View, message: ChatMessage, position: Int) {
        // 1. Inflate the custom layout
        val inflater = LayoutInflater.from(anchorView.context)
        val popupView = inflater.inflate(R.layout.popup_message_option, null)

        // 2. Create PopupWindow
        // Width: wrap_content, Height: wrap_content, Focusable: true (closes when clicking outside)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Add a slight elevation/shadow logic if needed (Android 5.0+)
        popupWindow.elevation = 10f

        // 3. Handle Item Clicks inside the popup
        val btnCopy = popupView.findViewById<TextView>(R.id.btnCopy)
        val btnDelete = popupView.findViewById<TextView>(R.id.btnRevoke)


        btnCopy.setOnClickListener {
            // Handle Copy logic here
            listener.onCopyMessage(message)
            popupWindow.dismiss()
        }

        btnDelete.setOnClickListener {
            // Trigger the listener we made earlier


                listener.onRevokeMessage(message, position)
                popupWindow.dismiss()

        }

        // 4. Show the popup
        // xOff and yOff adjust the position.
        // 0, 0 means top-left of the popup aligns with bottom-left of the message
        // We adjust Y to make it overlap slightly or appear next to it
//        popupWindow.showAsDropDown(anchorView, 0, -anchorView.height / 2)
// 3. Gọi hàm tính toán vị trí (Đã tách riêng)
        val isMe = getItemViewType(position) == VIEW_TYPE_SENT
        val offset = calculatePos(anchorView, popupView, isMe)

        // Use showAtLocation with Gravity.NO_GRAVITY to position at exact coordinates
        popupWindow.showAtLocation(
            anchorView,
            Gravity.NO_GRAVITY,
            offset.first,
            offset.second
        )
    }


    private fun calculatePos(anchorView: View, popupView: View, isMe: Boolean): Pair<Int, Int> {
        // 1. Lấy kích thước màn hình
        val displayMetrics = anchorView.resources.displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val screenWidth = displayMetrics.widthPixels

        // 2. Đo kích thước thật của Popup (Quan trọng: ép chiều rộng không quá màn hình)
        // Dùng AT_MOST để đo chiều cao chính xác hơn nếu text bị xuống dòng
        popupView.measure(
            View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.UNSPECIFIED
        )
        val popupWidth = popupView.measuredWidth
        val popupHeight = popupView.measuredHeight

        // 3. Lấy vị trí của tin nhắn
        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val anchorX = location[0]
        val anchorY = location[1]

        // --- TÍNH TOÁN Y (DỌC) ---

        // Tính khoảng trống bên dưới và bên trên
        // Trừ đi 50px (navigation bar/padding) cho an toàn
        val spaceBelow = screenHeight - (anchorY + anchorView.height) - 50
        val spaceAbove = anchorY - 50 // Khoảng trống từ đỉnh màn hình đến đầu tin nhắn

        var finalY = 0


        // Nếu bên dưới đủ chỗ chứa Popup -> Hiện bên dưới
        // Hoặc: Nếu bên trên KHÔNG đủ chỗ hiện bên dưới (chấp nhận che phím)
        if (spaceBelow >= popupHeight || spaceAbove < popupHeight) {
            // Hiện bên dưới (Mặc định)
            finalY = anchorY + anchorView.height - 10 // -10 để đè nhẹ lên tin nhắn cho đẹp
        } else {
            // Trường hợp duy nhất hiện bên trên:
            // Bên dưới hết chỗ VÀ Bên trên đủ chỗ
            finalY = anchorY - popupHeight + 10 // +10 để đè nhẹ lên tin nhắn
        }

        // --- TÍNH TOÁN X (NGANG)
        var finalX = anchorX
        if (isMe) {
            finalX = (anchorX + anchorView.width) - popupWidth
        }

        // Fix tràn lề trái/phải
        val sideMargin = 16
        if (finalX < sideMargin) {
            finalX = sideMargin
        } else if (finalX + popupWidth > screenWidth - sideMargin) {
            finalX = screenWidth - popupWidth - sideMargin
        }

        return Pair(finalX, finalY)
    }
}