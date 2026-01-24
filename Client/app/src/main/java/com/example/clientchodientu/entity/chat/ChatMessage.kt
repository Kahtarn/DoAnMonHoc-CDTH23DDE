package com.example.clientchodientu.entity.chat

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class ChatMessage(
    var id: String = "",
    var senderId: Int = 0,
    var receiverId: Int = 0,
    var productId: Int = 0,
    var content: String = "",
    @get:PropertyName("isRevoke")
    @set:PropertyName("isRevoke")
    var isRevoke: Boolean = false,
    var createAt: Timestamp = Timestamp.Companion.now()
)