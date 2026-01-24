package com.example.emailotp

import com.example.clientchodientu.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.untils.TokenManager
import com.example.clientchodientu.adapter.AdapterChat
import com.example.clientchodientu.dto.chat.InboxRespond
import com.example.clientchodientu.entity.chat.ApiResponse
import com.example.clientchodientu.ui.chat.DetailChatActivity
import com.example.clientchodientu.untils.ApiClient
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ChatActivity : AppCompatActivity() {
    private lateinit var rvInbox: RecyclerView
    private lateinit var adapter: AdapterChat
    private val conversationList = mutableListOf<InboxRespond>()
    private lateinit var db: FirebaseFirestore
    private var myId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        FirebaseApp.initializeApp(this)
        TokenManager.init(this)
        setupRecyclerView()
        lifecycleScope.launch {
            val firebaseToken = TokenManager.getFirebaseToken() ?: ""
            val mAuth = FirebaseAuth.getInstance()

            //sign out  mAuth.signOut()
            mAuth.signInWithCustomToken(firebaseToken)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Đăng nhập Firebase thành công!
                        // Lúc này request.auth.uid trên Firestore đã khớp với userId của bạn
                        Log.d("FIREBASE_AUTH", "Đã xác thực với Firebase thành công")
                        loadInboxDirectlyFromFirestore()
                    } else {
                        Log.e("FIREBASE_AUTH", "Lỗi đăng nhập Firebase: ${task.exception?.message}")
                    }
                }
        }

    }

    private fun setupRecyclerView() {
        rvInbox = findViewById(R.id.rvConversationList)
        // Khởi tạo Adapter với list rỗng và sự kiện click
        adapter = AdapterChat(conversationList) { conversation ->
            // Khi click vào 1 người, chuyển sang màn hình chat chi tiết
            val intent = Intent(this, DetailChatActivity::class.java)
            intent.putExtra("ROOM_NAME", conversation.roomName)
            intent.putExtra("PARTNER_ID", conversation.partnerId)
            intent.putExtra("MY_ID", myId)
            intent.putExtra("PARTNER_NAME", conversation.partnerName)
            startActivity(intent)

        }
        rvInbox.layoutManager = LinearLayoutManager(this)
        rvInbox.adapter = adapter
    }

    private fun loadInboxDirectlyFromFirestore() {

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if (user != null) {
             myId = user.uid
            db = FirebaseFirestore.getInstance()
            // Truy vấn thẳng: Lấy tất cả hội thoại, sắp xếp theo thời gian mới nhất
            db.collection("inboxes").document(myId)
                .collection("conversations")
                .orderBy("time", Query.Direction.DESCENDING) // Tự động đưa tin mới lên đầu
                .addSnapshotListener { snapshots, e ->
                    if (e != null) {
                        Log.w("FIRESTORE", "Listen failed (Có thể đang offline): $e")
                    }

                    val newList = mutableListOf<InboxRespond>()
                    for (doc in snapshots!!) {
                        val item = doc.toObject(InboxRespond::class.java)
                        item.roomName = doc.id.toString()
                        newList.add(item)
                    }
                    // Cập nhật toàn bộ Adapter
                    conversationList.clear()
                    conversationList.addAll(newList)
                    adapter.notifyDataSetChanged()
                }
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập Firebase", Toast.LENGTH_SHORT).show()
            Log.e("FIREBASE_AUTH", "Người dùng chưa đăng nhập Firebase")
        }
    }

}