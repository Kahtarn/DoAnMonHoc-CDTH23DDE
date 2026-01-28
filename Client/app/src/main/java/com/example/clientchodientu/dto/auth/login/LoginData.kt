package com.example.clientchodientu.dto.auth.login

data class LoginData(
    val accessToken: String,
    val refreshToken: String,
    val firebaseToken: String,
    val userId: Int,
    val username: String,
    val fullName: String
)
