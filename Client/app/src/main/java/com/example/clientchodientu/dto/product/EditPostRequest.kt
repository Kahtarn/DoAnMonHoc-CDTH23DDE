package com.example.clientchodientu.dto.product

data class EditPostRequest(
    val id: Int,
    val title: String,
    val price: String,
    val description: String,
    val categoryId: Int,
    val images: List<Any>
)
