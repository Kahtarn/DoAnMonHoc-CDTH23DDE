package com.example.clientchodientu.dto.auth.login

import com.example.clientchodientu.entity.User

data class LoginResponse(
    val success : Boolean,
    val message : String,
    val data : LoginData
)
