package com.example.chodientuapplication.dto.auth.login

data class LoginRequest(
    val emailOrUsername : String,
    val password : String
)