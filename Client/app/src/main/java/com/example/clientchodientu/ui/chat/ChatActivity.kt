package com.example.clientchodientu.ui.chat

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
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

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
        val user = mAuth.currentUser ?: return // Thoát nếu chưa login

        myId = user.uid
        db = FirebaseFirestore.getInstance()

        db.collection("inboxes").document(myId)
            .collection("conversations")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                // Kiểm tra lỗi trước
                if (e != null) {
                    Log.w("FIRESTORE", "Lỗi Listen: $e")
                    return@addSnapshotListener
                }

                // Kiểm tra snapshots null an toàn
                if (snapshots != null) {
                    val newList = mutableListOf<InboxRespond>()
                    for (doc in snapshots.documentChanges) {
                        when (doc.type) {
                            DocumentChange.Type.ADDED -> {
                                try {
                                    val item = doc.document.toObject(InboxRespond::class.java)
                                    item.roomName = doc.document.id
                                    conversationList.add(0, item) // Thêm vào đầu danh sách
                                    adapter.notifyItemInserted(0)
                                } catch (ex: Exception) {
                                    Log.e("FIRESTORE", " Lỗi: ${ex.message}")
                                }
                            }

                            DocumentChange.Type.MODIFIED -> {
                                try {
                                    val item = doc.document.toObject(InboxRespond::class.java)
                                    item.roomName = doc.document.id
                                    val index =
                                        conversationList.indexOfFirst { it.roomName == item.roomName }
                                    if (index != -1) {
                                        if (item.isRevoke) {
                                            item.lastMessage = "Tin nhắn đã được thu hồi"
                                        }
                                        conversationList[index] = item
                                        // Chỉ cập nhật đúng dòng đó trên giao diện, không load lại cả list
                                        adapter.notifyItemChanged(index)
                                    }
                                } catch (ex: Exception) {
                                    Log.e("FIRESTORE", " Lỗi: ${ex.message}")
                                }
                            }

                            DocumentChange.Type.REMOVED -> {
                                val index =
                                    conversationList.indexOfFirst { it.roomName == doc.document.id }
                                if (index != -1) {
                                    conversationList.removeAt(index)
                                    adapter.notifyItemRemoved(index)
                                }
                            } //

                        }
                    }

                }
            }
    }

}