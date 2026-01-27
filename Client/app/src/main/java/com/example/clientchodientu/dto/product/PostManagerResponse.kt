package com.example.clientchodientu.dto.product

import com.example.clientchodientu.entity.Product

data class PostManagerResponse(
    val success : Boolean,
    val message : String,
    val data : List<Product>
)
