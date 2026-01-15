package com.example.chodientuapplication.dto.auth.forgotpassword

data class ConfirmPasswordRequest(
    val email: String,
    val otpCode: String,
    val newPassword: String
)
