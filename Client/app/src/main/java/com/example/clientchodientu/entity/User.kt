package com.example.clientchodientu.entity

import java.time.LocalDateTime

data class User(
    val id: Int,
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val gender: Boolean,
    val provinceName: String,
    val wardName: String,
    val createAt: String,
    val avatarUrl: String
)
