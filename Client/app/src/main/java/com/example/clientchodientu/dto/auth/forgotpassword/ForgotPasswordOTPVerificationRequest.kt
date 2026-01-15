package com.example.chodientuapplication.dto.auth.forgotpassword

data class ForgotPasswordOTPVerificationRequest(
    val email: String,
    val otpCode: String
)