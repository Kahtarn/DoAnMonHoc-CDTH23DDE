package com.example.clientchodientu.entity

import java.math.BigDecimal

data class Product(
    val id: Int,
    val thumbnailUrl: String,
    val title: String,
    val description: String,
    val price: BigDecimal,
    val status: Int,
    val createAt: String,
    val seller: User,
    val category: Category
)
