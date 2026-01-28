package com.example.clientchodientu.dto.product

import java.math.BigDecimal

data class EditPostRequest(
    val title: String,
    val price: BigDecimal,
    val description: String,
    val categoryId: Int
)
