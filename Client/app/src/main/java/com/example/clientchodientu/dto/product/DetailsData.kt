package com.example.clientchodientu.dto.product

import com.example.clientchodientu.entity.Product

data class DetailsData(
    val product: Product,
    val images: List<String>
)
