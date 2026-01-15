package com.example.chodientuapplication.entity

import java.time.LocalDateTime

data class User(
    val id : Int,
    val username : String,
    val email: String,
    val password : String,
    val fullName : String,
    val phone : String,
    val gender : Boolean,
    val avatarUrl : String,
    val refreshToken : String,
    val accessToken : String,
    val provinceCode : Int,
    val districtCode : Int,
    val wardCode : Int,
    val createAt : LocalDateTime
)
