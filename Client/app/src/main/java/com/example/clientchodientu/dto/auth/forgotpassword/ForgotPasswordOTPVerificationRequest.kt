package com.example.clientchodientu.dto.auth.forgotpassword

data class ForgotPasswordOTPVerificationRequest(
    val email: String,
    val otpCode: String
)