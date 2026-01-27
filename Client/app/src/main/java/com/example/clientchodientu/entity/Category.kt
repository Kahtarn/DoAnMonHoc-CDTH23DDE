package com.example.clientchodientu.entity

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val name: String,
    @SerializedName("iconUrl")
    val iconUrl: String
)
