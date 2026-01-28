package com.example.clientchodientu.dto.product

import java.math.BigDecimal

data class PostResquest(
    val categoryId: Int,
    val title:String,
    val description:String,
    val price: BigDecimal,
    val thumbnailUrl:String,
    val imageUrl: List<String>
)