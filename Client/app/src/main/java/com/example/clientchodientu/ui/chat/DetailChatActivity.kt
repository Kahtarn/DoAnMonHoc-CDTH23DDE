package com.example.clientchodientu.ui.chat

import com.example.clientchodientu.R
import android.Manifest
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.untils.ApiClient
import com.example.clientchodientu.untils.TokenManager
import com.example.clientchodientu.adapter.AdapterDetailChat
import com.example.clientchodientu.adapter.OnMessageLongClickListener
import com.example.clientchodientu.dto.chat.RevokeMessageRespond
import com.example.clientchodientu.dto.chat.RevokeRequest
import com.example.clientchodientu.dto.chat.SendMessageRequest
import com.example.clientchodientu.entity.chat.ChatMessage
import com.example.emailotp.service.FirebaseMessagingService
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.properties.Delegates

class DetailChatActivity : AppCompatActivity(), OnMessageLongClickListener {
    private lateinit var db: FirebaseFirestore
    private val messageList = ArrayList<ChatMessage>()
    private lateinit var chatAdapter: AdapterDetailChat

    private lateinit var rcvChat: RecyclerView
    private lateinit var roomName: String
    private var myId by Delegates.notNull<Int>()
    private var receiverId by Delegates.notNull<Int>()
    private var myUsername = ""
    private var otherUsername = ""
    private var firestoreListener: ListenerRegistration? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("notification", "Notification permission granted")
        } else {
            Log.e("notification", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)
        TokenManager.init(this)

        rcvChat = findViewById(R.id.rcvChat)
        askNotificationPermission()

        roomName = intent.getStringExtra("ROOM_NAME") ?: "general"
        receiverId = intent.getIntExtra("PARTNER_ID", 0)
        myId = intent.getStringExtra("MY_ID")?.toInt() ?: 0
        Log.d("DetailChatActivity", "Room Name: $roomName, Receiver ID: $receiverId")

        db = FirebaseFirestore.getInstance()

        setupRecyclerView()
        listenToMessages()
        // update key fcm
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Lấy token thất bại", task.exception)
                return@addOnCompleteListener
            }

            // 3. Gửi Token lên Server
            val token = task.result
            Log.d("FCM main", "Token FCM hiện tại: $token")
            TokenManager.updateFCMToken(this, token)

            copyToClipboard(this, token)
        }

        // Sự kiện nút Gửi Tin Nhắn Chat
        findViewById<ImageButton>(R.id.btnSendChat).setOnClickListener {
            val edtMessage = findViewById<EditText>(R.id.edtMessage)
            val content = edtMessage.text.toString()
            if (content.isNotEmpty()) {
                lifecycleScope.launch {
                    sendMessage(content)
                    edtMessage.setText("")
                }
            }
        }
        // xoa roomMessages  khi nhan vao thong bao
        val roomId = intent.getStringExtra("roomId")
        if (roomId != null) {
            // Clear message history
            FirebaseMessagingService.roomMessages.remove(roomId)

            // Cancel the notification
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(roomId.hashCode())
        }

    }

    private fun setupRecyclerView() {
        val rcvChat = findViewById<RecyclerView>(R.id.rcvChat)
        val myLayoutManager = LinearLayoutManager(this)
        myLayoutManager.stackFromEnd = true
        rcvChat.layoutManager = myLayoutManager
        chatAdapter = AdapterDetailChat(messageList, myId, this)
        rcvChat.adapter = chatAdapter
    }

    private fun listenToMessages() {
        // Truy vấn vào Collection chứa tin nhắn của Room đó
        // Sắp xếp theo thời gian để tin cũ lên trên, tin mới ở dưới
        val query = db.collection("chat_rooms").document(roomName)
            .collection("messages")
            .orderBy("createAt", Query.Direction.ASCENDING)

        // addSnapshotListener: Hàm này sẽ chạy NGAY LẬP TỨC khi có bất kỳ thay đổi nào trên DB
        firestoreListener = query.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.e("Chat", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                // documentChanges: Chỉ chứa những thay đổi (Thêm/Sửa/Xóa)
                for (dc in snapshots.documentChanges) {
                    when (dc.type) {
                        // Nếu có tin nhắn MỚI được thêm vào
                        DocumentChange.Type.ADDED -> {
                            // Convert Document thành Object Message
                            val newMessage = dc.document.toObject(ChatMessage::class.java)
                            newMessage.id = dc.document.id
                            Log.d("fire store new message", newMessage.toString())

                            if (newMessage.isRevoke) {
                                newMessage.content = "Tin nhắn đã được thu hồi"
                            }
                            // Thêm vào Adapter
                            chatAdapter.addMessage(newMessage)

                            // Tự động cuộn xuống tin nhắn cuối cùng
                            rcvChat.smoothScrollToPosition(messageList.size - 1)
                        }

                        DocumentChange.Type.MODIFIED -> {
                            // Convert Document thành Object Message
                            val modifiedMessage = dc.document.toObject(ChatMessage::class.java)
                            modifiedMessage.id = dc.document.id
                            Log.d("fire store change", modifiedMessage.toString())

                            if (modifiedMessage.isRevoke) {
                                modifiedMessage.content = "Tin nhắn đã được thu hồi"
                            }
                            // Thêm vào Adapter
                            chatAdapter.updateMessage(modifiedMessage)

                            // Tự động cuộn xuống tin nhắn cuối cùng
                            rcvChat.smoothScrollToPosition(messageList.size - 1)
                        } // Xử lý nếu cần sửa tin
                        DocumentChange.Type.REMOVED -> {}  // Xử lý nếu cần thu hồi tin
                    }
                }
            }
        }
    }

    // huy qua trinh lang nghe
    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
    }

    override fun onResume() {
        super.onResume()
        // Lưu lại: Tôi đang ở phòng chat 123
        val prefs = getSharedPreferences("AppStatus", MODE_PRIVATE)
        prefs.edit().putBoolean("IS_ON_CHAT", true).apply()
        Log.d("Activity", "In chat sreen")
    }

    override fun onPause() {
        super.onPause()
        // Xóa đi: Tôi không còn ở phòng chat nào cả
        val prefs = getSharedPreferences("AppStatus", MODE_PRIVATE)
        prefs.edit().remove("IS_ON_CHAT").apply()
        Log.d("Activity", "Outside chat screen")
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)

                // Thêm kiểm tra nút Gửi
                val btnSend = findViewById<ImageButton>(R.id.btnSendChat)
                val btnRect = Rect()
                btnSend.getGlobalVisibleRect(btnRect)

                // Nếu chạm ngoài EditText VÀ ngoài cả nút Gửi thì mới ẩn bàn phím
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt()) &&
                    !btnRect.contains(event.rawX.toInt(), event.rawY.toInt())
                ) {

                    v.clearFocus()
                    val imm =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    suspend fun sendMessage(message: String) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val gson = Gson()

        // 1. Tham chiếu đến collection tin nhắn trong Firestore
        val messagesCollection = Firebase.firestore
            .collection("chat_rooms")
            .document(roomName)
            .collection("messages")

        // 2. Yêu cầu Firestore sinh ra một Document Reference MỚI (chưa có dữ liệu)
        // Lúc này Firestore tự động tạo ra một ID ngẫu nhiên, ví dụ: "Az9s8d7f6g5h4j3k2l"
        val newMsgRef = messagesCollection.document()

        // 3. Lấy cái ID đó ra
        val firestoreGeneratedId = newMsgRef.id

        Log.d("Chat", "ID do Firestore sinh ra là: $firestoreGeneratedId")


        // cho nay
        val sendMessageRequest =
            SendMessageRequest(firestoreGeneratedId, receiverId, message, false)
        val jsonString = gson.toJson(sendMessageRequest)
        val requestBody = jsonString.toRequestBody(mediaType)

        Log.d("ACCESS_TOKEN", TokenManager.getToken().toString())

        val request = Request.Builder()
            .url("http://10.0.2.2:8080/api/chat/send-private")
            .post(requestBody)
            .build()

        withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val respond = ApiClient.getClient(this@DetailChatActivity).newCall(request).execute()

            if (respond.isSuccessful) {
                withContext(Dispatchers.Main) {
                    Log.d("Chat", "Gửi tin nhắn thành công")
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.e("Chat", "Gửi tin nhắn thất bại: ${respond.code}")

                }
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun copyToClipboard(context: Context, textToCopy: String) {
        // 1. Get the Clipboard Manager service
        val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        // 2. Create the ClipData (label can be anything, it's for accessibility)
        val clip = ClipData.newPlainText("Copied Text", textToCopy)

        // 3. Set the data to the clipboard
        clipboard.setPrimaryClip(clip)

        // 4. Show feedback (Only for Android 12 and below)
        // Android 13+ (API 33) automatically shows a system UI confirmation, so we skip the Toast.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRevokeMessage(message: ChatMessage, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn muốn thu hồi tin nhắn: \"${message.content}\"?")
            .setPositiveButton("Thu hồi") { _, _ ->


                Thread {
                    val gson = Gson()
                    val sendMessageRequest = RevokeRequest(
                        message.id,
                        roomName
                    )
                    val requestBody = gson.toJson(sendMessageRequest)
                        .toRequestBody("application/json".toMediaType())

                    val request = Request.Builder()
                        .url("http://10.0.2.2:8080/api/chat/revoke-message")
                        .post(requestBody)
                        .build()

                    val response = ApiClient.getClient(this).newCall(request).execute()
                    val responseString = response.body?.string()
                    val data = gson.fromJson(responseString, RevokeMessageRespond::class.java)

                    if (response.isSuccessful) {
                        message.isRevoke = true
                        // Cập nhật dữ liệu
                        // Báo cho Adapter cập nhật lại dòng đó
                        runOnUiThread {

                            chatAdapter.notifyItemChanged(position)

                            Toast.makeText(this, data.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Thu hồi thất bại ${data.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }.start()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // 4. Xử lý khi chọn "Copy" từ Menu
    override fun onCopyMessage(message: ChatMessage) {
        // Copy text vào clipboard
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("message", message.content)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Đã sao chép", Toast.LENGTH_SHORT).show()
    }

}
