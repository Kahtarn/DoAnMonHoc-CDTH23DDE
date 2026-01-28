package com.example.clientchodientu.dto.chat

data class LoginData(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val username: String,
    val fullName: String
)
