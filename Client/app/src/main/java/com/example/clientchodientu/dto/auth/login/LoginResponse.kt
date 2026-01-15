package com.example.chodientuapplication.dto.auth.login

import com.example.chodientuapplication.entity.User

data class LoginResponse(
    val success : Boolean,
    val message : String,
    val data : User
)
