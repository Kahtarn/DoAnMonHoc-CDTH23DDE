package com.example.clientchodientu.dto.chat

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class ChatMessage(
    var id: String = "",
    val senderId: Int = 0,
    var content: String = "",
    val type: String = "TEXT", // Thêm cái này
    @get:PropertyName("isRevoke")
    @set:PropertyName("isRevoke")
    var isRevoke: Boolean = false,
    val createAt: Timestamp = Timestamp.now(),
    val metadata: ProductMetadata? = null // Chứa thông tin sản phẩm
)