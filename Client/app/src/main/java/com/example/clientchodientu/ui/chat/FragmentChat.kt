package com.example.clientchodientu.ui.chat

import android.content.Context
import com.example.clientchodientu.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientchodientu.untils.token.TokenManager
import com.example.clientchodientu.adapter.AdapterChat
import com.example.clientchodientu.dto.chat.InboxRespond
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class FragmentChat : Fragment() {
    private lateinit var rvInbox: RecyclerView
    private lateinit var adapter: AdapterChat
    private val conversationList = mutableListOf<InboxRespond>()
    private lateinit var db: FirebaseFirestore
    private var myId: Int = 0
    private var firestoreListener: ListenerRegistration? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        TokenManager.init(requireContext())
        setupRecyclerView(view)
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

    private fun setupRecyclerView(view:View) {
        rvInbox = view.findViewById(R.id.rvConversationList)
        // Khởi tạo Adapter với list rỗng và sự kiện click
        adapter = AdapterChat(conversationList) { conversation ->
            // Khi click vào 1 người, chuyển sang màn hình chat chi tiết
            val intent = Intent(requireContext(), DetailChatActivity::class.java)
            intent.putExtra("ROOM_NAME", conversation.roomName)
            intent.putExtra("MY_ID", myId)
            intent.putExtra("PARTNER_ID", conversation.partnerId)
            intent.putExtra("PARTNER_NAME", conversation.partnerName)
            startActivity(intent)
        }
        rvInbox.layoutManager = LinearLayoutManager(requireContext())
        rvInbox.adapter = adapter
    }

    private fun loadInboxDirectlyFromFirestore() {
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser ?: return // Thoát nếu chưa login

        myId = user.uid.toInt()
        db = FirebaseFirestore.getInstance()

        val query = db.collection("inboxes").document(myId.toString())
            .collection("conversations")
            .orderBy("time", Query.Direction.DESCENDING)
        firestoreListener = query.addSnapshotListener { snapshots, e ->
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

    override fun onDestroy() {
        super.onDestroy()
        firestoreListener?.remove()
    }

    override fun onResume() {
        super.onResume()
        // Lưu lại: Tôi đang ở phòng chat 123
        val prefs = requireContext().getSharedPreferences("AppStatus", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("IS_ON_CHAT", true).apply()
        Log.d("Activity", "In chat sreen")
    }

    override fun onPause() {
        super.onPause()
        // Xóa đi: Tôi không còn ở phòng chat nào cả
        val prefs = requireContext().getSharedPreferences("AppStatus", Context.MODE_PRIVATE)
        prefs.edit().remove("IS_ON_CHAT").apply()
        Log.d("Activity", "Outside chat screen")
    }

}