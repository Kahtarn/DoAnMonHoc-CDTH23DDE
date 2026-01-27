package com.example.clientchodientu.dto.product

import com.example.clientchodientu.entity.Product

data class PostFavoriteResponse(
    val success : Boolean,
    val message : String,
    val data : List<Product>
)
