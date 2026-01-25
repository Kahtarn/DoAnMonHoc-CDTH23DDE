package com.example.clientchodientu.dto.chat


data class ApiResponse(
    val status: Int?,
    val message: String?,
    val data: Map<String, Int>
)
