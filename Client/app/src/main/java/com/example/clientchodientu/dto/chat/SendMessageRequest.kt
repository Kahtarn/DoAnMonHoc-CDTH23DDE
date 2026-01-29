package com.example.clientchodientu.dto.chat

data class SendMessageRequest(
    var id: String,
    var receiverId: Int,
    var content: String,
    var isRevoke: Boolean,
    var productId: Int?
)
