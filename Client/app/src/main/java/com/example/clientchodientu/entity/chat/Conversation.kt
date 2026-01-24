package com.example.clientchodientu.entity.chat

data class Conversation (
    val id: Int,
    val userName: String,
    val lastMessage: String,
    val time: String,
    val isRevoked: Boolean = false, // Cờ kiểm tra xem tin cuối có bị thu hồi không
    val avatarUrl: String// Tạm thời dùng màu để giả lập avatar
)