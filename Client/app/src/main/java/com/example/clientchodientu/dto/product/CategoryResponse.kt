package com.example.clientchodientu.dto.product

import com.example.clientchodientu.entity.Category

data class CategoryResponse(
    val success: Boolean,
    val message: String,
    val data: List<Category>
)
