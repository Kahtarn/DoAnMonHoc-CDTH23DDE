package com.example.clientchodientu.untils.token

import com.example.clientchodientu.entity.ProductDetailData

data class ApiResponse(
    val status: Int?,
    val message: String?,
    val data: ProductDetailData
)
