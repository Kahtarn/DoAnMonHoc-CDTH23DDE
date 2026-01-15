package com.example.chodientuapplication.dto.auth.register

data class RegisterRequest (
    val username: String,
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val gender: Boolean,
    val provinceName: String,
    val wardName: String,
    val otpCode: String
)