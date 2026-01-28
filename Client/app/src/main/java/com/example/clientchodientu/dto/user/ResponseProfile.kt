package com.example.clientchodientu.dto.user

import com.example.clientchodientu.entity.User

data class ResponseProfile(
    val success:Boolean,
    val message:String,
    val data: User
)
