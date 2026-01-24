package com.example.clientchodientu.entity.chat

data class ApiResponse (
    val success: Boolean,
    val message: String,
    val data: Any? = null
)