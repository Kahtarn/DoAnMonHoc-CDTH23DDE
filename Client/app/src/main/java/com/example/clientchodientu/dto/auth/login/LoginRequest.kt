package com.example.clientchodientu.dto.auth.login

data class LoginRequest(
    val emailOrUsername : String,
    val password : String
)