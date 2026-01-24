package com.example.clientchodientu.untils

data class ApiResponseData<T>(
    val success: Boolean,
    val message: String,
    val data: T
)
