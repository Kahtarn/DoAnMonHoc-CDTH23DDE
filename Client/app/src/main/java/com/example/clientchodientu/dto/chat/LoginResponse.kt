package com.example.clientchodientu.dto.chat

data class LoginResponse(
    val success : Boolean,
    val message : String,
    val data : LoginData
)
